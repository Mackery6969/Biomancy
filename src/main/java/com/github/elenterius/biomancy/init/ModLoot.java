package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.loot.CatMorningGiftLootModifier;
import com.github.elenterius.biomancy.loot.DespoilLootModifier;
import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collections;
import java.util.Set;

import static com.github.elenterius.biomancy.BiomancyMod.rl;

public final class ModLoot {

	public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, BiomancyMod.MOD_ID);
	public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<? extends IGlobalLootModifier>> DESPOIL_SERIALIZER = GLOBAL_MODIFIERS.register("despoil", DespoilLootModifier.CODEC);
	public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<? extends IGlobalLootModifier>> CAT_MORNING_GIFT_SERIALIZER = GLOBAL_MODIFIERS.register("cat_morning_gift", CatMorningGiftLootModifier.CODEC);

	private ModLoot() {}

	public static final class Entity {
		private static final Set<ResourceLocation> LOOT_TABLE_KEYS = Sets.newHashSet();

		public static final ResourceLocation FLESH_BLOB_SIZE_2 = register("entities/flesh_blob/size_2");
		public static final ResourceLocation FLESH_BLOB_SIZE_3 = register("entities/flesh_blob/size_3");
		public static final ResourceLocation FLESH_BLOB_SIZE_4 = register("entities/flesh_blob/size_4");
		public static final ResourceLocation FLESH_BLOB_SIZE_5 = register("entities/flesh_blob/size_5");
		public static final ResourceLocation FLESH_BLOB_SIZE_6 = register("entities/flesh_blob/size_6");
		public static final ResourceLocation FLESH_BLOB_SIZE_7 = register("entities/flesh_blob/size_7");
		public static final ResourceLocation FLESH_BLOB_SIZE_8 = register("entities/flesh_blob/size_8");
		public static final ResourceLocation FLESH_BLOB_SIZE_9 = register("entities/flesh_blob/size_9");
		public static final ResourceLocation FLESH_BLOB_SIZE_10 = register("entities/flesh_blob/size_10");

		public static final ResourceLocation FLESH_SHEEP_UNSHORN = register("entities/flesh_sheep/unshorn");

		private static final Set<ResourceLocation> IMMUTABLE_LOOT_TABLE_KEYS = Collections.unmodifiableSet(LOOT_TABLE_KEYS);

		private Entity() {}

		private static ResourceLocation register(String id) {
			return register(rl(id));
		}

		private static ResourceLocation register(ResourceLocation key) {
			if (LOOT_TABLE_KEYS.add(key)) {
				return key;
			}
			else {
				throw new IllegalArgumentException(key + " is already a registered built-in loot table");
			}
		}

		public static Set<ResourceLocation> all() {
			return IMMUTABLE_LOOT_TABLE_KEYS;
		}

	}

}
