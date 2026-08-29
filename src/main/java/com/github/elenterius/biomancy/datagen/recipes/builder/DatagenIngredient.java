package com.github.elenterius.biomancy.datagen.recipes.builder;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Objects;

/**
 * Stands in for an item of a mod that is not present at datagen time.
 * The ingredient is emitted as a biomancy-owned item tag which optionally references the foreign item.
 */
public final class DatagenIngredient {

	public final ResourceLocation resourceLocation;
	public final TagKey<Item> tagKey;
	public final Ingredient ingredient;

	public DatagenIngredient(String itemKey) {
		this(Objects.requireNonNull(ResourceLocation.tryParse(itemKey)));
	}

	public DatagenIngredient(String namespace, String path) {
		this(Objects.requireNonNull(ResourceLocation.tryBuild(namespace, path)));
	}

	public DatagenIngredient(ResourceLocation itemKey) {
		resourceLocation = itemKey;
		tagKey = TagKey.create(Registries.ITEM, BiomancyMod.rl("compat/" + itemKey.getNamespace() + "/" + itemKey.getPath()));
		ingredient = Ingredient.of(tagKey);
	}

	public String recipeName() {
		return resourceLocation.getNamespace() + "_" + resourceLocation.getPath();
	}

}
