package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;

public final class MigrationHandler {

	private MigrationHandler() {}

	public static void registerAliases() {
		ModSerums.SERUMS.addAlias(BiomancyMod.rl("growth_serum"), ModSerums.AGEING_SERUM.getId());

		ModBlocks.BLOCKS.addAlias(BiomancyMod.rl("bio_lantern"), ModBlocks.YELLOW_BIO_LANTERN.getId());
		ModBlocks.BLOCKS.addAlias(BiomancyMod.rl("bone_spike"), ModBlocks.FLESH_SPIKE.getId());
		ModBlocks.BLOCKS.addAlias(BiomancyMod.rl("creator"), ModBlocks.PRIMORDIAL_CRADLE.getId());
		ModBlocks.BLOCKS.addAlias(BiomancyMod.rl("flesh_block"), ModBlocks.FLESH.getId());
		ModBlocks.BLOCKS.addAlias(BiomancyMod.rl("flesh_block_slab"), ModBlocks.FLESH_SLAB.getId());
		ModBlocks.BLOCKS.addAlias(BiomancyMod.rl("flesh_block_stairs"), ModBlocks.FLESH_STAIRS.getId());
		ModBlocks.BLOCKS.addAlias(BiomancyMod.rl("flesh_irisdoor"), ModBlocks.FLESH_IRIS_DOOR.getId());
		ModBlocks.BLOCKS.addAlias(BiomancyMod.rl("necrotic_flesh_block"), ModBlocks.MALIGNANT_FLESH.getId());
		ModBlocks.BLOCKS.addAlias(BiomancyMod.rl("flesh_tentacle"), ModBlocks.MALIGNANT_FLESH_VEINS.getId());
		ModBlocks.BLOCKS.addAlias(BiomancyMod.rl("corrupted_primal_flesh"), ModBlocks.PRIMAL_FLESH.getId());

		ModEntityTypes.ENTITIES.addAlias(BiomancyMod.rl("malignant_flesh_blob"), ModEntityTypes.PRIMORDIAL_HUNGRY_FLESH_BLOB.getId());

		ModBlockEntities.BLOCK_ENTITIES.addAlias(BiomancyMod.rl("creator"), ModBlockEntities.PRIMORDIAL_CRADLE.getId());

		ModItems.ITEMS.addAlias(BiomancyMod.rl("long_claws"), ModItems.RAVENOUS_CLAWS.getId());

		ModItems.ITEMS.addAlias(BiomancyMod.rl("mascot_pattern"), ModItems.MASCOT_BANNER_PATTERNS.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("mascot_outline_pattern"), ModItems.MASCOT_BANNER_PATTERNS.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("mascot_accent_pattern"), ModItems.MASCOT_BANNER_PATTERNS.getId());

		ModItems.ITEMS.addAlias(BiomancyMod.rl("bio_lantern"), ModItems.YELLOW_BIO_LANTERN.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("glass_vial"), ModItems.VIAL.getId());

		ModItems.ITEMS.addAlias(BiomancyMod.rl("creator"), ModItems.PRIMORDIAL_CRADLE.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("flesh_block"), ModItems.FLESH_BLOCK.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("flesh_block_slab"), ModItems.FLESH_SLAB.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("flesh_block_stairs"), ModItems.FLESH_STAIRS.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("flesh_irisdoor"), ModItems.FLESH_IRIS_DOOR.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("necrotic_flesh_block"), ModItems.MALIGNANT_FLESH_BLOCK.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("flesh_tentacle"), ModItems.MALIGNANT_FLESH_VEINS.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("corrupted_primal_flesh"), ModItems.PRIMAL_FLESH_BLOCK.getId());

		ModItems.ITEMS.addAlias(BiomancyMod.rl("biometal"), ModItems.LIVING_FLESH.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("bone_gear"), BuiltInRegistries.ITEM.getKey(Items.BONE));
		ModItems.ITEMS.addAlias(BiomancyMod.rl("lens"), ModItems.GEM_FRAGMENTS.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("skin_chunk"), ModItems.FLESH_BITS.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("flesh_lump"), ModItems.FLESH_BITS.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("mended_skin"), ModItems.FLESH_BITS.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("stomach"), ModItems.GENERIC_MOB_GLAND.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("artificial_stomach"), ModItems.GENERIC_MOB_GLAND.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("bolus"), ModItems.NUTRIENTS.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("keratin_filaments"), ModItems.TOUGH_FIBERS.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("digestate"), ModItems.ORGANIC_MATTER.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("oxide_powder"), ModItems.MINERAL_FRAGMENT.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("silicate_paste"), ModItems.MINERAL_FRAGMENT.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("bio_minerals"), ModItems.MINERAL_FRAGMENT.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("hormone_bile"), ModItems.HORMONE_SECRETION.getId());
		ModItems.ITEMS.addAlias(BiomancyMod.rl("corrosive_additive"), ModItems.DECAYING_ADDITIVE.getId());
	}

}
