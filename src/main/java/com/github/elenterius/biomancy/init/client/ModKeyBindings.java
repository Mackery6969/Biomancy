package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.BiomancyMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public final class ModKeyBindings {

	public static final String MAIN_CATEGORY = BiomancyMod.translationKey("key", "categories.main");
	public static final IKeyConflictContext CONFLICT_CONTEXT = new IKeyConflictContext() {
		@Override
		public boolean isActive() {
			return !KeyConflictContext.GUI.isActive();
		}

		@Override
		public boolean conflicts(IKeyConflictContext other) {
			return this == other;
		}
	};

	public static final KeyMapping EQUIPPED_ARMOR_ACTION = new KeyMapping(
			BiomancyMod.translationKey("key", "equipped_armor_action"),
			CONFLICT_CONTEXT,
			KeyModifier.CONTROL, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, MAIN_CATEGORY
	);
	public static final KeyMapping OFF_HAND_ITEM_ACTION = new KeyMapping(
			BiomancyMod.translationKey("key", "off_hand_item_action"),
			CONFLICT_CONTEXT,
			InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, MAIN_CATEGORY
	);
	public static final KeyMapping MAIN_HAND_ITEM_ACTION = new KeyMapping(
			BiomancyMod.translationKey("key", "main_hand_item_action"),
			CONFLICT_CONTEXT,
			InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, MAIN_CATEGORY
	);

	private ModKeyBindings() {}

	static void register(Consumer<KeyMapping> registry) {
		registry.accept(ModKeyBindings.MAIN_HAND_ITEM_ACTION);
		registry.accept(ModKeyBindings.OFF_HAND_ITEM_ACTION);
		registry.accept(ModKeyBindings.EQUIPPED_ARMOR_ACTION);
	}

}
