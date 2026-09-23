package com.github.elenterius.biomancy.gametest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BiomancyMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SerumTargetGameTests {

	private SerumTargetGameTests() {}

	@GameTest(template = "empty_platform")
	public static void dispenserSerumsResizeArmorStands(GameTestHelper helper) {
		BlockPos pos = new BlockPos(4, 2, 4);
		ArmorStand stand = helper.spawn(EntityType.ARMOR_STAND, pos);
		ItemStack injector = ModItems.INJECTOR.get().getDefaultInstance();
		InjectorItem.getItemHandler(injector).orElseThrow().setStack(ModItems.SHRINKING_SERUM.get().getDefaultInstance());

		helper.assertTrue(InjectorItem.tryInjectLivingEntity(helper.getLevel(), helper.absolutePos(pos), injector),
				"shrinking serum rejected the armor stand");
		helper.assertTrue(stand.isSmall(), "shrinking serum did not make the armor stand small");
		helper.assertTrue(ModItems.INJECTOR.get().getStoredItemStack(injector).isEmpty(),
				"shrinking the armor stand did not consume the dose");

		InjectorItem.getItemHandler(injector).orElseThrow().setStack(ModItems.ENLARGEMENT_SERUM.get().getDefaultInstance());
		helper.assertTrue(InjectorItem.tryInjectLivingEntity(helper.getLevel(), helper.absolutePos(pos), injector),
				"enlargement serum rejected the small armor stand");
		helper.assertFalse(stand.isSmall(), "enlargement serum did not restore the armor stand's size");
		helper.assertTrue(ModItems.INJECTOR.get().getStoredItemStack(injector).isEmpty(),
				"enlarging the armor stand did not consume the dose");
		helper.succeed();
	}

}
