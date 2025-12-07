---
id: biomancy:injector
type: item
---

# Injector

A simple device which utilizes a razor sharp needle to quickly and forcefully inject Serums into Mobs and Players.

- Can be used by a dispenser
- has a Radial Menu to select which Serum to equip
- Inventory: 1 Slot  (max stack size: 16)
- Deals 0.5 damage on injection
- [](@biomancy:anesthetic_touch) prevents the damage

<PrefabObtaining />

## Injection Chance

$$InjectionChance = DamagePenetrationPct + 0.075 * PierceLevel$$

- Checks if it can pierce through the armor (attribute) of its victim before applying the serum
- [Piercing Enchantment](@minecraft:piercing) increases chance to penetrate the armor
- When the injection fails the needle breaks (sound & particles) and the item will be on cooldown and the needle regrows (animation)
