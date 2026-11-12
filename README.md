# Gunging Ootilities Plugin (GooP) 2

GooP is a toolbox plugin for RPG servers: the `/goop` command language, Containers (personal
backpacks and accessory bags, physical chests, crafting stations), Custom Structures,
MMOItems conversion/reforging/OnApply, Summoner minions, and extra MythicMobs mechanics,
conditions, targeters and placeholders.

This branch modernises GooP 1.x for current servers and current MMO/Mythic plugins. It keeps
the plugin name, data folder, configuration files and command syntax of GooP 1.x, so it can
replace an existing install.

## Editions

Two jars are built from the same source. Pick the one for your server version.

| Jar | Server | Java | MythicMobs |
|---|---|---|---|
| `Gunging_Ootilities_Plugin-<version>-modern.jar` | Paper / Purpur 1.21.1 – 26.x | 21+ | 5.12 – 5.14 |
| `Gunging_Ootilities_Plugin-<version>-legacy.jar` | Paper / Purpur 1.20.1 – 1.20.6 | 17+ | 5.9.5 (5.10 on 1.20.6 only) |

Both editions are built against MythicLib 1.7.1, MMOItems 6.10.1 and MMOCore 1.13.1.
Spigot and Folia are not supported.

Known limits of the plugins themselves, not GooP: MMOItems 6.10.1 reforging (`/goop mmoitems
regenerate`) fails on Paper 1.20.1 – 1.20.4 because MMOItems calls an API added in 1.20.5, and
MythicMobs 5.10+ does not start on Paper 1.20.1 – 1.20.4.

Optional integrations, enabled when the plugin is installed: MythicMobs, MythicLib, MMOItems,
MMOCore, MythicCrucible, WorldGuard, PlaceholderAPI, Vault, mcMMO 2.x, Towny, GriefPrevention,
DiscordSRV, CMI, Essentials, Graveyards.

## Building

Requires JDK 25 (it compiles the legacy edition with `--release 17` and the modern one with
`--release 21`) and Maven 3.9+.

```sh
mvn -B verify                      # build, unit tests; jars are written to dist/
sh tools/compat/check-matrix.sh    # check both jars against every supported Paper and MythicMobs API
```

`check-matrix.sh` reads the compiled bytecode and verifies that every Bukkit, Paper,
MythicMobs and MMO method or field GooP uses exists, with the same class/interface kind, on
each supported version, without starting a server.

## Layout

| Path | Contents |
|---|---|
| `goop-common/` | The plugin source and resources, shared by both editions |
| `goop-core/` | Helpers with no Minecraft dependency (version parsing, name matching), unit tested |
| `editions/legacy`, `editions/modern` | Build definitions that compile `goop-common` against each API generation |
| `tools/compat/` | The API compatibility checker |

## License

GooP is released under CC0 1.0 (see `LICENSE`). Original author: Gunging.
