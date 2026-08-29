package com.github.elenterius.biomancy.block.base;

import com.github.elenterius.biomancy.util.PlayerInteractionPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class SimpleContainerBlockEntity extends BlockEntity implements MenuProvider, PlayerInteractionPredicate, Nameable {

	protected Component name;

	protected SimpleContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public boolean canPlayerInteract(Player player) {
		if (isRemoved()) return false;
		if (level == null || level.getBlockEntity(worldPosition) != this) return false;
		return player.distanceToSqr(Vec3.atCenterOf(worldPosition)) < 8d * 8d;
	}

	@Override
	public Component getName() {
		return name != null ? name : getDefaultName();
	}

	@Override
	public Component getDisplayName() {
		return getName();
	}

	@Nullable
	@Override
	public Component getCustomName() {
		return name;
	}

	public void setCustomName(Component name) {
		this.name = name;
	}

	public abstract Component getDefaultName();

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (name != null) {
			tag.putString("CustomName", Component.Serializer.toJson(name, registries));
		}
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if (tag.contains("CustomName", Tag.TAG_STRING)) {
			name = Component.Serializer.fromJson(tag.getString("CustomName"), registries);
		}
	}

	public abstract void dropContainerContents(Level level, BlockPos pos);

}
