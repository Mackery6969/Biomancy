package com.github.elenterius.biomancy.datagen.advancements;

import com.github.elenterius.biomancy.advancements.trigger.SacrificedItemTrigger;
import com.github.elenterius.biomancy.datagen.lang.LangProvider;
import com.github.elenterius.biomancy.init.ModTriggers;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ModAdvancementProvider extends AdvancementProvider {

	public ModAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper, LangProvider lang) {
		super(output, lookupProvider, fileHelper, List.of(new BiomancyAdvancementsGenerator(lang)));
	}

	protected static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(ItemLike... items) {
		return InventoryChangeTrigger.TriggerInstance.hasItems(items);
	}

	protected static Criterion<InventoryChangeTrigger.TriggerInstance> hasTag(TagKey<Item> tag) {
		return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(tag).build());
	}

	protected static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> hasPlacedBlock(Block block) {
		return ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(block);
	}

	protected static Criterion<KilledTrigger.TriggerInstance> hasKilledEntity(EntityType<?> entityType) {
		return KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entityType));
	}

	protected static Criterion<KilledTrigger.TriggerInstance> hasKilledEntityTag(TagKey<EntityType<?>> tag) {
		return KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(tag));
	}

	protected static Criterion<SacrificedItemTrigger.TriggerInstance> hasSacrificedItem(ItemLike item) {
		return ModTriggers.SACRIFICED_ITEM_TRIGGER.get().createCriterion(SacrificedItemTrigger.TriggerInstance.sacrificedItem(item));
	}

	protected static Criterion<SacrificedItemTrigger.TriggerInstance> hasSacrificedTag(TagKey<Item> tag) {
		return ModTriggers.SACRIFICED_ITEM_TRIGGER.get().createCriterion(SacrificedItemTrigger.TriggerInstance.sacrificedItem(tag));
	}

	protected static Criterion<TradeTrigger.TriggerInstance> hasTradedItem(ItemLike item) {
		return CriteriaTriggers.TRADE.createCriterion(new TradeTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(item).build())));
	}

	protected static Criterion<TradeTrigger.TriggerInstance> hasTradedItems(ItemLike... item) {
		return CriteriaTriggers.TRADE.createCriterion(new TradeTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(item).build())));
	}

	protected static Criterion<RecipeUnlockedTrigger.TriggerInstance> hasUnlockedDefaultRecipe(ItemLike itemLike) {
		return RecipeUnlockedTrigger.unlocked(RecipeBuilder.getDefaultRecipeId(itemLike));
	}

}
