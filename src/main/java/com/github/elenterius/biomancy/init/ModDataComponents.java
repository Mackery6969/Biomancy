package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.inventory.InjectorContents;
import com.github.elenterius.biomancy.item.weapon.gun.GunbladeItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {

	public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, BiomancyMod.MOD_ID);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<InjectorContents>> INJECTOR_CONTENTS = DATA_COMPONENT_TYPES.registerComponentType(
			"injector_contents",
			builder -> builder.persistent(InjectorContents.CODEC).networkSynchronized(InjectorContents.STREAM_CODEC)
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<GunbladeItem.GunbladeMode>> GUNBLADE_MODE = DATA_COMPONENT_TYPES.registerComponentType(
			"gunblade_mode",
			builder -> builder.persistent(GunbladeItem.GunbladeMode.CODEC)
					.networkSynchronized(ByteBufCodecs.idMapper(i -> GunbladeItem.GunbladeMode.values()[i], GunbladeItem.GunbladeMode::ordinal))
	);

	private ModDataComponents() {}

}
