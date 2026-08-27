package com.github.elenterius.biomancy.init;

import net.minecraft.world.inventory.RecipeBookType;

public final class ModRecipeBookTypes {

	public static final RecipeBookType BIO_FORGE = RecipeBookType.valueOf("BIOMANCY_BIO_FORGE");

	private ModRecipeBookTypes() {}

	static void init() {
		//force init static fields
	}

}
