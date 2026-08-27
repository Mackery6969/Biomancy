package com.github.elenterius.biomancy.datagen.recipes.builder;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jspecify.annotations.Nullable;

public sealed interface RecipeBuilder<T extends RecipeBuilder<?>> permits BioBrewingRecipeBuilder, BioForgingRecipeBuilder, DecomposingRecipeBuilder, DigestingRecipeBuilder, WorkbenchRecipeBuilder.ShapedBuilder, WorkbenchRecipeBuilder.ShapelessBuilder {

	static String getRecipeFolderName(@Nullable RecipeCategory category, String modId) {
		return category != null ? category.getFolderName() : modId;
	}

	private Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike itemLike) {
		return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(itemLike).build());
	}

	private Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag) {
		return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(tag).build());
	}

	private String getItemName(ItemLike itemLike) {
		ResourceLocation key = BuiltInRegistries.ITEM.getKey(itemLike.asItem());
		return key != null ? key.getPath() : "unknown";
	}

	private String getTagName(TagKey<Item> tag) {
		return tag.location().getPath();
	}

	T unlockedBy(String name, Criterion<?> criterion);

	default T unlockedBy(String name, ItemPredicate predicate) {
		return unlockedBy(name, InventoryChangeTrigger.TriggerInstance.hasItems(predicate));
	}

	default T unlockedBy(ItemLike itemLike, Criterion<?> criterion) {
		return unlockedBy("has_" + getItemName(itemLike), criterion);
	}

	default T unlockedBy(ItemLike itemLike) {
		return unlockedBy("has_" + getItemName(itemLike), has(itemLike));
	}

	default T unlockedBy(DeferredHolder<Item, ? extends Item> itemHolder) {
		Item item = itemHolder.get();
		return unlockedBy("has_" + getItemName(item), has(item));
	}

	default T unlockedBy(TagKey<Item> tag, Criterion<?> criterion) {
		return unlockedBy("has_" + getTagName(tag), criterion);
	}

	default T unlockedBy(TagKey<Item> tag) {
		return unlockedBy("has_" + getTagName(tag), has(tag));
	}

	default void save(RecipeOutput recipeOutput) {
		save(recipeOutput, null);
	}

	void save(RecipeOutput recipeOutput, @Nullable RecipeCategory category);

}
