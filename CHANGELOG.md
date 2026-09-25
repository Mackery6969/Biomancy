# Changelog

## [2.9.8.7] - 2026-09-24

### Fixed

- Fixed the primal energy supply config of the Primordial Cradle being ignored, where the flesh veins created charge out of nothing and kept spreading forever after a single feeding, even when set to limited ([#20](https://github.com/Mackery6969/Biomancy/issues/20)).
- Fixed a crash when the world saved while a mined Primordial Cradle, Bio-Forge, Digester or Decomposer was in the inventory, which also deleted the item after relogging ([#19](https://github.com/Mackery6969/Biomancy/issues/19)).

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

[2.9.8.6]: https://github.com/Mackery6969/Biomancy/releases/tag/v2.9.8.6
[2.9.8.5]: https://github.com/Mackery6969/Biomancy/releases/tag/v2.9.8.5
[2.9.8.4]: https://github.com/Mackery6969/Biomancy/releases/tag/v2.9.8.4
[2.9.8.3]: https://github.com/Mackery6969/Biomancy/releases/tag/v2.9.8.3
[2.9.8.2]: https://github.com/Mackery6969/Biomancy/releases/tag/v2.9.8.2
[2.9.8.1]: https://github.com/Mackery6969/Biomancy/releases/tag/v2.9.8.1
