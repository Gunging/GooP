#!/bin/sh
# Checks both GooP jars against every server API and MythicMobs version they claim to support,
# without starting a server: every Bukkit/Paper/Mythic member GooP calls must exist, with the
# same class/interface kind, on each target. Run from the repository root after `mvn package`.
#
#   sh tools/compat/check-matrix.sh
#
# Jars are downloaded into tools/compat/.cache (ignored by git).
set -eu

HERE=$(cd "$(dirname "$0")" && pwd)
CACHE="$HERE/.cache"
DIST="$HERE/../../dist"
PAPER=https://repo.papermc.io/repository/maven-public/io/papermc/paper/paper-api
LUMINE=https://mvn.lumine.io/repository/maven-public/io/lumine/Mythic-Dist
PHOENIX=https://nexus.phoenixdevt.fr/repository/maven-public
mkdir -p "$CACHE"

LEGACY_JAR=$(ls "$DIST"/*-legacy.jar | head -1)
MODERN_JAR=$(ls "$DIST"/*-modern.jar | head -1)

# Paper API jar for a version: x.y.z for the 1.x SNAPSHOT line, x.y[.z] for the year-based line
paper_api() {
  v=$1; f="$CACHE/paper-api-$v.jar"
  if [ ! -s "$f" ]; then
    case $v in
      1.*) ts=$(curl -fsS "$PAPER/$v-R0.1-SNAPSHOT/maven-metadata.xml" | grep -o '<value>[^<]*' | head -1 | cut -c8-)
           curl -fsS -o "$f" "$PAPER/$v-R0.1-SNAPSHOT/paper-api-$ts.jar" ;;
      *)   b=$(curl -fsS "$PAPER/maven-metadata.xml" | grep -o "<version>$v\.build\.[0-9]*-stable" | sed 's/<version>//' | sort -t. -k4 -n | tail -1)
           curl -fsS -o "$f" "$PAPER/$b/paper-api-$b.jar" ;;
    esac
  fi
  echo "$f"
}

mythic() {
  f="$CACHE/Mythic-Dist-$1.jar"
  [ -s "$f" ] || curl -fsS -o "$f" "$LUMINE/$1/Mythic-Dist-$1.jar"
  echo "$f"
}

# Latest timestamped SNAPSHOT of a Phoenix artifact: snapshot <group/path> <artifact> <version>
snapshot() {
  f="$CACHE/$2-$3.jar"
  if [ ! -s "$f" ]; then
    ts=$(curl -fsS "$PHOENIX/$1/$2/$3/maven-metadata.xml" | grep -o '<value>[^<]*' | head -1 | cut -c8-)
    curl -fsS -o "$f" "$PHOENIX/$1/$2/$3/$2-$ts.jar"
  fi
  echo "$f"
}

MYTHICLIB=$(snapshot io/lumine MythicLib-dist 1.7.1-SNAPSHOT)
MMOITEMS=$(snapshot net/Indyuce MMOItems-API 6.10.1-SNAPSHOT)
MMOCORE=$(snapshot net/Indyuce MMOCore-API 1.13.1-SNAPSHOT)

failed=0
check() { # label jar api-jars...
  label=$1; jar=$2; shift 2
  if out=$(python3 "$HERE/api_compat_check.py" "$jar" "$@"); then
    echo "ok    $label"
  else
    echo "FAIL  $label"; echo "$out" | sed 's/^/      /'; failed=1
  fi
}

echo "== Server API"
for v in 1.20.1 1.20.4 1.20.6; do check "legacy on Paper $v" "$LEGACY_JAR" "$(paper_api $v)"; done
for v in 1.21.1 1.21.4 1.21.8 1.21.11 26.1.2 26.2 26.3; do check "modern on Paper $v" "$MODERN_JAR" "$(paper_api $v)"; done

echo "== MythicMobs / MMO API"
export API_PREFIXES="io/lumine/mythic/,net/Indyuce/"
check "legacy with MythicMobs 5.11.0" "$LEGACY_JAR" "$(mythic 5.11.0)" "$MYTHICLIB" "$MMOITEMS" "$MMOCORE" "$(paper_api 1.20.6)"
for m in 5.12.1 5.13.0 5.14.0; do
  check "modern with MythicMobs $m" "$MODERN_JAR" "$(mythic $m)" "$MYTHICLIB" "$MMOITEMS" "$MMOCORE" "$(paper_api 26.3)"
done

exit $failed
