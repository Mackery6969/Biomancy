package com.github.elenterius.biomancy.serum;

import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModSerums;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class BasicSerum implements Serum {

	private final int color;
	private String translationKey = null;

	protected BasicSerum(int color) {
		this.color = color;
	}

	@Override
	public int getColor(CompoundTag tag) {
		return color;
	}

	@Override
	public boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		return true;
	}

	@Override
	public boolean canAffectPlayerSelf(CompoundTag tag, Player targetSelf) {
		return true;
	}

	@Override
	public String getNameTranslationKey() {
		if (translationKey == null) {
			translationKey = Serum.makeTranslationKey(Objects.requireNonNull(ModSerums.REGISTRY.get().getKey(this)));
		}
		return translationKey;
	}

	public void appendTooltip(CompoundTag tag, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		if (ClientTextUtil.showExtraInfo(tooltip)) {
			tooltip.add(ComponentUtil.translatable(getDescriptionTranslationKey()).withStyle(TextStyles.LORE));
		}
	}

	@Override
	public String toString() {
		return "Serum{name=%s, color=%s}".formatted(ModSerums.REGISTRY.get().getKey(this), Integer.toHexString(color));
	}

}
