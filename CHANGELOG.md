## [8.31.0-snapshot.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.8.30.0-snapshot.0...1.20.1-v2.8.31.0-snapshot.0) (2025-09-14)


### Features

* add new biomancy keybinds and fix support for modifier keys ([1d65a95](https://github.com/Elenterius/Biomancy/commit/1d65a9510610694f9812eb50f61a3eef25ca6219))
* **kubejs-plugin:** rework onCradleSpawnCustomMob event into onCradleSpawnMob event which provides the original mob the cradle wanted to spawn ([fc526c4](https://github.com/Elenterius/Biomancy/commit/fc526c41bfb1f93f14c4ebff1464b7144b448c4e))
* **recipes:** make any crops ([#forge](https://github.com/Elenterius/Biomancy/issues/forge):crops), AlexMobs banana & peel and more AlexCaves plants decomposable ([7d11057](https://github.com/Elenterius/Biomancy/commit/7d110573cb891db299b5df65a1a7aa87018e8c5a))
* **warrior-armor:** make consecutive bullet jumps more expensive (10/15/25/40) and reset the cost after 5 seconds of no bullet jump use ([0948232](https://github.com/Elenterius/Biomancy/commit/09482328cb1805a55c1f1c09a201119e5606aede))
* **warrior-armor:** reduce attack damage bonus from 20% to 10% ([28e3313](https://github.com/Elenterius/Biomancy/commit/28e33139da802bcbf3c609073f65ccb2d9d13b51))
* **warrior-armor:** reduce attack speed penalty from -20% to -10% ([ea4196e](https://github.com/Elenterius/Biomancy/commit/ea4196e953973318fde95e462008a5f711ae079d))
* **warrior-armor:** remove jump boost buff and rename leap ability to bullet jump ([a3180c2](https://github.com/Elenterius/Biomancy/commit/a3180c2e5a485af7950218719ce97dd93be3b56d))
* **warrior-armor:** replace passive ability 'Imposing Aura' with active ability 'Imposing Roar' ([33f6016](https://github.com/Elenterius/Biomancy/commit/33f6016341f6ec2f13631b29fc958aadc4860841))

## [8.30.0-snapshot.0](https://github.com/Elenterius/Biomancy/compare/1.20.1-v2.8.29.1-snapshot.0...1.20.1-v2.8.30.0-snapshot.0) (2025-09-10)


### Features

* add events for spawning custom mobs with the cradle and add KubeJS integration for it ([7491b14](https://github.com/Elenterius/Biomancy/commit/7491b14638ca89989b2b18af11f6ee55bf107878))
* add KubeJS integration with biomancy recipes and add Essence helpers ([31dde22](https://github.com/Elenterius/Biomancy/commit/31dde22b009e668723782525cddaa4b06f88d824))
* add KubeJS integration with biomancy serums, bio-forge tabs, nutrients fuel/repair value and cradle tributes ([667373b](https://github.com/Elenterius/Biomancy/commit/667373be460eb1e04f12bca12c47acd19bb94c26))
* **assets:** update alchemist armor icon textures ([338faae](https://github.com/Elenterius/Biomancy/commit/338faae44fc47dd075f1b9f2a9c1d316da953d98))
* **assets:** update impaler icon texture ([52d8f6b](https://github.com/Elenterius/Biomancy/commit/52d8f6b1e1d4c317c986898a8295836b1bef5399))
* make flesh blobs launch upwards when spawned from a cradle ([c60ec4f](https://github.com/Elenterius/Biomancy/commit/c60ec4f445034f6e91e66669a62cb98a32d1034c))
* **mod-compat:** add custom tooltip frames (by Jasdan) for the tooltip overhaul mod ([53235f1](https://github.com/Elenterius/Biomancy/commit/53235f1f8678c6c0d3f8816f518d5151692149b1))
* **spatial-db:** refactor spatial database by adding automatic live backups and corruption recovery from backups on world startup ([8743f5e](https://github.com/Elenterius/Biomancy/commit/8743f5e74c73349b7dc2bd5cb46702e793602f78))
* tweak tooltip styles ([13040dd](https://github.com/Elenterius/Biomancy/commit/13040dd24cdb24f93946090306fae4baf579ca4e))
* update Chinese translation ([25a2b53](https://github.com/Elenterius/Biomancy/commit/25a2b53417aa2c7fd7cc586379bcb2a6c4ac5e6f))
* update tooltip frames for the tooltip overhaul mod ([f4c5d58](https://github.com/Elenterius/Biomancy/commit/f4c5d58fa9c4f12081cf5c6d8781d541ddf2665a))


### Bug Fixes

* fix creative tab order of Biomancy tabs ([0d61452](https://github.com/Elenterius/Biomancy/commit/0d61452fd8158044bf080d1b7d10bcafe0736fec))
* fix EssenceIngredients not displaying their tier ([2717ee7](https://github.com/Elenterius/Biomancy/commit/2717ee7a329d8111f778e241267fb56f5b798aa5))
* fix mixin incompatibility with iron spells mod ([bd57e1e](https://github.com/Elenterius/Biomancy/commit/bd57e1e79988cfeaa109cbb1881463d56abf1a99)), closes [#173](https://github.com/Elenterius/Biomancy/issues/173)
* fix order of item tags ([42e7a6d](https://github.com/Elenterius/Biomancy/commit/42e7a6dea3e28f17496c128e86bbc057babfb55e))
* fix shield retraction animation playing on first use ([a2208e8](https://github.com/Elenterius/Biomancy/commit/a2208e806a562509c3f53bfd3362fef28f5c4ec7))

