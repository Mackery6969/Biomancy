package com.github.elenterius.biomancy.crafting;

import com.github.elenterius.biomancy.init.ModIngredientTypes;
import com.github.elenterius.biomancy.item.EssenceItem;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.stream.Stream;

public record EssenceIngredient(ItemStack itemStack, CompoundTag partialTag) implements ICustomIngredient {

	public static final MapCodec<EssenceIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ItemStack.SINGLE_ITEM_CODEC.fieldOf("item").forGetter(EssenceIngredient::itemStack),
			CompoundTag.CODEC.fieldOf("predicate_tag").forGetter(EssenceIngredient::partialTag)
	).apply(instance, EssenceIngredient::new));

	public static Ingredient of(EntityType<?> entityType) {
		return of(entityType, 0);
	}

	public static Ingredient of(EntityType<?> entityType, int tier) {
		if (tier < 0 || tier > 3) throw new IllegalArgumentException("Cannot create a EssenceIngredient with invalid tier");

		CompoundTag essenceTag = new CompoundTag();
		essenceTag.putString(EssenceItem.ENTITY_TYPE_KEY, EntityType.getKey(entityType).toString());

		CompoundTag partialTag = new CompoundTag();
		partialTag.put(EssenceItem.ESSENCE_DATA_KEY, essenceTag);
		if (tier > 0) partialTag.putInt(EssenceItem.ESSENCE_TIER_KEY, tier);

		ItemStack stack = EssenceItem.fromEntityType(entityType, tier); //we set the tier here only for visual purposes

		return new EssenceIngredient(stack, partialTag).toVanilla();
	}

	@Override
	public boolean test(ItemStack stack) {
		if (stack.isEmpty()) return false;
		return itemStack.getItem() == stack.getItem() && CustomData.itemMatcher(DataComponents.CUSTOM_DATA, partialTag).test(stack);
	}

	@Override
	public Stream<ItemStack> getItems() {
		return Stream.of(itemStack);
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IngredientType<?> getType() {
		return ModIngredientTypes.ESSENCE.get();
	}

}
