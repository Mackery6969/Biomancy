package com.github.elenterius.biomancy.loot;

import com.github.elenterius.biomancy.init.ModLoot;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Set;

/**
 * Copies the given keys from the source block entity's saved nbt into the
 * {@link DataComponents#BLOCK_ENTITY_DATA} component of the dropped stack.
 */
public class CopyBlockEntityDataFunction extends LootItemConditionalFunction {

	public static final MapCodec<CopyBlockEntityDataFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
			.and(Codec.STRING.listOf().fieldOf("keys").forGetter(function -> function.keys))
			.apply(instance, CopyBlockEntityDataFunction::new));

	private final List<String> keys;

	protected CopyBlockEntityDataFunction(List<LootItemCondition> conditions, List<String> keys) {
		super(conditions);
		this.keys = List.copyOf(keys);
	}

	public static Builder copyData(String... keys) {
		return new Builder(List.of(keys));
	}

	@Override
	public LootItemFunctionType<CopyBlockEntityDataFunction> getType() {
		return ModLoot.COPY_BLOCK_ENTITY_DATA.get();
	}

	@Override
	public Set<LootContextParam<?>> getReferencedContextParams() {
		return Set.of(LootContextParams.BLOCK_ENTITY);
	}

	@Override
	protected ItemStack run(ItemStack stack, LootContext context) {
		BlockEntity blockEntity = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
		if (blockEntity == null) return stack;

		CompoundTag sourceTag = blockEntity.saveWithoutMetadata(context.getLevel().registryAccess());
		CompoundTag targetTag = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();

		boolean copiedAny = false;
		for (String key : keys) {
			if (sourceTag.contains(key)) {
				targetTag.put(key, sourceTag.get(key).copy());
				copiedAny = true;
			}
		}

		if (copiedAny) {
			CustomData.set(DataComponents.BLOCK_ENTITY_DATA, stack, targetTag);
		}

		return stack;
	}

	public static class Builder extends LootItemConditionalFunction.Builder<Builder> {

		private final List<String> keys;

		Builder(List<String> keys) {
			this.keys = keys;
		}

		@Override
		protected Builder getThis() {
			return this;
		}

		@Override
		public CopyBlockEntityDataFunction build() {
			return new CopyBlockEntityDataFunction(getConditions(), keys);
		}
	}

}
