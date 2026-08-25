package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import net.mcreator.sonsofsins.SonsOfSinsMod;
import net.mcreator.sonsofsins.init.SonsOfSinsModEntities;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModDespoilLoot extends DespoilLootProvider {

	protected static final Set<EntityType<?>> BONE_MARROW_MOBS = Set.of(
			EntityType.SKELETON_HORSE, EntityType.SKELETON, EntityType.STRAY,
			EntityType.WARDEN,

			SonsOfSinsModEntities.WISTIVER.get(), SonsOfSinsModEntities.DEVOURER.get()
	);

	protected static final Set<EntityType<?>> WITHERED_BONE_MARROW_MOBS = Set.of(
			EntityType.WITHER_SKELETON, EntityType.WITHER
	);

	protected static final Set<EntityType<?>> TOXIC_MOBS = Set.of(
			EntityType.CAVE_SPIDER,
			EntityType.PUFFERFISH,
			EntityType.BEE
	);

	protected static final Set<EntityType<?>> VOLATILE_MOBS = Set.of(
			EntityType.CREEPER,
			EntityType.GHAST, EntityType.BLAZE,
			EntityType.WITHER, EntityType.ENDER_DRAGON
	);

	protected static final Set<EntityType<?>> SHARP_CLAW_MOBS = Set.of(
			EntityType.BAT, EntityType.PARROT,
			EntityType.CAT, EntityType.OCELOT,
			EntityType.WOLF, EntityType.FOX,
			EntityType.POLAR_BEAR, EntityType.PANDA,
			EntityType.ENDER_DRAGON,

			SonsOfSinsModEntities.GUZZLER.get()
	);

	protected static final Set<EntityType<?>> SHARP_FANG_MOBS = Set.of(
			EntityType.BAT,
			EntityType.CAT, EntityType.OCELOT,
			EntityType.WOLF, EntityType.FOX,
			EntityType.POLAR_BEAR, EntityType.PANDA,
			EntityType.HOGLIN, EntityType.ZOGLIN,
			EntityType.ENDER_DRAGON,

			SonsOfSinsModEntities.NIBBLER.get(), SonsOfSinsModEntities.GUZZLER.get()
	);

	protected static final Set<EntityType<?>> INVALID_MOBS_FOR_MEATY_LOOT = Set.of(
			EntityType.SLIME, EntityType.MAGMA_CUBE,
			EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.SHULKER,
			EntityType.VEX, EntityType.GHAST, EntityType.ALLAY, EntityType.PHANTOM,
			EntityType.BLAZE,
			EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE, EntityType.ZOMBIE_HORSE, EntityType.ZOMBIE_VILLAGER,
			EntityType.SKELETON, EntityType.SKELETON_HORSE, EntityType.STRAY, EntityType.WITHER_SKELETON, EntityType.WITHER,
			EntityType.WARDEN,
			EntityType.CREEPER,

			SonsOfSinsModEntities.GRUB_ORGANS.get(), SonsOfSinsModEntities.GUZZLER_ORGANS.get(), SonsOfSinsModEntities.GULBER_ORGANS.get(), SonsOfSinsModEntities.NIBBLER_ORGANS.get(), SonsOfSinsModEntities.DEVOURER_ORGANS.get(),
			SonsOfSinsModEntities.CURSE.get(), SonsOfSinsModEntities.WALKING_BED.get()
	);

	@Override
	public void generate() {
		Set<String> validNamespaces = Set.of("minecraft", BiomancyMod.MOD_ID, SonsOfSinsMod.MODID);
		Predicate<EntityType<?>> allowedNamespace = entityType -> validNamespaces.contains(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(entityType)).getNamespace());

		Predicate<EntityType<?>> validEntityType = entityType -> entityType.getCategory() != MobCategory.MISC; //excludes Players & Villagers as well
		Predicate<EntityType<?>> ignoreEntityType = entityType -> entityType != EntityType.WARDEN;

		BuiltInRegistries.ENTITY_TYPE.getValues().stream()
				.filter(allowedNamespace)
				.filter(validEntityType)
				.filter(ignoreEntityType)
				.forEach(this::add);

		add(EntityType.PLAYER);
		add(EntityType.VILLAGER);

		add(EntityType.WARDEN, lootTable -> lootTable.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(Items.ECHO_SHARD))));
	}

	protected void add(EntityType<?> entityType) {
		add(entityType, createLootTable(entityType));
	}

	protected void add(EntityType<?> entityType, Consumer<LootTable.Builder> consumer) {
		LootTable.Builder lootTable = createLootTable(entityType);
		consumer.accept(lootTable);
		add(entityType, lootTable);
	}

	protected LootTable.Builder createLootTable(EntityType<?> entityType) {
		LootTable.Builder lootTable = LootTable.lootTable();

		//noinspection unchecked
		EntityType<? extends LivingEntity> livingEntityType = (EntityType<? extends LivingEntity>) entityType;

		createCommonPool(livingEntityType).ifPresent(lootTable::withPool);
		createSpecialPool(livingEntityType).ifPresent(lootTable::withPool);

		return lootTable;
	}

	protected Optional<LootPool.Builder> createCommonPool(EntityType<? extends LivingEntity> entityType) {
		LootPool.Builder builder = LootPool.lootPool().setRolls(ConstantValue.exactly(1));
		boolean hasLoot = false;

		AttributeSupplier baseAttributes = DefaultAttributes.getSupplier(entityType);
		final double attackDamage = baseAttributes.hasAttribute(Attributes.ATTACK_DAMAGE) ? baseAttributes.getValue(Attributes.ATTACK_DAMAGE) : 0;

		final float volume = entityType.getWidth() * entityType.getHeight() * entityType.getWidth();
		final float fangMultiplier = 0.825f;
		final float clawMultiplier = 7f;
		final float marrowMultiplier = 2.9f;
		final float sinewMultiplier = 7f;
		final float bileGlandMultiplier = 0.5f;

		final boolean hasToxinGland = TOXIC_MOBS.contains(entityType);
		final boolean hasVolatileGland = VOLATILE_MOBS.contains(entityType);

		if (SHARP_FANG_MOBS.contains(entityType)) {
			int minCount = 1;
			int maxCount = Mth.ceil(Math.log(volume * fangMultiplier + 1));

			if (attackDamage >= 5d) minCount += 1;
			if (attackDamage >= 3d) maxCount += 1;

			NumberProvider countProvider = maxCount > minCount ? UniformGenerator.between(minCount, maxCount) : ConstantValue.exactly(minCount);

			builder.add(
					LootItem.lootTableItem(ModItems.MOB_FANG.get()).setWeight(144)
							.apply(SetItemCountFunction.setCount(countProvider))
							.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
			);

			despoilDropSources.computeIfAbsent(ModItems.MOB_FANG.get(), k -> new HashSet<>()).add(entityType);

			hasLoot = true;
		}

		if (SHARP_CLAW_MOBS.contains(entityType)) {
			int minCount = 1;
			int maxCount = Mth.ceil(Math.log(volume * clawMultiplier + 1));

			if (attackDamage >= 5d) minCount += 1;
			if (attackDamage >= 3d) maxCount += 1;

			NumberProvider countProvider = maxCount > minCount ? UniformGenerator.between(minCount, maxCount) : ConstantValue.exactly(minCount);

			builder.add(
					LootItem.lootTableItem(ModItems.MOB_CLAW.get()).setWeight(150)
							.apply(SetItemCountFunction.setCount(countProvider))
							.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
			);

			despoilDropSources.computeIfAbsent(ModItems.MOB_CLAW.get(), k -> new HashSet<>()).add(entityType);

			hasLoot = true;
		}

		if (volume >= 0.25f && !INVALID_MOBS_FOR_MEATY_LOOT.contains(entityType)) {
			int maxCount = Mth.ceil(Math.log(volume * sinewMultiplier + 1));
			NumberProvider countProvider = maxCount > 1 ? UniformGenerator.between(1, maxCount) : ConstantValue.exactly(1);

			builder.add(
					LootItem.lootTableItem(ModItems.MOB_SINEW.get()).setWeight(50)
							.apply(SetItemCountFunction.setCount(countProvider))
							.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
			);

			despoilDropSources.computeIfAbsent(ModItems.MOB_SINEW.get(), k -> new HashSet<>()).add(entityType);

			hasLoot = true;
		}

		if (volume >= 0.25f && !hasToxinGland && !hasVolatileGland && !INVALID_MOBS_FOR_MEATY_LOOT.contains(entityType)) {
			int maxCount = Mth.ceil(Math.log(volume * bileGlandMultiplier + 1));
			NumberProvider countProvider = UniformGenerator.between(0, maxCount);

			int weight = 40;

			if (!baseAttributes.hasAttribute(Attributes.ATTACK_DAMAGE) || baseAttributes.getValue(Attributes.ATTACK_DAMAGE) <= 0.125d) {
				weight += 10;
			}

			builder.add(
					LootItem.lootTableItem(ModItems.GENERIC_MOB_GLAND.get()).setWeight(weight)
							.apply(SetItemCountFunction.setCount(countProvider))
//							.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
			);

			despoilDropSources.computeIfAbsent(ModItems.GENERIC_MOB_GLAND.get(), k -> new HashSet<>()).add(entityType);

			hasLoot = true;
		}

		if (BONE_MARROW_MOBS.contains(entityType)) {
			int maxCount = Mth.ceil(Math.log(volume * marrowMultiplier + 1));
			NumberProvider countProvider = maxCount > 1 ? UniformGenerator.between(1, maxCount) : ConstantValue.exactly(1);

			builder.add(
					LootItem.lootTableItem(ModItems.MOB_MARROW.get()).setWeight(45)
							.apply(SetItemCountFunction.setCount(countProvider))
							.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
			);

			despoilDropSources.computeIfAbsent(ModItems.MOB_MARROW.get(), k -> new HashSet<>()).add(entityType);

			hasLoot = true;
		}

		if (!hasLoot) return Optional.empty();

		return Optional.of(builder);
	}

	protected Optional<LootPool.Builder> createSpecialPool(EntityType<? extends LivingEntity> entityType) {
		LootPool.Builder builder = LootPool.lootPool().setRolls(ConstantValue.exactly(1));
		boolean hasLoot = false;

		final float volume = entityType.getWidth() * entityType.getHeight() * entityType.getWidth();
		final float organMultiplier = 0.9f;
		final float witheredMarrowMultiplier = 3f;

		boolean hasToxinGland = TOXIC_MOBS.contains(entityType);
		boolean hasVolatileGland = VOLATILE_MOBS.contains(entityType);

		if (hasToxinGland) {
			int maxCount = Mth.ceil(Math.log(volume * organMultiplier + 1));
			NumberProvider countProvider = maxCount > 1 ? UniformGenerator.between(1, maxCount) : ConstantValue.exactly(1);

			builder.add(
					LootItem.lootTableItem(ModItems.TOXIN_GLAND.get()).setWeight(75)
							.apply(SetItemCountFunction.setCount(countProvider))
//							.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
			);

			despoilDropSources.computeIfAbsent(ModItems.TOXIN_GLAND.get(), k -> new HashSet<>()).add(entityType);

			hasLoot = true;
		}

		if (hasVolatileGland) {
			int maxCount = Mth.ceil(Math.log(volume * organMultiplier + 1));
			NumberProvider countProvider = maxCount > 1 ? UniformGenerator.between(1, maxCount) : ConstantValue.exactly(1);

			builder.add(
					LootItem.lootTableItem(ModItems.VOLATILE_GLAND.get()).setWeight(50)
							.apply(SetItemCountFunction.setCount(countProvider))
//							.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
			);

			despoilDropSources.computeIfAbsent(ModItems.VOLATILE_GLAND.get(), k -> new HashSet<>()).add(entityType);

			hasLoot = true;
		}

		if (WITHERED_BONE_MARROW_MOBS.contains(entityType)) {
			int maxCount = Mth.ceil(Math.log(volume * witheredMarrowMultiplier + 1));
			NumberProvider countProvider = maxCount > 1 ? UniformGenerator.between(1, maxCount) : ConstantValue.exactly(1);

			builder.add(
					LootItem.lootTableItem(ModItems.WITHERED_MOB_MARROW.get()).setWeight(65)
							.apply(SetItemCountFunction.setCount(countProvider))
							.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 1)))
			);

			despoilDropSources.computeIfAbsent(ModItems.WITHERED_MOB_MARROW.get(), k -> new HashSet<>()).add(entityType);

			hasLoot = true;
		}

		if (!hasLoot) return Optional.empty();

		return Optional.of(builder);
	}

	protected Map<Item, Set<EntityType<?>>> despoilDropSources = new HashMap<>();

}
