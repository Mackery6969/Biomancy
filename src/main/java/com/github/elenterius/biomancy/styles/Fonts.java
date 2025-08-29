package com.github.elenterius.biomancy.styles;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.item.KnowledgeReader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

public final class Fonts {

	//	public static final ResourceLocation ILLAGER_RUNES = new ResourceLocation("minecraft", "illageralt");
	//	public static final ResourceLocation STANDARD_GALACTIC_ALPHABET = new ResourceLocation("minecraft", "alt");

	private Fonts() {}

	public static final class PrimordialRunes {

		public static final ResourceLocation CARO_INVITICA = BiomancyMod.rl("caro_invitica");
		public static final int GLYPH_WIDTH = 8;

		/// only updated on the client side
		private static boolean isTranslatable = false;

		private PrimordialRunes() {}

		public static ResourceLocation getId() {
			return CARO_INVITICA;
		}

		public static boolean isTranslatable(ResourceLocation font) {
			return isTranslatable && font.equals(CARO_INVITICA);
		}

		/// needs to be called on the client side
		public static void updateTranslatable(Player player) {
			isTranslatable = player.hasEffect(ModMobEffects.PRIMORDIAL_INFESTATION.get()) || KnowledgeReader.canTranslatePrimordialRunes(player, EquipmentSlot.HEAD);
		}

	}

}
