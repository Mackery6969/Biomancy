package com.github.elenterius.biomancy.client.render.item.injector;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class InjectorModel extends DefaultedItemGeoModel<InjectorItem> {

	public InjectorModel() {
		super(BiomancyMod.rl("injector"));
	}

	@Override
	public RenderType getRenderType(InjectorItem item, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

	@Override
	public boolean crashIfBoneMissing() {
		return true;
	}

}
