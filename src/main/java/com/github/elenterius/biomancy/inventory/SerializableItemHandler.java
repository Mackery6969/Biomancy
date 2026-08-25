package com.github.elenterius.biomancy.inventory;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public interface SerializableItemHandler extends IItemHandlerModifiable, INBTSerializable<CompoundTag> {

}
