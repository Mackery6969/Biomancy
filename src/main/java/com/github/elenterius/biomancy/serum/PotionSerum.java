package com.github.elenterius.biomancy.serum;

import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.init.ModSerums;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class PotionSerum implements Serum {

	public static final String COLOR_TAG = "color";
	public static final String POTION_TAG = "potion";
	public static final String STATUS_EFFECTS_TAG = "status_effects";
	public static final String NAME_TAG = "name";

	public PotionSerum() {}

	@Override
	public int getColor(CompoundTag tag) {
		if (!tag.contains(COLOR_TAG, Tag.TAG_INT)) return Serum.EMPTY_COLOR;
		return tag.getInt(COLOR_TAG);
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
	public void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		applyPotionEffects(level, tag, source, target);
	}

	@Override
	public void affectPlayerSelf(ServerLevel level, CompoundTag tag, ServerPlayer targetSelf) {
		applyPotionEffects(level, tag, targetSelf, targetSelf);
	}

	private void applyPotionEffects(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		List<MobEffectInstance> effects = getAllEffects(tag);

		for (MobEffectInstance effectInstance : effects) {
			if (effectInstance.getEffect().isInstantenous()) {
				effectInstance.getEffect().applyInstantenousEffect(source, source, target, effectInstance.getAmplifier(), 1);
			}
			else {
				target.addEffect(new MobEffectInstance(effectInstance), source); //we need to create a new instance because it could be tied to a potion
			}
		}

		if (effects.isEmpty() && getPotion(tag) == Potions.WATER) {
			if (target.isSensitiveToWater()) {
				//noinspection DataFlowIssue
				target.hurt(level.damageSources().indirectMagic(source, source), 1f);
			}

			if (target.isOnFire() && target.isAlive()) {
				target.extinguishFire();
			}

			if (target instanceof Axolotl axolotl) {
				axolotl.rehydrate();
			}
		}
	}

	@Override
	public String getNameTranslationKey() {
		return Serum.makeTranslationKey(Objects.requireNonNull(ModSerums.REGISTRY.get().getKey(this)));
	}

	@Override
	public MutableComponent getDisplayName(CompoundTag tag) {
		String translationKey = tag.contains(NAME_TAG, Tag.TAG_STRING) ? tag.getString(NAME_TAG) : getNameTranslationKey();
		return ComponentUtil.translatable(translationKey);
	}

	public void appendTooltip(CompoundTag tag, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		PotionUtils.addPotionTooltip(getAllEffects(tag), tooltip, 1f);
	}

	@Override
	public String toString() {
		return "Serum{name=%s}".formatted(ModSerums.REGISTRY.get().getKey(this));
	}

	public static void setData(CompoundTag tag, Potion potion, Collection<MobEffectInstance> statusEffects) {
		setPotion(tag, potion);
		setStatusEffects(tag, statusEffects);

		List<MobEffectInstance> allEffects = new ArrayList<>();
		allEffects.addAll(getPotion(tag).getEffects());
		allEffects.addAll(statusEffects);

		tag.putInt(COLOR_TAG, computeColor(potion, allEffects));

		MobEffect primaryEffect = getPrimaryEffect(allEffects);
		if (primaryEffect != null) {
			tag.putString(NAME_TAG, primaryEffect.getDescriptionId());
		}
	}

	public static void setData(CompoundTag tag, Potion potion, Collection<MobEffectInstance> statusEffects, int color) {
		setPotion(tag, potion);
		setStatusEffects(tag, statusEffects);
		tag.putInt(COLOR_TAG, color);

		List<MobEffectInstance> allEffects = new ArrayList<>();
		allEffects.addAll(getPotion(tag).getEffects());
		allEffects.addAll(statusEffects);

		MobEffect primaryEffect = getPrimaryEffect(allEffects);
		if (primaryEffect != null) {
			tag.putString(NAME_TAG, primaryEffect.getDescriptionId());
		}
	}

	public static void setPotion(CompoundTag tag, Potion potion) {
		if (potion == Potions.EMPTY) {
			tag.remove(POTION_TAG);
		}
		else {
			tag.putString(POTION_TAG, BuiltInRegistries.POTION.getKey(potion).toString());
		}
	}

	public static Potion getPotion(CompoundTag tag) {
		return Potion.byName(tag.getString(POTION_TAG));
	}

	public static void setStatusEffects(CompoundTag tag, Collection<MobEffectInstance> effects) {
		if (effects.isEmpty()) return;

		ListTag list = new ListTag();

		for (MobEffectInstance mobeffectinstance : effects) {
			list.add(mobeffectinstance.save(new CompoundTag()));
		}

		tag.put(STATUS_EFFECTS_TAG, list);
	}

	public static void addStatusEffects(CompoundTag tag, Collection<MobEffectInstance> effects) {
		if (effects.isEmpty()) return;

		ListTag list = tag.getList(STATUS_EFFECTS_TAG, Tag.TAG_LIST);

		for (MobEffectInstance mobeffectinstance : effects) {
			list.add(mobeffectinstance.save(new CompoundTag()));
		}

		tag.put(STATUS_EFFECTS_TAG, list);
	}

	public static List<MobEffectInstance> getStatusEffects(CompoundTag tag) {
		if (!tag.contains(STATUS_EFFECTS_TAG, Tag.TAG_LIST)) return new ArrayList<>();

		List<MobEffectInstance> list = new ArrayList<>();

		ListTag tagList = tag.getList(STATUS_EFFECTS_TAG, Tag.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			MobEffectInstance instance = MobEffectInstance.load(tagList.getCompound(i));
			if (instance != null) list.add(instance);
		}

		return list;
	}

	public static List<MobEffectInstance> getAllEffects(CompoundTag tag) {
		List<MobEffectInstance> effects = getStatusEffects(tag);
		effects.addAll(0, getPotion(tag).getEffects());
		return effects;
	}

	public static int computeColor(Potion potion, List<MobEffectInstance> allEffects) {
		if (potion == EMPTY && allEffects.isEmpty()) {
			return 0x18CCE5;
		}
		else {
			return PotionUtils.getColor(allEffects);
		}
	}

	@Nullable
	public static MobEffect getPrimaryEffect(Collection<MobEffectInstance> allEffects) {
		MobEffectInstance instance = getPrimaryEffectInstance(allEffects);
		return instance != null ? instance.getEffect() : null;
	}

	@Nullable
	public static MobEffectInstance getPrimaryEffectInstance(Collection<MobEffectInstance> allEffects) {
		if (allEffects.isEmpty()) return null;

		Iterator<MobEffectInstance> iterator = allEffects.iterator();
		MobEffectInstance primary = iterator.next();

		while (iterator.hasNext()) {
			MobEffectInstance instance = iterator.next();
			boolean isInstanceHarmful = instance.getEffect().getCategory() == MobEffectCategory.HARMFUL;
			boolean isPrimaryHarmful = primary.getEffect().getCategory() == MobEffectCategory.HARMFUL;

			if (isInstanceHarmful && !isPrimaryHarmful) {
				continue;
			}

			if (isPrimaryHarmful && !isInstanceHarmful) {
				primary = instance;
				continue;
			}

			int amplifier = instance.getAmplifier();
			if (amplifier > primary.getAmplifier()) {
				primary = instance;
			}
			else if (amplifier == primary.getAmplifier() && !primary.isInfiniteDuration()) {
				if (instance.isInfiniteDuration() || instance.getDuration() > primary.getDuration()) {
					primary = instance;
				}
			}
		}

		return primary;
	}

}
