package com.github.elenterius.biomancy.client.render.item.armor;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.item.armor.WarriorArmorItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class WarriorArmorRenderer extends GeoArmorRenderer<WarriorArmorItem> {

	public WarriorArmorRenderer() {
		super(new DefaultedItemGeoModel<>(BiomancyMod.createRL("armor/warrior_armor")));
	}

}