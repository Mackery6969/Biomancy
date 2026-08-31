package com.github.elenterius.biomancy.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.JumpPadBlock;
import com.github.elenterius.biomancy.entity.misc.LivingEntityData;
import com.github.elenterius.biomancy.enchantment.LivingEnchantmentEffects;
import com.github.elenterius.biomancy.init.AcidInteractions;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.item.armor.WarriorArmorItem;
import com.github.elenterius.biomancy.serum.FrenzySerum;
import com.github.elenterius.biomancy.styles.Fonts;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID)
public final class LivingEventHandler {

	private LivingEventHandler() {}

	@SubscribeEvent
	public static void onLivingJoinLevel(final EntityJoinLevelEvent event) {
		if (event.getLevel().isClientSide()) return;
		if (event.getEntity() instanceof Mob mob && mob.hasEffect(ModMobEffects.FRENZY)) {
			FrenzySerum.injectAIBehavior(mob);
		}
	}

	@SubscribeEvent
	public static void onLivingTick(final EntityTickEvent.Post event) {
		if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;

		if (livingEntity instanceof LivingEntityData.TransientDataProvider provider) {
			provider.biomancy$getData().tick(livingEntity);
		}

		AcidInteractions.handleEntityInsideAcidFluid(livingEntity);
	}

	@SubscribeEvent
	public static void onPlayerTickPre(final PlayerTickEvent.Pre event) {
		Player player = event.getEntity();
		if (player.level().isClientSide() && player.tickCount % 9 == 0) {
			Fonts.PrimordialRunes.updateTranslatable(player);
		}
	}

	@SubscribeEvent
	public static void onPlayerTickPost(final PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		if (player.level().isClientSide()) return;

		if (player.tickCount % 30 == 0) {
			LivingEnchantmentEffects.repairSelfFeedingItems(player);
			LivingEnchantmentEffects.repairParasiticMetabolismItems(player);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingDeath(final LivingDeathEvent event) {
		LivingEntity livingEntity = event.getEntity();
		if (livingEntity.level() instanceof ServerLevel serverLevel && livingEntity.hasEffect(ModMobEffects.PRIMORDIAL_INFESTATION)) {
			if (livingEntity.isFreezing() || livingEntity.isOnFire()) return;
			PrimordialEcosystem.placeMalignantBlocksOnLivingDeath(serverLevel, livingEntity);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingFall(final LivingFallEvent event) {
		LivingEntity livingEntity = event.getEntity();

		ItemStack stack = livingEntity.getItemBySlot(EquipmentSlot.FEET);
		if (stack.getItem() instanceof WarriorArmorItem armor) {
			armor.onFall(stack, event);
		}

		if (event.isCanceled() || (event.getDamageMultiplier() <= 0f && event.getDistance() <= 0f)) return;

		Level level = livingEntity.level();
		AABB aabb = livingEntity.getBoundingBox();

		if (livingEntity.mainSupportingBlockPos.isPresent()) {
			BlockPos posAbove = livingEntity.mainSupportingBlockPos.get().above();
			BlockState blockState = level.getBlockState(posAbove);
			if (blockState.getBlock() instanceof JumpPadBlock jumpPad && jumpPad.checkIfAABBIntersects(posAbove, blockState, Direction.DOWN, aabb)) {
				event.setDistance(0f);
				event.setDamageMultiplier(0f);
				event.setCanceled(true);
				return;
			}
		}

		double xSize = aabb.getXsize();
		double zSize = aabb.getZsize();
		double deflateX = xSize > 2d ? xSize - 2d : 0d; //clamp large AABB size to min -2 and max 2
		double deflateZ = zSize > 2d ? zSize - 2d : 0d; //clamp large AABB size to min -2 and max 2

		int y = Mth.floor(aabb.minY + JumpPadBlock.EPSILON);
		int x1 = Mth.floor(aabb.minX + deflateX + JumpPadBlock.EPSILON);
		int z1 = Mth.floor(aabb.minZ + deflateZ + JumpPadBlock.EPSILON);
		int x2 = Mth.floor(aabb.maxX - deflateX - JumpPadBlock.EPSILON);
		int z2 = Mth.floor(aabb.maxZ - deflateZ - JumpPadBlock.EPSILON);

		//noinspection deprecation
		if (level.hasChunksAt(x1, y, z1, x2, y, z2)) {
			BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
			for (int x = x1; x <= x2; x++) {
				for (int z = z1; z <= z2; z++) {
					cursor.set(x, y, z);
					BlockState blockState = level.getBlockState(cursor);
					if (blockState.getBlock() instanceof JumpPadBlock jumpPad && jumpPad.checkIfAABBIntersects(cursor, blockState, Direction.DOWN, aabb)) {
						event.setDistance(0f);
						event.setDamageMultiplier(0f);
						event.setCanceled(true);
						return;
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingJump(final LivingEvent.LivingJumpEvent event) {
		LivingEntity livingEntity = event.getEntity();
		if (livingEntity.level().isClientSide) return;

		ItemStack stack = livingEntity.getItemBySlot(EquipmentSlot.LEGS);
		if (stack.getItem() instanceof WarriorArmorItem armor) {
			armor.onJump(stack, livingEntity, livingEntity.isShiftKeyDown());
		}
	}

	public static boolean onAutoSpinHorizontalCollision(final LivingEntity livingEntity) {
		if (livingEntity.level().isClientSide) return false;

		if (livingEntity.onGround() && livingEntity.isShiftKeyDown()) {
			ItemStack stack = livingEntity.getItemBySlot(EquipmentSlot.LEGS);
			if (stack.getItem() instanceof WarriorArmorItem armor) {
				if (armor.onJump(stack, livingEntity, true)) {
					livingEntity.fallDistance = 0f;
					return true;
				}
			}
		}

		return false;
	}

}
