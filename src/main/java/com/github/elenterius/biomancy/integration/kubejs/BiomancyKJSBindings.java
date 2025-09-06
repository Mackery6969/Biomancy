package com.github.elenterius.biomancy.integration.kubejs;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

//TODO: ALL METHOD NAMES MUST BE UNIQUE BECAUSE RHINO IS UNABLE TO HANDLE METHOD OVERLOADING

@SuppressWarnings("unused")
public final class BiomancyKJSBindings {

	public static EntityType<?> getEntityType(ResourceLocation entityTypeId) {
		//noinspection deprecation
		return BuiltInRegistries.ENTITY_TYPE.get(entityTypeId);
	}

	public final EssenceItemUtil EssenceItem = new EssenceItemUtil();
	public final EssenceIngredientUtil EssenceIngredient = new EssenceIngredientUtil();

	public static final class EssenceIngredientUtil {

		private EssenceIngredientUtil() {}

		public com.github.elenterius.biomancy.crafting.EssenceIngredient fromTier(ResourceLocation entityTypeId, int tier) {
			EntityType<?> entityType = getEntityType(entityTypeId);
			BiomancyKubeJSPlugin.LOGGER.warn("Creating EssenceIngredient for {} with tier {}", entityTypeId, tier);
			return com.github.elenterius.biomancy.crafting.EssenceIngredient.of(entityType, tier);
		}

		public com.github.elenterius.biomancy.crafting.EssenceIngredient from(ResourceLocation entityTypeId) {
			EntityType<?> entityType = getEntityType(entityTypeId);
			BiomancyKubeJSPlugin.LOGGER.warn("Creating EssenceIngredient for {} with tier -1", entityTypeId);
			return com.github.elenterius.biomancy.crafting.EssenceIngredient.of(entityType);
		}

	}

	public static final class EssenceItemUtil {

		private EssenceItemUtil() {}

		public ItemStack from(ResourceLocation entityTypeId) {
			EntityType<?> entityType = getEntityType(entityTypeId);
			return com.github.elenterius.biomancy.item.EssenceItem.fromEntityType(entityType, 1);
		}

		public ItemStack fromTier(ResourceLocation entityTypeId, int tier) {
			EntityType<?> entityType = getEntityType(entityTypeId);
			return com.github.elenterius.biomancy.item.EssenceItem.fromEntityType(entityType, tier);
		}

		public ItemStack fromUUID(ResourceLocation entityTypeId, UUID uuid) {
			EntityType<?> entityType = getEntityType(entityTypeId);
			return com.github.elenterius.biomancy.item.EssenceItem.fromEntityType(entityType, uuid);
		}

		public ItemStack fromLiving(LivingEntity livingEntity) {
			return com.github.elenterius.biomancy.item.EssenceItem.fromEntity(livingEntity, 0, 0);
		}

		public ItemStack fromLivingWith(LivingEntity livingEntity, int surgicalPrecisionLevel, int lootingLevel) {
			return com.github.elenterius.biomancy.item.EssenceItem.fromEntity(livingEntity, surgicalPrecisionLevel, lootingLevel);
		}

	}

}

