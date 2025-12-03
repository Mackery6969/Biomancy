package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import com.github.elenterius.biomancy.init.tags.ModBlockTags;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ExplosionUtil {

	public static ExplosionDamageCalculator DECAY_DAMAGE_CALCULATOR = new ExplosionDamageCalculator() {
		@Override
		public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, FluidState fluidState) {
			if (state.isAir() && fluidState.isEmpty()) {
				return Optional.empty();
			}

			if (state.is(ModBlockTags.DECAY_DESTRUCTIBLE)) {
				float explosionResistance = Math.max(state.getExplosionResistance(level, pos, explosion), fluidState.getExplosionResistance(level, pos, explosion));
				return Optional.of(explosionResistance * 0.25f);
			}

			if (state.canBeReplaced() || state.getExplosionResistance(level, pos, explosion) == 0f) {
				return Optional.empty();
			}

			return Optional.of(3_600_000f);
		}

		@Override
		public boolean shouldBlockExplode(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, float power) {
			return state.is(ModBlockTags.DECAY_DESTRUCTIBLE) || state.canBeReplaced() || state.getExplosionResistance(level, pos, explosion) == 0f;
		}
	};

	public static ExplosionDamageCalculator INCENDIARY_DAMAGE_CALCULATOR = new ExplosionDamageCalculator() {

		@Override
		public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, FluidState fluid) {
			if (state.isAir() && fluid.isEmpty()) return Optional.empty();

			boolean isFlammable = Direction.stream().anyMatch(direction -> state.isFlammable(level, pos, direction));
			float multiplier = isFlammable ? 0.5f : 3f;

			return Optional.of(multiplier * Math.max(state.getExplosionResistance(level, pos, explosion), fluid.getExplosionResistance(level, pos, explosion)));
		}

		@Override
		public boolean shouldBlockExplode(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, float power) {
			return super.shouldBlockExplode(explosion, level, pos, state, power);
		}

	};

	public static void explodeDecay(Level level, @Nullable Entity source, double x, double y, double z, float radius, Level.ExplosionInteraction explosionInteraction) {
		explode(ExplosionType.DECAY, level, source, ModDamageSources.decay(level, source, getIndirectSourceEntity(source)), DECAY_DAMAGE_CALCULATOR, x, y, z, radius, false, explosionInteraction, true);
	}

	public static void explodeIncendiary(Level level, Entity source, float radius, Level.ExplosionInteraction explosionInteraction) {
		explode(ExplosionType.VOLATILE, level, source, ModDamageSources.incendiary(level, source, getIndirectSourceEntity(source)), INCENDIARY_DAMAGE_CALCULATOR, source.getX(), source.getY(), source.getZ(), radius, true, explosionInteraction, true);
	}

	public static void explodeIncendiary(Level level, @Nullable Entity source, double x, double y, double z, float radius, Level.ExplosionInteraction explosionInteraction) {
		explode(ExplosionType.VOLATILE, level, source, ModDamageSources.incendiary(level, source, getIndirectSourceEntity(source)), INCENDIARY_DAMAGE_CALCULATOR, x, y, z, radius, true, explosionInteraction, true);
	}

	public static void explode(ExplosionType explosionType, Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction, boolean spawnParticles) {
		Explosion.BlockInteraction blockInteraction = switch (explosionInteraction) {
			case NONE -> Explosion.BlockInteraction.KEEP;
			case BLOCK -> getDestroyType(level, GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY);
			case MOB -> ForgeEventFactory.getMobGriefingEvent(level, source) ? getDestroyType(level, GameRules.RULE_MOB_EXPLOSION_DROP_DECAY) : Explosion.BlockInteraction.KEEP;
			case TNT -> getDestroyType(level, GameRules.RULE_TNT_EXPLOSION_DROP_DECAY);
		};

		Explosion explosion = explosionType.serverFactory.create(level, source, damageSource, damageCalculator, x, y, z, radius, fire, blockInteraction);

		if (ForgeEventFactory.onExplosionStart(level, explosion)) return;

		explosion.explode();
		explosion.finalizeExplosion(spawnParticles);

		if (level instanceof ServerLevel serverLevel) {
			ModNetworkHandler.sendCustomExplosionToClients(serverLevel, explosionType, explosion);
		}
	}

	private static Explosion.BlockInteraction getDestroyType(Level level, GameRules.Key<GameRules.BooleanValue> gameRule) {
		return level.getGameRules().getBoolean(gameRule) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
	}

	public static @Nullable LivingEntity getIndirectSourceEntity(@Nullable Entity source) {
		if (source == null) {
			return null;
		}

		if (source instanceof LivingEntity livingEntity) {
			return livingEntity;
		}

		if (source instanceof TraceableEntity traceableEntity && traceableEntity.getOwner() instanceof LivingEntity livingEntity) {
			return livingEntity;
		}

		return null;
	}

	public static class VolatileExplosion extends Explosion {

		public VolatileExplosion(Level level, @Nullable Entity source, double x, double y, double z, float radius, List<BlockPos> positions) {
			this(level, source, null, null, x, y, z, radius, false, Explosion.BlockInteraction.DESTROY_WITH_DECAY);
			toBlow.addAll(positions);
		}

		public VolatileExplosion(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean ignoredFire, BlockInteraction interaction) {
			super(level, source, damageSource, damageCalculator, x, y, z, radius, true, interaction); //fire is always true
		}

		@Override
		public void finalizeExplosion(boolean spawnParticles) {
			if (level.isClientSide) {
				level.playLocalSound(x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4f, (1f + (level.random.nextFloat() - level.random.nextFloat()) * 0.2f) * 0.7f, false);
			}

			boolean interactsWithBlocks = interactsWithBlocks();

			if (spawnParticles) {
				if (!(radius < 2f) && interactsWithBlocks) {
					level.addParticle(ModParticleTypes.VOLATILE_EXPLOSION_EMITTER.get(), x, y, z, 1d, 0d, 0d);
				}
				else {
					level.addParticle(ModParticleTypes.VOLATILE_EXPLOSION.get(), x, y, z, 1d, 0d, 0d);
				}
			}

			if (interactsWithBlocks) {
				destroyBlocks();
			}

			placeSplatters();
		}

		protected void destroyBlocks() {
			ObjectArrayList<Pair<ItemStack, BlockPos>> drops = new ObjectArrayList<>();
			boolean isPlayerSource = getIndirectSourceEntity() instanceof Player;

			Util.shuffle(toBlow, level.random);

			for (BlockPos pos : toBlow) {
				BlockState state = level.getBlockState(pos);

				if (state.is(Blocks.MAGMA_BLOCK)) {
					level.setBlock(pos, Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL);
				}
				else if (!state.isAir()) {
					level.getProfiler().push("explosion_blocks");

					if (level instanceof ServerLevel serverlevel && state.canDropFromExplosion(level, pos, this)) {
						LootParams.Builder lootParams = new LootParams.Builder(serverlevel)
								.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
								.withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
								.withOptionalParameter(LootContextParams.BLOCK_ENTITY, state.hasBlockEntity() ? level.getBlockEntity(pos) : null)
								.withOptionalParameter(LootContextParams.THIS_ENTITY, source);

						if (blockInteraction == BlockInteraction.DESTROY_WITH_DECAY) {
							lootParams.withParameter(LootContextParams.EXPLOSION_RADIUS, radius);
						}

						state.spawnAfterBreak(serverlevel, pos, ItemStack.EMPTY, isPlayerSource);
						state.getDrops(lootParams).forEach(stack -> addBlockDrops(drops, stack, pos.immutable()));
					}

					state.onBlockExploded(level, pos, this);
					level.getProfiler().pop();
				}
			}

			for (Pair<ItemStack, BlockPos> pair : drops) {
				Block.popResource(level, pair.getSecond(), pair.getFirst());
			}
		}

		protected void placeSplatters() {
			if (level instanceof ServerLevel serverLevel) {
				for (BlockPos pos : toBlow) {
					if (random.nextInt(3) == 0 && level.getBlockState(pos).isAir()) {
						for (Direction direction : Direction.values()) {
							BlockPos neighborPos = pos.relative(direction);
							BlockState neighborState = level.getBlockState(neighborPos);
							if (neighborState.isFaceSturdy(level, neighborPos, direction.getOpposite())) {
								//isFlammable(level, neighborPos, direction.getOpposite())
								//level.setBlockAndUpdate(pos, BaseFireBlock.getState(level, pos));
								ModBlocks.VOLATILE_SPLATTER.get().spreadSplatter(serverLevel, pos, direction.getOpposite(), random);
								break;
							}
						}
					}
				}
			}
		}

	}

	public static class DecayExplosion extends Explosion {

		public DecayExplosion(Level level, @Nullable Entity source, double x, double y, double z, float radius, List<BlockPos> positions) {
			this(level, source, null, null, x, y, z, radius, false, Explosion.BlockInteraction.DESTROY_WITH_DECAY);
			toBlow.addAll(positions);
		}

		public DecayExplosion(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean ignoredFire, BlockInteraction interaction) {
			super(level, source, damageSource, damageCalculator, x, y, z, radius, false, interaction); //fire is always false
		}

		@Override
		public void finalizeExplosion(boolean spawnParticles) {
			if (level.isClientSide) {
				level.playLocalSound(x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4f, (1f + (level.random.nextFloat() - level.random.nextFloat()) * 0.2f) * 0.7f, false);
			}

			boolean interactsWithBlocks = interactsWithBlocks();

			if (spawnParticles) {
				if (!(radius < 2f) && interactsWithBlocks) {
					level.addParticle(ModParticleTypes.DECAY_EXPLOSION_EMITTER.get(), x, y, z, 1d, 0d, 0d);
				}
				else {
					level.addParticle(ModParticleTypes.DECAY_EXPLOSION.get(), x, y, z, 1d, 0d, 0d);
				}
			}

			if (interactsWithBlocks) {
				destroyBlocks();
			}
		}

		protected void destroyBlocks() {
			ObjectArrayList<Pair<ItemStack, BlockPos>> drops = new ObjectArrayList<>();
			boolean isPlayerSource = getIndirectSourceEntity() instanceof Player;

			Util.shuffle(toBlow, level.random);

			for (BlockPos pos : toBlow) {
				BlockState state = level.getBlockState(pos);

				if (!state.isAir()) {
					level.getProfiler().push("explosion_blocks");

					if (level instanceof ServerLevel serverlevel && state.canDropFromExplosion(level, pos, this)) {
						LootParams.Builder lootParams = new LootParams.Builder(serverlevel)
								.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
								.withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
								.withOptionalParameter(LootContextParams.BLOCK_ENTITY, state.hasBlockEntity() ? level.getBlockEntity(pos) : null)
								.withOptionalParameter(LootContextParams.THIS_ENTITY, source);

						if (blockInteraction == BlockInteraction.DESTROY_WITH_DECAY) {
							lootParams.withParameter(LootContextParams.EXPLOSION_RADIUS, radius);
						}

						state.spawnAfterBreak(serverlevel, pos, ItemStack.EMPTY, isPlayerSource);
						state.getDrops(lootParams).forEach(stack -> addBlockDrops(drops, stack, pos.immutable()));
					}

					state.onBlockExploded(level, pos, this);
					level.getProfiler().pop();
				}
			}

			for (Pair<ItemStack, BlockPos> pair : drops) {
				Block.popResource(level, pair.getSecond(), pair.getFirst());
			}
		}

	}

	public enum ExplosionType {
		VANILLA(Explosion::new, Explosion::new),
		DECAY(DecayExplosion::new, DecayExplosion::new),
		VOLATILE(VolatileExplosion::new, VolatileExplosion::new);

		public final ServerExplosionFactory<? extends Explosion> serverFactory;
		public final ClientExplosionFactory<? extends Explosion> clientFactory;

		<T extends Explosion> ExplosionType(ServerExplosionFactory<T> serverFactory, ClientExplosionFactory<T> clientFactory) {
			this.clientFactory = clientFactory;
			this.serverFactory = serverFactory;
		}

		public byte id() {
			return (byte) ordinal();
		}

		public static ExplosionType fromId(byte id) {
			if (id < 0 || id >= values().length) return VANILLA;
			return values()[id];
		}
	}

	public interface ServerExplosionFactory<T extends Explosion> {
		T create(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Explosion.BlockInteraction interaction);
	}

	public interface ClientExplosionFactory<T extends Explosion> {
		T create(Level level, @Nullable Entity source, double toBlowX, double toBlowY, double toBlowZ, float radius, List<BlockPos> positions);
	}

}
