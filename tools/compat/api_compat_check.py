"""Static binary-compatibility check of a plugin jar against a server API jar.

For every method/field reference the plugin's bytecode makes into the API packages, verify
that on the target API:
  * the owner class exists,
  * the owner's kind matches the instruction (a Methodref on a type that became an interface,
    or an InterfaceMethodref on a type that became a class, throws IncompatibleClassChangeError),
  * the member exists with the same descriptor somewhere in the owner's hierarchy.

This catches what changes between Paper versions (enum -> interface conversions, removed or
renamed constants, changed signatures) without starting a server. It does not model Paper's
own load-time bytecode rewriting, so a reported problem means "relies on the server rewriting
it", which is exactly what GooP should not rely on.

Usage: api_compat_check.py <plugin.jar> <api.jar> [<extra api or dependency jar> ...]
Exit code 1 when problems are found.
"""
import struct, sys, zipfile
from collections import defaultdict

import os
API_PREFIXES = tuple(os.environ.get("API_PREFIXES", "org/bukkit/,io/papermc/,com/destroystokyo/").split(","))

class ClassInfo:
    __slots__ = ("name", "interface", "super", "interfaces", "fields", "methods")

def parse(data):
    """Returns (ClassInfo, refs) where refs = [(kind, owner, name, desc)]"""
    pos = 8
    count = struct.unpack_from(">H", data, pos)[0]; pos += 2
    cp = [None] * count
    i = 1
    while i < count:
        tag = data[pos]; pos += 1
        if tag == 1:
            ln = struct.unpack_from(">H", data, pos)[0]; pos += 2
            cp[i] = ("utf8", data[pos:pos + ln].decode("utf-8", "replace")); pos += ln
        elif tag in (3, 4):
            pos += 4
        elif tag in (5, 6):
            pos += 8; i += 1
        elif tag == 7:
            cp[i] = ("class", struct.unpack_from(">H", data, pos)[0]); pos += 2
        elif tag in (8, 16, 19, 20):
            pos += 2
        elif tag in (9, 10, 11):
            c, nt = struct.unpack_from(">HH", data, pos); pos += 4
            cp[i] = ({9: "field", 10: "method", 11: "imethod"}[tag], c, nt)
        elif tag == 12:
            cp[i] = ("nat",) + struct.unpack_from(">HH", data, pos); pos += 4
        elif tag == 15:
            pos += 3
        elif tag in (17, 18):
            pos += 4
        else:
            raise ValueError("bad constant tag %d" % tag)
        i += 1

    def utf(idx): return cp[idx][1]
    def cls(idx): return utf(cp[idx][1])

    access, this_c, super_c = struct.unpack_from(">HHH", data, pos); pos += 6
    info = ClassInfo()
    info.name = cls(this_c)
    info.interface = bool(access & 0x0200)
    info.super = cls(super_c) if super_c else None
    n = struct.unpack_from(">H", data, pos)[0]; pos += 2
    info.interfaces = [cls(struct.unpack_from(">H", data, pos + 2 * k)[0]) for k in range(n)]; pos += 2 * n

    def members():
        nonlocal pos
        out = set()
        n = struct.unpack_from(">H", data, pos)[0]; pos += 2
        for _ in range(n):
            _, name, desc, attrs = struct.unpack_from(">HHHH", data, pos); pos += 8
            out.add((utf(name), utf(desc)))
            for _ in range(attrs):
                ln = struct.unpack_from(">I", data, pos + 2)[0]; pos += 6 + ln
        return out
    info.fields = members()
    info.methods = members()

    refs = []
    for e in cp:
        if e and e[0] in ("field", "method", "imethod"):
            nat = cp[e[2]]
            refs.append((e[0], cls(e[1]), utf(nat[1]), utf(nat[2])))
    return info, refs

def load(jar):
    classes = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and not n.startswith("META-INF/"):
                try:
                    info, _ = parse(z.read(n))
                    classes[info.name] = info
                except Exception:
                    pass
    return classes

