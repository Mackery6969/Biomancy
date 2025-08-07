package com.github.elenterius.biomancy.mixin.client;

import com.github.elenterius.biomancy.styles.Fonts;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(Font.class)
public class FontMixin {

	@Shadow
	@Final
	private Function<ResourceLocation, FontSet> fonts;

	@Inject(method = "getFontSet", at = @At(value = "HEAD"), cancellable = true)
	private void onGetFontSet(ResourceLocation key, CallbackInfoReturnable<FontSet> cir) {
		if (Fonts.PrimordialRunes.isTranslatable(key)) {
			cir.setReturnValue(fonts.apply(Style.DEFAULT_FONT));
		}
	}

}
