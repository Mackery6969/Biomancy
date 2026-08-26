package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.item.*;
import com.github.elenterius.biomancy.item.armor.AcolyteArmorItem;
import com.github.elenterius.biomancy.item.armor.WarriorArmorItem;
import com.github.elenterius.biomancy.item.extractor.ExtractorItem;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import com.github.elenterius.biomancy.item.shield.ThornShieldItem;
import com.github.elenterius.biomancy.item.weapon.DespoilingSwordItem;
import com.github.elenterius.biomancy.item.weapon.GrenadeItem;
import com.github.elenterius.biomancy.item.weapon.RavenousClawsItem;
import com.github.elenterius.biomancy.item.weapon.gun.CausticGunbladeItem;
import com.github.elenterius.biomancy.item.weapon.gun.DevArmCannonItem;
import com.github.elenterius.biomancy.item.weapon.gun.ImpalerItem;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class ModItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, BiomancyMod.MOD_ID);
	public static final DeferredRegister<Item> DEV_ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, BiomancyMod.MOD_ID);

	//# Material / Mob Loot
	public static final DeferredHolder<Item, SimpleItem> MOB_FANG = registerSimpleItem("mob_fang");
	public static final DeferredHolder<Item, SimpleItem> MOB_CLAW = registerSimpleItem("mob_claw");
	public static final DeferredHolder<Item, SimpleItem> MOB_SINEW = registerSimpleItem("mob_sinew", ModRarities.UNCOMMON);
	public static final DeferredHolder<Item, BoneMarrowItem> MOB_MARROW = registerItem("mob_marrow", props -> new BoneMarrowItem(props.food(ModFoods.MARROW_FLUID).rarity(ModRarities.RARE)));
	public static final DeferredHolder<Item, BoneMarrowItem> WITHERED_MOB_MARROW = registerItem("withered_mob_marrow", props -> new BoneMarrowItem(props.food(ModFoods.CORROSIVE_FLUID).rarity(ModRarities.VERY_RARE)));
	public static final DeferredHolder<Item, SimpleItem> GENERIC_MOB_GLAND = registerItem("mob_gland", props -> new SimpleItem(props.food(ModFoods.POOR_FLESH).rarity(ModRarities.UNCOMMON)));
	public static final DeferredHolder<Item, SimpleItem> TOXIN_GLAND = registerItem("toxin_gland", props -> new SimpleItem(props.food(ModFoods.TOXIN_GLAND).rarity(ModRarities.RARE)));
	public static final DeferredHolder<Item, VolatileGlandItem> VOLATILE_GLAND = registerItem("volatile_gland", props -> new VolatileGlandItem(props.food(ModFoods.VOLATILE_GLAND).rarity(ModRarities.RARE)));
	public static final DeferredHolder<Item, AcidicEggItem> ACIDIC_EGG = registerItem("acidic_egg", AcidicEggItem::new);

	//# Complex Components
	public static final DeferredHolder<Item, SimpleItem> FLESH_BITS = registerSimpleItem("flesh_bits");
	public static final DeferredHolder<Item, SimpleItem> BONE_FRAGMENTS = registerSimpleItem("bone_fragments");
	public static final DeferredHolder<Item, SimpleItem> TOUGH_FIBERS = registerSimpleItem("tough_fibers");
	public static final DeferredHolder<Item, SimpleItem> ELASTIC_FIBERS = registerSimpleItem("elastic_fibers");
	public static final DeferredHolder<Item, SimpleItem> MINERAL_FRAGMENT = registerSimpleItem("mineral_fragment");
	public static final DeferredHolder<Item, SimpleItem> GEM_FRAGMENTS = registerSimpleItem("gem_fragments");

	//# Basic Components
	public static final DeferredHolder<Item, SimpleItem> NUTRIENTS = registerSimpleItem("nutrients");
	public static final DeferredHolder<Item, SimpleItem> ORGANIC_MATTER = registerSimpleItem("organic_matter");
	public static final DeferredHolder<Item, BioluminescentGooItem> BIO_LUMENS = registerItem("bio_lumens", BioluminescentGooItem::new);
	public static final DeferredHolder<Item, SimpleItem> EXOTIC_DUST = registerSimpleItem("exotic_dust");
	public static final DeferredHolder<Item, SimpleItem> STONE_POWDER = registerSimpleItem("stone_powder");

	//# Specific Components
	public static final DeferredHolder<Item, SimpleItem> REGENERATIVE_FLUID = registerSimpleItem("regenerative_fluid");
	public static final DeferredHolder<Item, SimpleItem> WITHERING_OOZE = registerSimpleItem("withering_ooze");
	public static final DeferredHolder<Item, SimpleItem> HORMONE_SECRETION = registerSimpleItem("hormone_secretion");
	public static final DeferredHolder<Item, SimpleItem> TOXIN_EXTRACT = registerSimpleItem("toxin_extract");
	public static final DeferredHolder<Item, SimpleItem> ACID_EXTRACT = registerItem("acid_extract", properties -> new SimpleItem(properties.food(ModFoods.GASTRIC_JUICE)));
	public static final DeferredHolder<Item, SimpleItem> BILE = registerSimpleItem("bile");
	public static final DeferredHolder<Item, SimpleItem> VOLATILE_FLUID = registerSimpleItem("volatile_fluid");

	//# Serum
	public static final DeferredHolder<Item, SimpleItem> VIAL = registerSimpleItem("vial");
	public static final DeferredHolder<Item, SimpleItem> ORGANIC_COMPOUND = registerSimpleVialItem("organic_compound");
	public static final DeferredHolder<Item, UnstableCompoundItem> UNSTABLE_COMPOUND = registerItem("unstable_compound", UnstableCompoundItem::new);
	public static final DeferredHolder<Item, SimpleItem> GENETIC_COMPOUND = registerSimpleVialItem("genetic_compound");
	public static final DeferredHolder<Item, SimpleItem> EXOTIC_COMPOUND = registerSimpleVialItem("exotic_compound");
	public static final DeferredHolder<Item, SimpleItem> HEALING_ADDITIVE = registerSimpleVialItem("healing_additive");
	public static final DeferredHolder<Item, SimpleItem> DECAYING_ADDITIVE = registerSimpleVialItem("decaying_additive");
	public static final DeferredHolder<Item, SerumItem> REJUVENATION_SERUM = registerSerumItem(ModSerums.REJUVENATION_SERUM);
	public static final DeferredHolder<Item, SerumItem> AGEING_SERUM = registerSerumItem(ModSerums.AGEING_SERUM);
	public static final DeferredHolder<Item, SerumItem> ENLARGEMENT_SERUM = registerSerumItem(ModSerums.ENLARGEMENT_SERUM);
	public static final DeferredHolder<Item, SerumItem> SHRINKING_SERUM = registerSerumItem(ModSerums.SHRINKING_SERUM);
	public static final DeferredHolder<Item, SerumItem> BREEDING_STIMULANT = registerSerumItem(ModSerums.BREEDING_STIMULANT);
	public static final DeferredHolder<Item, SerumItem> ABSORPTION_BOOST = registerSerumItem(ModSerums.ABSORPTION_BOOST);
	public static final DeferredHolder<Item, SerumItem> CLEANSING_SERUM = registerSerumItem(ModSerums.CLEANSING_SERUM);
	public static final DeferredHolder<Item, SerumItem> INSOMNIA_CURE = registerSerumItem(ModSerums.INSOMNIA_CURE);
	public static final DeferredHolder<Item, SerumItem> FRENZY_SERUM = registerSerumItem(ModSerums.FRENZY_SERUM);
	public static final DeferredHolder<Item, PotionSerumItem> POTION_SERUM = registerItem("potion_serum", props -> new PotionSerumItem(props.stacksTo(16).rarity(ModRarities.UNCOMMON)));

	//## Special
	public static final DeferredHolder<Item, SimpleItem> PRIMORDIAL_CORE = registerSimpleItem("primordial_core", ModRarities.VERY_RARE);
	public static final DeferredHolder<Item, SimpleItem> LIVING_FLESH = registerItem("living_flesh", props -> new SimpleItem(props.food(ModFoods.LIVING_FLESH).rarity(ModRarities.VERY_RARE)));
	public static final DeferredHolder<Item, EssenceItem> ESSENCE = registerItem("essence", EssenceItem::new);

	//# Tools
	public static final DeferredHolder<Item, DespoilingSwordItem> DESPOIL_SICKLE = registerItem("despoil_sickle", props -> SwordSmithy.forge(DespoilingSwordItem::new, ModTiers.PRIMAL_FLESH, 10, 1, props.rarity(ModRarities.VERY_RARE)));
	public static final DeferredHolder<Item, ExtractorItem> ESSENCE_EXTRACTOR = registerItem("extractor", props -> new ExtractorItem(props.durability(200).rarity(ModRarities.RARE)));
	public static final DeferredHolder<Item, InjectorItem> INJECTOR = registerItem("injector", props -> new InjectorItem(props.durability(200).rarity(ModRarities.RARE)));
	public static final DeferredHolder<Item, RavenousClawsItem> RAVENOUS_CLAWS = registerItem("ravenous_claws", props -> new RavenousClawsItem(ModTiers.BIOFLESH, 4f, 3.5f, 250, props.rarity(ModRarities.VERY_RARE)));
	public static final DeferredHolder<Item, CausticGunbladeItem> CAUSTIC_GUNBLADE = registerItem("caustic_gunblade", props -> new CausticGunbladeItem(200, props.stacksTo(1).rarity(ModRarities.VERY_RARE)));
	public static final DeferredHolder<Item, ImpalerItem> IMPALER = registerItem("impaler", props -> new ImpalerItem(200, props.stacksTo(1).rarity(ModRarities.ULTRA_RARE)));
	public static final DeferredHolder<Item, SimpleItem> GRENADE_CASING = registerSimpleItem("grenade_casing");
	public static final DeferredHolder<Item, GrenadeItem> TOXIN_GRENADE = registerItem("toxin_grenade", GrenadeItem::new);
	public static final DeferredHolder<Item, GrenadeItem> ACID_GRENADE = registerItem("acid_grenade", GrenadeItem::new);
	public static final DeferredHolder<Item, GrenadeItem> DECAY_GRENADE = registerItem("decay_grenade", GrenadeItem::new);
	public static final DeferredHolder<Item, GrenadeItem> INCENDIARY_GRENADE = registerItem("incendiary_grenade", GrenadeItem::new);

	//# Shield
	public static final DeferredHolder<Item, ThornShieldItem> THORN_SHIELD = registerItem("thorn_shield", props -> new ThornShieldItem(250, props.stacksTo(1).rarity(ModRarities.VERY_RARE)));

	//# Armor
	public static final DeferredHolder<Item, AcolyteArmorItem> ACOLYTE_ARMOR_HELMET = registerLivingArmorHelmet("acolyte_armor", ModArmorMaterials.ACOLYTE, 200, AcolyteArmorItem::new);
	public static final DeferredHolder<Item, AcolyteArmorItem> ACOLYTE_ARMOR_CHESTPLATE = registerLivingArmorChestplate("acolyte_armor", ModArmorMaterials.ACOLYTE, 250, AcolyteArmorItem::new);
	public static final DeferredHolder<Item, AcolyteArmorItem> ACOLYTE_ARMOR_LEGGINGS = registerLivingArmorLeggings("acolyte_armor", ModArmorMaterials.ACOLYTE, 250, AcolyteArmorItem::new);
	public static final DeferredHolder<Item, AcolyteArmorItem> ACOLYTE_ARMOR_BOOTS = registerLivingArmorBoots("acolyte_armor", ModArmorMaterials.ACOLYTE, 200, AcolyteArmorItem::new);
	public static final DeferredHolder<Item, WarriorArmorItem> WARRIOR_ARMOR_HELMET = registerLivingArmorHelmet("warrior_armor", ModArmorMaterials.WARRIOR, 200 * 2, WarriorArmorItem::new);
	public static final DeferredHolder<Item, WarriorArmorItem> WARRIOR_ARMOR_CHESTPLATE = registerLivingArmorChestplate("warrior_armor", ModArmorMaterials.WARRIOR, 250 * 2, WarriorArmorItem::new);
	public static final DeferredHolder<Item, WarriorArmorItem> WARRIOR_ARMOR_LEGGINGS = registerLivingArmorLeggings("warrior_armor", ModArmorMaterials.WARRIOR, 250 * 2, WarriorArmorItem::new);
	public static final DeferredHolder<Item, WarriorArmorItem> WARRIOR_ARMOR_BOOTS = registerLivingArmorBoots("warrior_armor", ModArmorMaterials.WARRIOR, 200 * 2, WarriorArmorItem::new);

	//# Misc
	public static final DeferredHolder<Item, EffectCureItem> NUTRIENT_PASTE = registerItem("nutrient_paste", props -> new EffectCureItem(props.food(ModFoods.NUTRIENT_PASTE)));
	public static final DeferredHolder<Item, EffectCureItem> NUTRIENT_BAR = registerItem("nutrient_bar", props -> new EffectCureItem(props.food(ModFoods.NUTRIENT_BAR)));
	public static final DeferredHolder<Item, BloomberryItem> BLOOMBERRY = registerItem("bloomberry", props -> new BloomberryItem(props.food(ModFoods.NUTRIENT_PASTE)));
	public static final DeferredHolder<Item, FertilizerItem> FERTILIZER = registerItem("fertilizer", props -> new FertilizerItem(props.rarity(ModRarities.UNCOMMON)));
	public static final DeferredHolder<Item, SimpleItem> CREATOR_MIX = registerSimpleItem("creator_mix");
	public static final DeferredHolder<Item, BucketItem> ACID_BUCKET = registerItem("acid_bucket", properties -> new BucketItem(ModFluids.ACID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1).rarity(Rarity.COMMON)));
	public static final DeferredHolder<Item, SimpleItem> GELLING_AGENT = registerSimpleItem("gelling_agent");

	public static final DeferredHolder<Item, MaykerBannerPatternItem> MASCOT_BANNER_PATTERNS = registerItem("mascot_patterns", props -> new MaykerBannerPatternItem(ModBannerPatterns.TAG_MASCOT, props));

	//## Internal
	public static final DeferredHolder<Item, SimpleItem> TAB_ICON = registerSimpleItem("tab_icon");

	//## Dev
	public static final DeferredHolder<Item, DevArmCannonItem> DEV_ARM_CANNON = registerDevItem("dev_arm_cannon", props -> new DevArmCannonItem(props.stacksTo(1).durability(ModTiers.BIOFLESH.getUses()).rarity(ModRarities.ULTRA_RARE)));
	public static final DeferredHolder<Item, GuideBookItem> DEV_GUIDE_BOOK = registerDevItem("guide_book", props -> new GuideBookItem(props.stacksTo(1).rarity(ModRarities.RARE)));

	//# Block Items

	//## Machine
	public static final DeferredHolder<Item, BEWLBlockItem> PRIMORDIAL_CRADLE = registerBlockItem(ModBlocks.PRIMORDIAL_CRADLE, block -> new BEWLBlockItem(block, createProperties().rarity(ModRarities.VERY_RARE)));
	public static final DeferredHolder<Item, SimpleBlockItem> BIO_FORGE = registerSimpleBlockItem(ModBlocks.BIO_FORGE, ModRarities.RARE);
	public static final DeferredHolder<Item, SimpleBlockItem> DECOMPOSER = registerSimpleBlockItem(ModBlocks.DECOMPOSER, ModRarities.RARE);
	public static final DeferredHolder<Item, SimpleBlockItem> BIO_LAB = registerSimpleBlockItem(ModBlocks.BIO_LAB, ModRarities.RARE);
	public static final DeferredHolder<Item, SimpleBlockItem> DIGESTER = registerSimpleBlockItem(ModBlocks.DIGESTER, ModRarities.RARE);

	//## Storage, Automation & Utility
	public static final DeferredHolder<Item, SimpleBlockItem> TONGUE = registerSimpleBlockItem(ModBlocks.TONGUE, ModRarities.UNCOMMON);
	public static final DeferredHolder<Item, SimpleBlockItem> MAW_HOPPER = registerSimpleBlockItem(ModBlocks.MAW_HOPPER, ModRarities.UNCOMMON);
	public static final DeferredHolder<Item, FleshkinChestBlockItem> FLESHKIN_CHEST = registerBlockItem(ModBlocks.FLESHKIN_CHEST, FleshkinChestBlockItem::new, ModRarities.UNCOMMON);
	public static final DeferredHolder<Item, StorageSacBlockItem> STORAGE_SAC = registerBlockItem(ModBlocks.STORAGE_SAC, block -> new StorageSacBlockItem(block, createProperties().stacksTo(1)));
	public static final DeferredHolder<Item, SimpleBlockItem> VIAL_HOLDER = registerSimpleBlockItem(ModBlocks.VIAL_HOLDER);
	public static final DeferredHolder<Item, SimpleBlockItem> JUMP_PAD = registerSimpleBlockItem(ModBlocks.JUMP_PAD);
	public static final DeferredHolder<Item, ChrysalisBlockItem> CHRYSALIS = registerBlockItem(ModBlocks.CHRYSALIS, ChrysalisBlockItem::new, ModRarities.VERY_RARE);
	public static final DeferredHolder<Item, SimpleBlockItem> MODULAR_LARYNX = registerSimpleBlockItem(ModBlocks.MODULAR_LARYNX);
	public static final DeferredHolder<Item, SimpleBlockItem> FLESH_SPIKE = registerSimpleBlockItem(ModBlocks.FLESH_SPIKE);
	public static final DeferredHolder<Item, SimpleBlockItem> FLESHKIN_PRESSURE_PLATE = registerSimpleBlockItem(ModBlocks.FLESHKIN_PRESSURE_PLATE);
	public static final DeferredHolder<Item, SimpleBlockItem> WATER_GEL_BLOCK = registerSimpleBlockItem(ModBlocks.WATER_GEL_BLOCK);
	public static final DeferredHolder<Item, SimpleBlockItem> ACID_SPLATTER = registerSimpleBlockItem(ModBlocks.ACID_SPLATTER);
	public static final DeferredHolder<Item, SimpleBlockItem> VOLATILE_SPLATTER = registerSimpleBlockItem(ModBlocks.VOLATILE_SPLATTER);

	//public static final DeferredHolder<Item, SimpleBlockItem> NEURAL_INTERCEPTOR = registerSimpleBlockItem(ModBlocks.NEURAL_INTERCEPTOR, ModRarities.VERY_RARE);
	//	public static final DeferredHolder<Item, SimpleBlockItem> FLESHKIN_DOOR = registerSimpleBlockItem(ModBlocks.FLESHKIN_DOOR);
	//	public static final DeferredHolder<Item, SimpleBlockItem> FLESHKIN_TRAPDOOR = registerSimpleBlockItem(ModBlocks.FLESHKIN_TRAPDOOR);

	public static final DeferredHolder<Item, SimpleBlockItem> FLESH_IRIS_DOOR = registerSimpleBlockItem(ModBlocks.FLESH_IRIS_DOOR);
	public static final DeferredHolder<Item, SimpleBlockItem> FLESH_DOOR = registerSimpleBlockItem(ModBlocks.FLESH_DOOR);
	public static final DeferredHolder<Item, SimpleBlockItem> FULL_FLESH_DOOR = registerSimpleBlockItem(ModBlocks.FULL_FLESH_DOOR);
	public static final DeferredHolder<Item, FleshChainBlockItem> TENDON_CHAIN = registerBlockItem(ModBlocks.TENDON_CHAIN, FleshChainBlockItem::new);
	public static final DeferredHolder<Item, SimpleBlockItem> FLESH_LADDER = registerSimpleBlockItem(ModBlocks.FLESH_LADDER);
	public static final DeferredHolder<Item, SimpleBlockItem> FLESH_FENCE = registerSimpleBlockItem(ModBlocks.FLESH_FENCE);
	public static final DeferredHolder<Item, SimpleBlockItem> FLESH_FENCE_GATE = registerSimpleBlockItem(ModBlocks.FLESH_FENCE_GATE);
	public static final DeferredHolder<Item, SimpleBlockItem> YELLOW_BIO_LANTERN = registerSimpleBlockItem(ModBlocks.YELLOW_BIO_LANTERN);
	public static final DeferredHolder<Item, SimpleBlockItem> BLUE_BIO_LANTERN = registerSimpleBlockItem(ModBlocks.BLUE_BIO_LANTERN);
	public static final DeferredHolder<Item, SimpleBlockItem> PRIMORDIAL_BIO_LANTERN = registerSimpleBlockItem(ModBlocks.PRIMORDIAL_BIO_LANTERN);

	//## Membranes
	public static final DeferredHolder<Item, SimpleBlockItem> BIOMETRIC_MEMBRANE = registerBlockItem(ModBlocks.BIOMETRIC_MEMBRANE, BiometricMembraneBlockItem::new, ModRarities.VERY_RARE);
	public static final DeferredHolder<Item, SimpleBlockItem> ONEWAY_MEMBRANE = registerSimpleBlockItem(ModBlocks.ONEWAY_MEMBRANE);
	public static final DeferredHolder<Item, SimpleBlockItem> IMPERMEABLE_MEMBRANE = registerSimpleBlockItem(ModBlocks.IMPERMEABLE_MEMBRANE);
	public static final DeferredHolder<Item, SimpleBlockItem> IMPERMEABLE_MEMBRANE_PANE = registerSimpleBlockItem(ModBlocks.IMPERMEABLE_MEMBRANE_PANE);
	public static final DeferredHolder<Item, SimpleBlockItem> BABY_PERMEABLE_MEMBRANE = registerSimpleBlockItem(ModBlocks.BABY_PERMEABLE_MEMBRANE);
	public static final DeferredHolder<Item, SimpleBlockItem> BABY_PERMEABLE_MEMBRANE_PANE = registerSimpleBlockItem(ModBlocks.BABY_PERMEABLE_MEMBRANE_PANE);
	public static final DeferredHolder<Item, SimpleBlockItem> ADULT_PERMEABLE_MEMBRANE = registerSimpleBlockItem(ModBlocks.ADULT_PERMEABLE_MEMBRANE);
	public static final DeferredHolder<Item, SimpleBlockItem> ADULT_PERMEABLE_MEMBRANE_PANE = registerSimpleBlockItem(ModBlocks.ADULT_PERMEABLE_MEMBRANE_PANE);
	public static final DeferredHolder<Item, SimpleBlockItem> PRIMAL_PERMEABLE_MEMBRANE = registerSimpleBlockItem(ModBlocks.PRIMAL_PERMEABLE_MEMBRANE);
	public static final DeferredHolder<Item, SimpleBlockItem> PRIMAL_PERMEABLE_MEMBRANE_PANE = registerSimpleBlockItem(ModBlocks.PRIMAL_PERMEABLE_MEMBRANE_PANE);
	public static final DeferredHolder<Item, SimpleBlockItem> UNDEAD_PERMEABLE_MEMBRANE = registerSimpleBlockItem(ModBlocks.UNDEAD_PERMEABLE_MEMBRANE);
	public static final DeferredHolder<Item, SimpleBlockItem> UNDEAD_PERMEABLE_MEMBRANE_PANE = registerSimpleBlockItem(ModBlocks.UNDEAD_PERMEABLE_MEMBRANE_PANE);

	//## Building Blocks
	public static final DeferredHolder<Item, SimpleBlockItem> FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.FLESH);
	public static final DeferredHolder<Item, SimpleBlockItem> FLESH_SLAB = registerSimpleBlockItem(ModBlocks.FLESH_SLAB);
	public static final DeferredHolder<Item, SimpleBlockItem> FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.FLESH_STAIRS);
	public static final DeferredHolder<Item, SimpleBlockItem> FLESH_WALL = registerSimpleBlockItem(ModBlocks.FLESH_WALL);
	public static final DeferredHolder<Item, SimpleBlockItem> PACKED_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.PACKED_FLESH);
	public static final DeferredHolder<Item, SimpleBlockItem> PACKED_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.PACKED_FLESH_SLAB);
	public static final DeferredHolder<Item, SimpleBlockItem> PACKED_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.PACKED_FLESH_STAIRS);
	public static final DeferredHolder<Item, SimpleBlockItem> PACKED_FLESH_WALL = registerSimpleBlockItem(ModBlocks.PACKED_FLESH_WALL);
	public static final DeferredHolder<Item, SimpleBlockItem> FIBROUS_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.FIBROUS_FLESH);
	public static final DeferredHolder<Item, SimpleBlockItem> FIBROUS_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.FIBROUS_FLESH_SLAB);
	public static final DeferredHolder<Item, SimpleBlockItem> FIBROUS_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.FIBROUS_FLESH_STAIRS);
	public static final DeferredHolder<Item, SimpleBlockItem> FIBROUS_FLESH_WALL = registerSimpleBlockItem(ModBlocks.FIBROUS_FLESH_WALL);
	public static final DeferredHolder<Item, SimpleBlockItem> FLESH_PILLAR = registerSimpleBlockItem(ModBlocks.FLESH_PILLAR);
	public static final DeferredHolder<Item, SimpleBlockItem> CHISELED_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.CHISELED_FLESH);
	public static final DeferredHolder<Item, SimpleBlockItem> ORNATE_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.ORNATE_FLESH);
	public static final DeferredHolder<Item, SimpleBlockItem> ORNATE_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.ORNATE_FLESH_SLAB);
	public static final DeferredHolder<Item, SimpleBlockItem> TUBULAR_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.TUBULAR_FLESH_BLOCK);

	public static final DeferredHolder<Item, SimpleBlockItem> PRIMAL_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.PRIMAL_FLESH);
	public static final DeferredHolder<Item, SimpleBlockItem> PRIMAL_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.PRIMAL_FLESH_SLAB);
	public static final DeferredHolder<Item, SimpleBlockItem> PRIMAL_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.PRIMAL_FLESH_STAIRS);
	public static final DeferredHolder<Item, SimpleBlockItem> PRIMAL_FLESH_WALL = registerSimpleBlockItem(ModBlocks.PRIMAL_FLESH_WALL);
	public static final DeferredHolder<Item, SimpleBlockItem> SMOOTH_PRIMAL_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.SMOOTH_PRIMAL_FLESH);
	public static final DeferredHolder<Item, SimpleBlockItem> SMOOTH_PRIMAL_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.SMOOTH_PRIMAL_FLESH_SLAB);
	public static final DeferredHolder<Item, SimpleBlockItem> SMOOTH_PRIMAL_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.SMOOTH_PRIMAL_FLESH_STAIRS);
	public static final DeferredHolder<Item, SimpleBlockItem> SMOOTH_PRIMAL_FLESH_WALL = registerSimpleBlockItem(ModBlocks.SMOOTH_PRIMAL_FLESH_WALL);
	public static final DeferredHolder<Item, SimpleBlockItem> FIBROUS_PRIMAL_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.FIBROUS_PRIMAL_FLESH);
	public static final DeferredHolder<Item, SimpleBlockItem> FIBROUS_PRIMAL_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.FIBROUS_PRIMAL_FLESH_SLAB);
	public static final DeferredHolder<Item, SimpleBlockItem> FIBROUS_PRIMAL_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.FIBROUS_PRIMAL_FLESH_STAIRS);
	public static final DeferredHolder<Item, SimpleBlockItem> FIBROUS_PRIMAL_FLESH_WALL = registerSimpleBlockItem(ModBlocks.FIBROUS_PRIMAL_FLESH_WALL);
	public static final DeferredHolder<Item, SimpleBlockItem> POROUS_PRIMAL_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.POROUS_PRIMAL_FLESH);
	public static final DeferredHolder<Item, SimpleBlockItem> POROUS_PRIMAL_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.POROUS_PRIMAL_FLESH_SLAB);
	public static final DeferredHolder<Item, SimpleBlockItem> POROUS_PRIMAL_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.POROUS_PRIMAL_FLESH_STAIRS);
	public static final DeferredHolder<Item, SimpleBlockItem> POROUS_PRIMAL_FLESH_WALL = registerSimpleBlockItem(ModBlocks.POROUS_PRIMAL_FLESH_WALL);
	public static final DeferredHolder<Item, SimpleBlockItem> MALIGNANT_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH);
	public static final DeferredHolder<Item, SimpleBlockItem> MALIGNANT_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH_SLAB);
	public static final DeferredHolder<Item, SimpleBlockItem> MALIGNANT_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH_STAIRS);
	public static final DeferredHolder<Item, SimpleBlockItem> MALIGNANT_FLESH_WALL = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH_WALL);
	public static final DeferredHolder<Item, SimpleBlockItem> MALIGNANT_FLESH_VEINS = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH_VEINS);
	public static final DeferredHolder<Item, SimpleBlockItem> PRIMAL_BLOOM = registerSimpleBlockItem(ModBlocks.PRIMAL_BLOOM);
	public static final DeferredHolder<Item, SimpleBlockItem> BLOOMLIGHT = registerSimpleBlockItem(ModBlocks.BLOOMLIGHT);
	public static final DeferredHolder<Item, SimpleBlockItem> PRIMAL_ORIFICE = registerSimpleBlockItem(ModBlocks.PRIMAL_ORIFICE);
	public static final DeferredHolder<Item, SimpleBlockItem> PRIMAL_BONE = registerSimpleBlockItem(ModBlocks.PRIMAL_BONE);

	//# Spawn Eggs
	public static final DeferredHolder<Item, DeferredSpawnEggItem> HUNGRY_FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.HUNGRY_FLESH_BLOB, 0xe9967a, 0xf6d2c6);
	public static final DeferredHolder<Item, DeferredSpawnEggItem> FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.FLESH_BLOB, 0xe9967a, 0xf6d2c6);
	public static final DeferredHolder<Item, DeferredSpawnEggItem> LEGACY_FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.LEGACY_FLESH_BLOB, 0xeec5da, 0xffc0cb);
	public static final DeferredHolder<Item, DeferredSpawnEggItem> PRIMORDIAL_FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.PRIMORDIAL_FLESH_BLOB, 0xde6074, 0xc343fe);
	public static final DeferredHolder<Item, DeferredSpawnEggItem> PRIMORDIAL_HUNGRY_FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.PRIMORDIAL_HUNGRY_FLESH_BLOB, 0x752144, 0x752144);
	public static final DeferredHolder<Item, DeferredSpawnEggItem> FLESH_COW_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.FLESH_COW, 0xe9967a, 0x9d7572);
	public static final DeferredHolder<Item, DeferredSpawnEggItem> FLESH_SHEEP_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.FLESH_SHEEP, 0xe9967a, 0xf9bbd4);
	public static final DeferredHolder<Item, DeferredSpawnEggItem> FLESH_PIG_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.FLESH_PIG, 0xe9967a, 0xed7684);
	public static final DeferredHolder<Item, DeferredSpawnEggItem> FLESH_CHICKEN_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.FLESH_CHICKEN, 0xe9967a, 0xce4e65);
	public static final DeferredHolder<Item, DeferredSpawnEggItem> CHROMA_SHEEP_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.CHROMA_SHEEP, 0xe9967a, 0xf9bbd4);
	public static final DeferredHolder<Item, DeferredSpawnEggItem> THICK_FUR_SHEEP_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.THICK_FUR_SHEEP, 0xe9967a, 0xf9bbd4);

	private ModItems() {}

	public static Stream<Item> stream() {
		return ModItems.ITEMS.getEntries().stream().map(DeferredHolder::get);
	}

	public static <T extends Item> Stream<T> findItems(Class<T> clazz) {
		return ModItems.ITEMS.getEntries().stream()
				.map(DeferredHolder::get)
				.filter(clazz::isInstance)
				.map(clazz::cast);
	}

	public static <T extends Item> Stream<DeferredHolder<Item, T>> findEntries(Class<T> clazz) {
		//noinspection unchecked
		return ModItems.ITEMS.getEntries().stream()
				.filter(registryObject -> clazz.isInstance(registryObject.get()))
				.map(registryObject -> (DeferredHolder<Item, T>) registryObject);
	}

	private static <T extends Item> DeferredHolder<Item, T> registerItem(String name, Function<Item.Properties, T> factory) {
		return ITEMS.register(name, () -> factory.apply(createProperties()));
	}

	private static <T extends Item> DeferredHolder<Item, T> registerDevItem(String name, Function<Item.Properties, T> factory) {
		return ITEMS.register(name, () -> factory.apply(createProperties()));
	}

	private static <T extends Block> DeferredHolder<Item, SimpleBlockItem> registerSimpleBlockItem(DeferredHolder<Block, T> blockHolder) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> new SimpleBlockItem(blockHolder.get(), createProperties()));
	}

	private static <T extends Block> DeferredHolder<Item, SimpleBlockItem> registerSimpleBlockItem(DeferredHolder<Block, T> blockHolder, Rarity rarity) {
		return registerSimpleBlockItem(blockHolder, () -> createProperties().rarity(rarity));
	}

	private static <T extends Block> DeferredHolder<Item, SimpleBlockItem> registerSimpleBlockItem(DeferredHolder<Block, T> blockHolder, Supplier<Item.Properties> properties) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> new SimpleBlockItem(blockHolder.get(), properties.get()));
	}

	private static <T extends Block, I extends BlockItem> DeferredHolder<Item, I> registerBlockItem(DeferredHolder<Block, T> blockHolder, Function<T, I> factory) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> factory.apply(blockHolder.get()));
	}

	private static <T extends Block, I extends BlockItem> DeferredHolder<Item, I> registerBlockItem(DeferredHolder<Block, T> blockHolder, IBlockItemFactory<T, I> factory) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> factory.create(blockHolder.get(), createProperties()));
	}

	private static <T extends Block, I extends BlockItem> DeferredHolder<Item, I> registerBlockItem(DeferredHolder<Block, T> blockHolder, IBlockItemFactory<T, I> factory, Rarity rarity) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> factory.create(blockHolder.get(), createProperties().rarity(rarity)));
	}

	private static <I extends ArmorItem> DeferredHolder<Item, I> registerArmorHelmet(String name, Holder<ArmorMaterial> material, ArmorFactory<I> factory) {
		return registerArmor(name + "_helmet", material, ArmorItem.Type.HELMET, factory);
	}

	private static <I extends ArmorItem> DeferredHolder<Item, I> registerArmorChestplate(String name, Holder<ArmorMaterial> material, ArmorFactory<I> factory) {
		return registerArmor(name + "_chestplate", material, ArmorItem.Type.CHESTPLATE, factory);
	}

	private static <I extends ArmorItem> DeferredHolder<Item, I> registerArmorLeggings(String name, Holder<ArmorMaterial> material, ArmorFactory<I> factory) {
		return registerArmor(name + "_leggings", material, ArmorItem.Type.LEGGINGS, factory);
	}

	private static <I extends ArmorItem> DeferredHolder<Item, I> registerArmorBoots(String name, Holder<ArmorMaterial> material, ArmorFactory<I> factory) {
		return registerArmor(name + "_boots", material, ArmorItem.Type.BOOTS, factory);
	}

	private static <I extends ArmorItem> DeferredHolder<Item, I> registerArmor(String name, Holder<ArmorMaterial> material, ArmorItem.Type type, ArmorFactory<I> factory) {
		return ITEMS.register(name, () -> factory.create(material, type, createProperties()));
	}

	private static <I extends ArmorItem> DeferredHolder<Item, I> registerLivingArmorHelmet(String name, Holder<ArmorMaterial> material, int maxNutrients, LivingArmorFactory<I> factory) {
		return registerLivingArmor(name + "_helmet", material, ArmorItem.Type.HELMET, maxNutrients, factory);
	}

	private static <I extends ArmorItem> DeferredHolder<Item, I> registerLivingArmorChestplate(String name, Holder<ArmorMaterial> material, int maxNutrients, LivingArmorFactory<I> factory) {
		return registerLivingArmor(name + "_chestplate", material, ArmorItem.Type.CHESTPLATE, maxNutrients, factory);
	}

	private static <I extends ArmorItem> DeferredHolder<Item, I> registerLivingArmorLeggings(String name, Holder<ArmorMaterial> material, int maxNutrients, LivingArmorFactory<I> factory) {
		return registerLivingArmor(name + "_leggings", material, ArmorItem.Type.LEGGINGS, maxNutrients, factory);
	}

	private static <I extends ArmorItem> DeferredHolder<Item, I> registerLivingArmorBoots(String name, Holder<ArmorMaterial> material, int maxNutrients, LivingArmorFactory<I> factory) {
		return registerLivingArmor(name + "_boots", material, ArmorItem.Type.BOOTS, maxNutrients, factory);
	}

	private static <I extends ArmorItem> DeferredHolder<Item, I> registerLivingArmor(String name, Holder<ArmorMaterial> material, ArmorItem.Type type, int maxNutrients, LivingArmorFactory<I> factory) {
		return ITEMS.register(name, () -> factory.create(material, type, maxNutrients, createProperties().rarity(ModRarities.VERY_RARE)));
	}

	private static <T extends EntityType<? extends Mob>> DeferredHolder<Item, DeferredSpawnEggItem> registerSpawnEgg(DeferredHolder<EntityType<?>, T> mobHolder, int primaryColor, int accentColor) {
		return ITEMS.register(mobHolder.getId().getPath() + "_spawn_egg", () -> new DeferredSpawnEggItem(mobHolder, primaryColor, accentColor, createProperties()));
	}

	private static <T extends Serum> DeferredHolder<Item, SerumItem> registerSerumItem(DeferredHolder<Serum, T> registryObject) {
		return ITEMS.register(registryObject.getId().getPath(), () -> new SerumItem(createProperties().stacksTo(16).rarity(ModRarities.UNCOMMON), registryObject));
	}

	private static DeferredHolder<Item, SimpleItem> registerSimpleVialItem(String name) {
		return ITEMS.register(name, () -> new SimpleItem(createProperties()));
	}

	private static DeferredHolder<Item, SimpleItem> registerSimpleItem(String name) {
		return ITEMS.register(name, () -> new SimpleItem(createProperties()));
	}

	private static DeferredHolder<Item, SimpleItem> registerSimpleItem(String name, Rarity rarity) {
		return registerSimpleItem(name, () -> createProperties().rarity(rarity));
	}

	private static Item.Properties createProperties() {
		return new Item.Properties().rarity(ModRarities.COMMON);
	}

	private static DeferredHolder<Item, SimpleItem> registerSimpleItem(String name, Supplier<Item.Properties> properties) {
		return ITEMS.register(name, () -> new SimpleItem(properties.get()));
	}

	private interface SwordSmithy<T extends SwordItem> {
		AttributeSupplier PLAYER_ATTRIBUTES = Player.createAttributes().build();

		static <T extends SwordItem> T forge(SwordSmithy<T> smithy, Tier tier, int attackDamage, float attackSpeed, Item.Properties properties) {
			int attackDamageModifier = Mth.floor(attackDamage - (PLAYER_ATTRIBUTES.getValue(Attributes.ATTACK_DAMAGE) + tier.getAttackDamageBonus()));
			float attackSpeedModifier = attackSpeed - (float) PLAYER_ATTRIBUTES.getValue(Attributes.ATTACK_SPEED);
			return smithy.forge(tier, attackDamageModifier, attackSpeedModifier, properties);
		}

		T forge(Tier tier, int attackDamageModifier, float attackSpeedModifier, Item.Properties properties);
	}

	interface IBlockItemFactory<T extends Block, I extends BlockItem> {
		I create(T block, Item.Properties properties);
	}

	interface ArmorFactory<I extends ArmorItem> {
		I create(Holder<ArmorMaterial> material, ArmorItem.Type type, Item.Properties properties);
	}

	interface LivingArmorFactory<I extends ArmorItem> {
		I create(Holder<ArmorMaterial> material, ArmorItem.Type type, int maxNutrients, Item.Properties properties);
	}

}
