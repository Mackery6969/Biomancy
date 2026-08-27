package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.block.membrane.BiometricMembraneBlock;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.item.EssenceItem;
import com.github.elenterius.biomancy.item.armor.AcolyteArmorUpgrades;
import com.github.elenterius.biomancy.item.armor.LivingArmorItem;
import com.github.elenterius.biomancy.world.mound.MoundShape;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public final class SpecialCraftingRecipeMaker {

	private SpecialCraftingRecipeMaker() {}

	public static List<CraftingRecipe> createCradleCleansingRecipes() {
		ItemStack cradle = ModItems.PRIMORDIAL_CRADLE.get().getDefaultInstance();
		CompoundTag tag = new CompoundTag();
		CompoundTag tagProcGen = new CompoundTag();
		MoundShape.ProcGenValues procGenValues = new MoundShape.ProcGenValues(1234L, (byte) 0, (byte) 0, (byte) 1, 250, 66, 0.7f, 0.5f);
		procGenValues.writeTo(tagProcGen);
		tag.put(PrimordialCradleBlockEntity.PROC_GEN_VALUES_KEY, tagProcGen);
		BlockItem.setBlockEntityData(cradle, ModBlockEntities.PRIMORDIAL_CRADLE.get(), tag);

		List<Ingredient> ingredients = new ArrayList<>();
		ingredients.add(Ingredient.of(cradle));
		ingredients.add(Ingredient.of(ModItems.CLEANSING_SERUM.get()));
		NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, ingredients.toArray(Ingredient[]::new));

		ItemStack result = ModItems.PRIMORDIAL_CRADLE.get().getDefaultInstance();

		return List.of(
				new ShapelessRecipe("", CraftingBookCategory.MISC, result, inputs)
		);
	}

	public static List<CraftingRecipe> createHelmetUpgradeRecipes() {
		return ModItems.findEntries(LivingArmorItem.class).map(SpecialCraftingRecipeMaker::createHelmetUpgradeRecipe).toList();
	}

	private static <A extends ArmorItem> CraftingRecipe createHelmetUpgradeRecipe(DeferredHolder<Item, A> armorItem) {
		NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, Ingredient.of(ModItems.PRIMORDIAL_CORE.get()), Ingredient.of(armorItem.get()));
		ItemStack result = AcolyteArmorUpgrades.addUpgrade(armorItem.get().getDefaultInstance(), AcolyteArmorUpgrades.PRIMORDIAL_SIGHT);
		return new ShapelessRecipe("", CraftingBookCategory.MISC, result, inputs);
	}

	public static List<CraftingRecipe> createPlayerHeadRecipes() {
		NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY,
				Ingredient.of(Items.PLAYER_HEAD),
				Ingredient.of(ModItems.EXOTIC_DUST.get()),
				Ingredient.of(createUniquePlayerEssence(UUID.fromString("853c80ef-3c37-49fd-aa49-938b674adae6"), "jeb_"))
		);
		ItemStack result = createPlayerHead("jeb_");

		return List.of(
				new ShapelessRecipe("", CraftingBookCategory.MISC, result, inputs)
		);
	}

	private static ItemStack createPlayerHead(String name) {
		ItemStack stack = Items.PLAYER_HEAD.getDefaultInstance();
		stack.set(DataComponents.PROFILE, new ResolvableProfile(Optional.of(name), Optional.empty(), new PropertyMap()));

		return stack;
	}

	private static ItemStack createUniquePlayerEssence(UUID entityUUID, String playerName) {
		ItemStack stack = ModItems.ESSENCE.get().getDefaultInstance();

		EntityType<?> entityType = EntityType.PLAYER;
		int[] colors = EssenceItem.getEssenceColors(entityUUID);

		CompoundTag essenceTag = new CompoundTag();
		essenceTag.putString(EssenceItem.ENTITY_TYPE_KEY, EntityType.getKey(entityType).toString());
		essenceTag.putString(EssenceItem.ENTITY_NAME_KEY, entityType.getDescriptionId());
		essenceTag.putUUID(EssenceItem.ENTITY_UUID_KEY, entityUUID);

		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
			tag.put(EssenceItem.ESSENCE_DATA_KEY, essenceTag);
			tag.putInt(EssenceItem.ESSENCE_TIER_KEY, 3);
			tag.putIntArray(EssenceItem.COLORS_KEY, colors);
			tag.putString(EssenceItem.PLAYER_NAME_KEY, playerName);
		});

		return stack;
	}

	public static List<CraftingRecipe> createBiometricMembraneRecipes() {
		UUID pigUUID = UUID.fromString("420faf42-bf42-4b20-af42-c42420e42d42");

		UUID playerUUID = UUID.fromString("853c80ef-3c37-49fd-aa49-938b674adae6");
		String playerName = "jeb_";

		Stream<CraftingRecipe[]> recipePairs = Stream.of(
				createRecipePair(EntityType.PIG, false, null),
				createRecipePair(EntityType.PIG, true, null),
				createRecipePair(EntityType.PIG, false, pigUUID),
				createRecipePair(EntityType.PIG, true, pigUUID),
				createRecipePair(EntityType.PLAYER, false, null),
				createRecipePair(EntityType.PLAYER, true, null),
				createUniquePlayerRecipePair(false, playerUUID, playerName),
				createUniquePlayerRecipePair(true, playerUUID, playerName)
		);

		return recipePairs.flatMap(Stream::of).toList();
	}

	private static CraftingRecipe[] createUniquePlayerRecipePair(boolean isInverted, UUID playerUUID, String playerName) {
		Ingredient ingredient = Ingredient.of(createUniquePlayerEssence(playerUUID, playerName));
		return createRecipePair(EntityType.PLAYER, isInverted, playerUUID, 3, ingredient);
	}

	private static CraftingRecipe[] createRecipePair(EntityType<?> entityType, boolean isInverted, @Nullable UUID entityUUID) {
		int tier = entityUUID != null ? 3 : 0;
		Ingredient ingredient = Ingredient.of(EssenceItem.fromEntityType(entityType, tier));
		return createRecipePair(entityType, isInverted, entityUUID, tier, ingredient);
	}

	private static CraftingRecipe[] createRecipePair(EntityType<?> entityType, boolean isInverted, @Nullable UUID entityUUID, int tier, Ingredient essence) {
		List<Ingredient> ingredients = new ArrayList<>();
		ingredients.add(essence);
		ingredients.add(Ingredient.of(ModItems.BIOMETRIC_MEMBRANE.get()));

		if (isInverted) {
			ingredients.add(Ingredient.of(Items.REDSTONE_TORCH));
		}

		NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, ingredients.toArray(Ingredient[]::new));

		ItemStack membraneStack = BiometricMembraneBlock.createItem(entityType, entityUUID, EssenceItem.getEssenceColors(entityType, entityUUID, tier), isInverted);

		String name = ModItems.BIOMETRIC_MEMBRANE.getId().toLanguageKey();

		return new CraftingRecipe[]{
				new ShapelessRecipe(name, CraftingBookCategory.MISC, membraneStack, inputs),
				new ShapelessRecipe(name, CraftingBookCategory.MISC, new ItemStack(ModItems.BIOMETRIC_MEMBRANE.get()), NonNullList.of(Ingredient.EMPTY, Ingredient.of(membraneStack))),
		};
	}
}
