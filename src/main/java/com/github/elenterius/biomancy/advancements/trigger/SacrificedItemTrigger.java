package com.github.elenterius.biomancy.advancements.trigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

public class SacrificedItemTrigger extends SimpleCriterionTrigger<SacrificedItemTrigger.TriggerInstance> {

	@Override
	public Codec<TriggerInstance> codec() {
		return TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, ItemStack stack) {
		trigger(player, triggerInstance -> triggerInstance.matches(stack));
	}

	public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) implements SimpleCriterionTrigger.SimpleInstance {

		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
				ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item)
		).apply(instance, TriggerInstance::new));

		public static TriggerInstance sacrificedItems(ItemLike... items) {
			ItemPredicate predicate = ItemPredicate.Builder.item().of(items).build();
			return new TriggerInstance(Optional.empty(), Optional.of(predicate));
		}

		public static TriggerInstance sacrificedItem(ItemLike item) {
			ItemPredicate predicate = ItemPredicate.Builder.item().of(item).build();
			return new TriggerInstance(Optional.empty(), Optional.of(predicate));
		}

		public static TriggerInstance sacrificedItem(TagKey<Item> tag) {
			ItemPredicate predicate = ItemPredicate.Builder.item().of(tag).build();
			return new TriggerInstance(Optional.empty(), Optional.of(predicate));
		}

		public static TriggerInstance sacrificedItem() {
			return new TriggerInstance(Optional.empty(), Optional.empty());
		}

		public boolean matches(ItemStack stack) {
			return item.isEmpty() || item.get().test(stack);
		}

	}

}
