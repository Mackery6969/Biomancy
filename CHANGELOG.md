# Changelog

## [2.9.8.7] - 2026-09-22

### Added

Experimental features are now available in the Experiments screen when creating a world. These features are not fully tested and may be buggy, but they can be enabled for players who want to try them out.

- Added the **Primordial Hivemind** experiment, selectable in the Experiments screen when creating a world. It turns the Primordial Cradle into a thinking organism:
- The Cradle burns primal energy over time instead of hoarding it, and picks a growth target it steers its flesh toward rather than spreading in every direction.
- Flesh Blobs hunt animals for the hive, carry the biomass back to the Cradle, and feed it directly. Killing a gorged blob before it gets home denies the hive the meal.
- Flesh Blobs defend the Cradle from whoever attacks it, and fight back when attacked themselves.
- A Cradle that starves goes dormant and its flesh recedes. A Cradle that has never been fed stays inert instead, so worldgen structures keep the flesh they generated with.
- The Cradle reacts to fire, lava and explosions damaging its flesh, and decides what to do about it: a well fed hive grows into the hazard and thickens its flesh into solid blocks to smother it, while a weak hive recoils and grows away from it instead. Whoever lit the fire or set off the explosion is remembered as a threat.
- Flesh with no Cradle spreads faster and more aggressively than normal, but has nothing sustaining it and dies out once its charge is spent.
- Flesh Blobs keep a share of every kill as their own energy and hand the rest to the Cradle, so they still function alone. A blob bonded to a living hive is stronger, gaining attack damage, movement speed and armour, and spends its own energy to heal itself.
- Flesh Blobs that end up walled in by the hive's own flesh eat their way back out, absorbing the flesh as energy. A blob holding biomass for a hive that no longer exists absorbs it instead of carrying it forever.

## [2.9.8.6] - 2026-09-21

### Fixed

- Fixed the Primordial Cradle from consuming the wrong items, including non food items, when attempting to feed it.
- Fixed the structures from not generating properly in the world, they were locatable but not actually generating.

### Changed

- Changed the Bio-Forge to no longer rely on advancements to unlock recipes. Crafting a recipe now unlocks it directly, the same way vanilla's stonecutter and smithing table do, so recipes still unlock on worlds with advancements disabled.
- Changed `doBioForgeRecipeProgression` to default to `false`, matching vanilla's `doLimitedCrafting` game rule. Existing configs keep their current value.

## [2.9.8.5] - 2026-09-19

### Fixed

- Fixed the absorption serum from not actually giving any absorption hearts when used.
- Fixed the frenzy serum from causing the UI to not display properly when used.

### Changed

- Changed it so the growth and shrinking serums will not be obtainable unless Pehkui is installed, as Pekhui is required for the serums to work properly.

### Added

- Added more interaction with the Sable sublevels, allowing for spreading on a sublevel to branch out to nearby blocks and other sublevels.

## [2.9.8.4] - 2026-09-19

### Fixed

- Fixed a crash related to how Lithium handled fluids ([#7](https://github.com/Mackery6969/Biomancy/issues/7)).
- Fixed the durability meter of the gunblade to properly display ([#6](https://github.com/Mackery6969/Biomancy/issues/6)).
- Fixed a crash related to the flesh veins.

### Added

- Added better debugging for recipes that fail to load.

## [2.9.8.3] - 2026-09-18

### Fixed

- Fixed the primordial cradle not being able to replace any blocks or spread to any blocks in the world.

## [2.9.8.2] - 2026-09-17

### Added

- Added support for Bio-Factory; a create mod addon.

## [2.9.8.1] - 2026-09-08

_Initial release._

[2.9.8.7]: https://github.com/Mackery6969/Biomancy/releases/tag/v2.9.8.7
[2.9.8.6]: https://github.com/Mackery6969/Biomancy/releases/tag/v2.9.8.6
[2.9.8.5]: https://github.com/Mackery6969/Biomancy/releases/tag/v2.9.8.5
[2.9.8.4]: https://github.com/Mackery6969/Biomancy/releases/tag/v2.9.8.4
[2.9.8.3]: https://github.com/Mackery6969/Biomancy/releases/tag/v2.9.8.3
[2.9.8.2]: https://github.com/Mackery6969/Biomancy/releases/tag/v2.9.8.2
[2.9.8.1]: https://github.com/Mackery6969/Biomancy/releases/tag/v2.9.8.1