OBJECT_METHODS = {("equals", "(Ljava/lang/Object;)Z"), ("hashCode", "()I"), ("toString", "()Ljava/lang/String;"),
                  ("getClass", "()Ljava/lang/Class;"), ("clone", "()Ljava/lang/Object;"), ("notify", "()V"),
                  ("notifyAll", "()V"), ("wait", "()V"), ("wait", "(J)V"), ("finalize", "()V"),
                  # java.lang.Enum, for enums in the API
                  ("name", "()Ljava/lang/String;"), ("ordinal", "()I"), ("compareTo", "(Ljava/lang/Enum;)I"),
                  ("getDeclaringClass", "()Ljava/lang/Class;")}

def find_member(api, owner, name, desc, is_field, seen=None):
    top = seen is None
    seen = seen or set()
    if owner in seen: return None
    seen.add(owner)
    c = api.get(owner)
    if c is None:
        # JDK types are not in the jars. A direct reference to one is assumed fine; reaching one as a
        # superclass only counts for Object's own methods, so a member missing from the API class
        # above it is still reported.
        if not owner.startswith("java/"): return None
        if top: return True
        return True if (not is_field and (name, desc) in OBJECT_METHODS) else None
    pool = c.fields if is_field else c.methods
    if (name, desc) in pool: return True
    # signature-polymorphic and inherited lookups
    for parent in ([c.super] if c.super else []) + c.interfaces:
        if find_member(api, parent, name, desc, is_field, seen): return True
    return None

def main():
    plugin, apis = sys.argv[1], sys.argv[2:]
    api = {}
    for j in apis: api.update(load(j))
    problems = defaultdict(set)
    own = load(plugin)          # the plugin's own classes, for members inherited from API classes
    merged = dict(api); merged.update(own)

    def reaches_api(owner):
        """If the owner's superclass chain runs into a checked API class, with every class on the
        way known (a class from a jar that was not supplied makes the result unknowable: False)"""
        hit = False
        while owner and not owner.startswith("java/"):
            if owner.startswith(API_PREFIXES): hit = True
            c = merged.get(owner)
            if c is None: return False
            owner = c.super
        return hit

    with zipfile.ZipFile(plugin) as z:
        for n in z.namelist():
            if not n.endswith(".class"): continue
            try:
                info, refs = parse(z.read(n))
            except Exception:
                continue
            for kind, owner, name, desc in refs:
                if owner in own and not owner.startswith(API_PREFIXES):
                    # Call on a plugin class: only interesting when the member is inherited from the API
                    if reaches_api(owner) and not find_member(merged, owner, name, desc, kind == "field"):
                        problems["missing inherited %s %s.%s %s" % ("field" if kind == "field" else "method", owner, name, desc)].add(info.name)
                    continue
                if not owner.startswith(API_PREFIXES): continue
                c = api.get(owner)
                where = info.name
                if c is None:
                    problems["missing class " + owner].add(where); continue
                if kind == "method" and c.interface:
                    problems["class-call on interface %s.%s%s" % (owner, name, desc)].add(where)
                if kind == "imethod" and not c.interface:
                    problems["interface-call on class %s.%s%s" % (owner, name, desc)].add(where)
                if name in ("<init>", "<clinit>") and kind != "method": continue
                if not find_member(api, owner, name, desc, kind == "field"):
                    problems["missing %s %s.%s %s" % ("field" if kind == "field" else "method", owner, name, desc)].add(where)
    for p in sorted(problems):
        users = sorted(problems[p])
        print("%s\n    used by: %s%s" % (p, ", ".join(u.rsplit("/", 1)[-1] for u in users[:6]), " (+%d)" % (len(users) - 6) if len(users) > 6 else ""))
    print("problems: %d" % len(problems))
    sys.exit(1 if problems else 0)

if __name__ == "__main__":
    main()
