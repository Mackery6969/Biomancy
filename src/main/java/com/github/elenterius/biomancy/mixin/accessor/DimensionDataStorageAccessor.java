package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;
import java.util.Map;

@Mixin(DimensionDataStorage.class)
public interface DimensionDataStorageAccessor {

	@Accessor("dataFolder")
	File biomancy$getDataFolder();

	@Accessor("cache")
	Map<String, SavedData> biomancy$getCache();

}
