package com.github.elenterius.biomancy.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record InjectorContents(ItemStack stack, int amount) {

	public static final InjectorContents EMPTY = new InjectorContents(ItemStack.EMPTY, 0);

	public static final Codec<InjectorContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ItemStack.CODEC.optionalFieldOf("item", ItemStack.EMPTY).forGetter(InjectorContents::stack),
			Codec.INT.optionalFieldOf("amount", 0).forGetter(InjectorContents::amount)
	).apply(instance, InjectorContents::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, InjectorContents> STREAM_CODEC = StreamCodec.composite(
			ItemStack.OPTIONAL_STREAM_CODEC, InjectorContents::stack,
			ByteBufCodecs.VAR_INT, InjectorContents::amount,
			InjectorContents::new
	);

	public boolean isEmpty() {
		return stack.isEmpty() || amount <= 0;
	}

}
