package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.entity.projectile.ThrownAcidicEgg;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AcidicEggItem extends Item {

	public AcidicEggItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (level.isClientSide) {
			return InteractionResultHolder.success(stack);
		}

		ThrownAcidicEgg thrownEgg = new ThrownAcidicEgg(level, player);
		thrownEgg.setItem(stack);
		thrownEgg.shootFromRotation(player, player.getXRot(), player.getYRot(), 0f, 1.5f, 1f);
		level.addFreshEntity(thrownEgg);

		level.playSound(null, player.getX(), player.getY(0.5d), player.getZ(), SoundEvents.EGG_THROW, player.getSoundSource(), 0.5f, 0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f));

		player.awardStat(Stats.ITEM_USED.get(this));

		if (!player.getAbilities().instabuild) {
			stack.shrink(1);
		}

		return InteractionResultHolder.consume(stack);
	}

}