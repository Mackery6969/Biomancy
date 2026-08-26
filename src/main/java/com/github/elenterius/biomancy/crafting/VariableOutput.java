package com.github.elenterius.biomancy.crafting;

import com.github.elenterius.biomancy.crafting.recipe.RecipeUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class VariableOutput {

	public static final Codec<VariableOutput> CODEC = RecipeUtil.jsonBridgeCodec(VariableOutput::serialize, VariableOutput::deserialize);
	public static final StreamCodec<RegistryFriendlyByteBuf, VariableOutput> STREAM_CODEC = StreamCodec.of((buf, value) -> value.toNetwork(buf), VariableOutput::fromNetwork);

	private final Item item;
	private final @Nullable
	CompoundTag tag;
	private final ItemCountRange countRange;

	public VariableOutput(ItemStack stack) {
		this(stack, stack.getCount());
	}

	public VariableOutput(ItemStack stack, int count) {
		this(stack.getItem(), getTag(stack), new ItemCountRange.ConstantValue(count));
	}

	public VariableOutput(ItemStack stack, int min, int max) {
		this(stack.getItem(), getTag(stack), new ItemCountRange.UniformRange(min, max));
	}

	public VariableOutput(ItemStack stack, int n, float p) {
		this(stack.getItem(), getTag(stack), new ItemCountRange.BinomialRange(n, p));
	}

	public VariableOutput(ItemLike item) {
		this(item, 1);
	}

	public VariableOutput(ItemLike item, int count) {
		this(item, new ItemCountRange.ConstantValue(count));
	}

	public VariableOutput(ItemLike item, int min, int max) {
		this(item, new ItemCountRange.UniformRange(min, max));
	}

	public VariableOutput(ItemLike item, int n, float p) {
		this(item, new ItemCountRange.BinomialRange(n, p));
	}

	public VariableOutput(ItemStack stack, ItemCountRange countRange) {
		this(stack.getItem(), getTag(stack), countRange);
	}

	public VariableOutput(ItemLike item, ItemCountRange countRange) {
		this(item, null, countRange);
	}

	public VariableOutput(ItemLike item, @Nullable CompoundTag tag, ItemCountRange countRange) {
		this.item = item.asItem();
		this.tag = tag;
		this.countRange = countRange;
	}

	private static @Nullable CompoundTag getTag(ItemStack stack) {
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		return tag.isEmpty() ? null : tag;
	}

	public Item getItem() {
		return item;
	}

	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(item);
		if (tag != null && !tag.isEmpty()) {
			CustomData.set(DataComponents.CUSTOM_DATA, stack, tag.copy());
		}
		return stack;
	}

	public ItemStack getItemStack(RandomSource rng) {
		int count = getCount(rng);
		if (count < 1) return ItemStack.EMPTY;

		ItemStack stack = new ItemStack(item);
		if (tag != null && !tag.isEmpty()) {
			CustomData.set(DataComponents.CUSTOM_DATA, stack, tag.copy());
		}
		stack.setCount(count);

		return stack;
	}

	public int getCount(RandomSource rng) {
		return countRange.getCount(rng);
	}

	public ItemCountRange getCountRange() {return countRange;}

	public JsonObject serialize() {
		JsonObject result = new JsonObject();
		result.addProperty("item", Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).toString());

		JsonObject obj = new JsonObject();
		ItemCountRange.toJson(obj, countRange);
		result.add("countRange", obj);

		if (tag != null && !tag.isEmpty()) {
			result.addProperty("nbt", tag.getAsString());
		}

		return result;
	}

	public static VariableOutput deserialize(JsonObject jsonObject) {
		ResourceLocation id = ResourceLocation.parse(GsonHelper.getAsString(jsonObject, "item"));
		Item item = BuiltInRegistries.ITEM.get(id);
		ItemCountRange countRange = ItemCountRange.fromJson(GsonHelper.getAsJsonObject(jsonObject, "countRange"));
		if (item == Items.AIR) throw new JsonParseException("Result can't be Empty");
		return new VariableOutput(item, countRange);
	}

	public static VariableOutput fromNetwork(RegistryFriendlyByteBuf buffer) {
		ItemStack stack = ItemStack.STREAM_CODEC.decode(buffer);
		ItemCountRange range = ItemCountRange.fromNetwork(buffer);
		return new VariableOutput(stack, range);
	}

	public void toNetwork(RegistryFriendlyByteBuf buffer) {
		ItemStack.STREAM_CODEC.encode(buffer, getItemStack());
		ItemCountRange.toNetwork(buffer, countRange);
	}

}
