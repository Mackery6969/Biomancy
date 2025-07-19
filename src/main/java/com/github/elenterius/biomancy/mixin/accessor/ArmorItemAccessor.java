package com.github.elenterius.biomancy.mixin.accessor;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.EnumMap;
import java.util.UUID;

@Mixin(ArmorItem.class)
public interface ArmorItemAccessor {

	@NotNull
	@Accessor("ARMOR_MODIFIER_UUID_PER_TYPE")
	static EnumMap<ArmorItem.Type, UUID> biomancy$ARMOR_MODIFIER_UUID_PER_TYPE() {
		//noinspection DataFlowIssue
		return null;
	}

	@Accessor("defaultModifiers")
	Multimap<Attribute, AttributeModifier> biomancy$getDefaultModifiers();

	@Mutable
	@Accessor("defaultModifiers")
	void biomancy$setDefaultModifiers(Multimap<Attribute, AttributeModifier> modifiers);

}
