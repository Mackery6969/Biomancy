package com.github.elenterius.biomancy.block.digester;

import com.github.elenterius.biomancy.block.base.HorizontalFacingMachineBlock;
import com.github.elenterius.biomancy.block.base.MachineBlockEntity;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.FormatUtil;
import com.github.elenterius.biomancy.util.sounds.SoundUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class DigesterBlock extends HorizontalFacingMachineBlock {

	public static final MapCodec<DigesterBlock> CODEC = simpleCodec(DigesterBlock::new);

	protected static final VoxelShape SHAPE = createShape();

	public DigesterBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends DigesterBlock> codec() {
		return CODEC;
	}

	private static VoxelShape createShape() {
		VoxelShape base = Block.box(3d, 0d, 3d, 13d, 12d, 13d);
		VoxelShape lid = Block.box(5d, 12d, 5d, 11d, 16d, 11d);
		return Shapes.join(base, lid, BooleanOp.OR);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.DIGESTER.get().create(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.DIGESTER.get(), MachineBlockEntity::serverTick);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof DigesterBlockEntity digester && digester.canPlayerInteract(player)) {
			if (!level.isClientSide) {
				((ServerPlayer) player).openMenu(digester, pos);
				SoundUtil.Server.playBlockSound((ServerLevel) level, pos, ModSoundEvents.UI_DIGESTER_OPEN);
			}
			return ItemInteractionResult.SUCCESS;
		}

		return ItemInteractionResult.CONSUME;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(5) != 0) return;
		if (!isCrafting(state)) return;

		int particleAmount = random.nextInt(1, 5);
		int color = 0x867e36; //old moss green
		ColorParticleOption particleOption = ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0xFF000000 | color);
		for (int i = 0; i < particleAmount; i++) {
			level.addParticle(particleOption, pos.getX() + 0.5d + ((random.nextFloat() - random.nextFloat()) * 0.125f), pos.getY() + 0.9d, pos.getZ() + 0.5d + ((random.nextFloat() - random.nextFloat()) * 0.125f), 0d, 0d, 0d);
		}

		if (random.nextInt(3) != 0) return;

		if (!playFoodEatingSound(level, pos, random)) {
			SoundUtil.Client.playBlockSound(level, pos, ModSoundEvents.DIGESTER_CRAFTING_RANDOM, 0.65f);
		}
	}

	public boolean isCrafting(BlockState state) {
		return Boolean.TRUE.equals(state.getValue(CRAFTING));
	}

	private boolean playFoodEatingSound(Level level, BlockPos pos, RandomSource random) {
		if (level.getBlockEntity(pos) instanceof DigesterBlockEntity digester) {
			ItemStack stack = digester.getInputSlotStack();
			if (stack.isEmpty()) return false;

			if (stack.getUseAnimation() == UseAnim.DRINK) {
				SoundUtil.Client.playBlockSound(level, pos, stack.getDrinkingSound(), 0.5F, random.nextFloat() * 0.1F + 0.9F);
			}
			else if (stack.getUseAnimation() == UseAnim.EAT) {
				SoundUtil.Client.playBlockSound(level, pos, stack.getEatingSound(), 0.5f + 0.5f * random.nextInt(2), (random.nextFloat() - random.nextFloat()) * 0.2f + 1f);
			}

			return true;
		}

		return false;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		int fuelAmount = getFuelAmount(stack);
		if (fuelAmount > 0) {
			tooltip.add(ComponentUtil.EMPTY_LINE);
			DecimalFormat df = FormatUtil.getIntegerFormatter();
			tooltip.add(ComponentUtil.translatable("tooltip.biomancy.nutrients_fuel").withStyle(ChatFormatting.GRAY));
			tooltip.add(ComponentUtil.literal("%s/%s u".formatted(df.format(fuelAmount), df.format(DigesterBlockEntity.MAX_FUEL))).withStyle(TextStyles.NUTRIENTS));
		}
	}

	public static int getFuelAmount(ItemStack stack) {
		CustomData customData = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
		if (customData.isEmpty()) return 0;
		CompoundTag tag = customData.copyTag();
		return tag.contains("Fuel") ? tag.getCompound("Fuel").getInt("Amount") : 0;
	}
}
