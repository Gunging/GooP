# Changelog

## 2.0.0 (unreleased)

### Compatibility
- Two editions built from one source: `modern` for Paper/Purpur 1.21.1 – 26.x and `legacy`
  for 1.20.1 – 1.20.6.
- Builds against MMOItems 6.10.1, MythicLib 1.7.1, MMOCore 1.13.1, MythicMobs 5.10 (legacy) /
  5.14 (modern) and mcMMO 2.x, all from public Maven repositories.
- Minecraft version detection understands 1.21.10+ and the year-based 26.x releases.
- Materials, entity types, enchantments, attributes and potion effects that were renamed
  between versions are resolved by name at runtime, so old names in configs keep working.

### Fixes
- Accessory/equipment slots in Personal Containers apply their MMOItems stats again on
  MMOItems 6.10.1 (the inventory API GooP used was removed).
- No more `ClassCastException: HashSet cannot be cast to List` in the console on every hit.
- No more `Player data not loaded` errors for players whose MMOItems data is still loading.
- `/goop consumeitem`, `testinventory` and other item filters match the whole name:
  `v ICE` no longer matches `MELON_SLICE`, `SWORD_1` no longer matches `SWORD_10`.
- Attribute lookups work on 1.20.6 – 1.21.2 (they threw `NoSuchFieldError`).
- Config files are saved without racing the main thread; save errors are logged.
- The `distance` player placeholder returns -1 instead of throwing when the player is in
  another world.
- Player head textures use Paper's profile API.

### Changed
- Item name filters without `*` must match exactly. Write `*ICE*` where the old
  "contains" behaviour was intended.
- MythicMobs older than 5.10 is no longer supported (custom mechanics use the 5.2+ API only).
- mcMMO placeholders require mcMMO 2.x.
- MMOItem Shrubs, Graveyards and Towny are reached without compile-time dependencies; if a
  version of them changes shape, only that integration is disabled.
