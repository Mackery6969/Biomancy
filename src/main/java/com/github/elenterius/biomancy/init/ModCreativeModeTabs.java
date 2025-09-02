package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.livingtool.LivingTool;
import com.github.elenterius.biomancy.api.serum.SerumContainer;
import com.github.elenterius.biomancy.crafting.recipe.PotionSerumRecipes;
import com.github.elenterius.biomancy.item.SerumItem;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class ModCreativeModeTabs {

	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BiomancyMod.MOD_ID);

	public static final RegistryObject<CreativeModeTab> MAIN = register("main", () -> ModItems.TAB_ICON.get().getDefaultInstance(), ModCreativeModeTabs::mainTab);
	public static final RegistryObject<CreativeModeTab> BIO_ALCHEMY = register("bio_alchemy", () -> ModItems.INJECTOR.get().getDefaultInstance(), ModCreativeModeTabs::alchemyTab);

	private ModCreativeModeTabs() {}

	private static CreativeModeTab.Builder mainTab(CreativeModeTab.Builder builder) {
		return builder
				.displayItems((params, output) -> {
					Set<RegistryObject<? extends Item>> hiddenItems = Set.of(
							ModItems.TAB_ICON,
							ModItems.ESSENCE,
							ModItems.GUIDE_BOOK,
							ModItems.DEV_ARM_CANNON,

							ModItems.BIO_LAB,
							ModItems.INJECTOR,
							ModItems.VIAL,
							ModItems.ORGANIC_COMPOUND,
							ModItems.UNSTABLE_COMPOUND,
							ModItems.GENETIC_COMPOUND,
							ModItems.EXOTIC_COMPOUND,
							ModItems.HEALING_ADDITIVE,
							ModItems.DECAYING_ADDITIVE
					);

					Predicate<RegistryObject<Item>> isNotHidden = entry -> !hiddenItems.contains(entry);
					Predicate<RegistryObject<Item>> isNotSerum = entry -> !(entry.get() instanceof SerumContainer);

					ModItems.ITEMS.getEntries().stream()
							.filter(isNotHidden)
							.filter(isNotSerum)
							.forEach(entry -> {
								Item item = entry.get();
								output.accept(item);

								if (item instanceof LivingTool livingTool) {
									ItemStack itemStack = item.getDefaultInstance();
									livingTool.setNutrients(itemStack, Integer.MAX_VALUE);
									output.accept(itemStack);
								}

								if (entry.equals(ModItems.FLESHKIN_CHEST)) {
									output.accept(ModBlocks.FLESHKIN_CHEST.get().createItemStackForCreativeTab());
								}
							});

					for (RegistryObject<Enchantment> entry : ModEnchantments.ENCHANTMENTS.getEntries()) {
						Enchantment enchantment = entry.get();
						output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.getMaxLevel())));
					}
				});
	}

	private static CreativeModeTab.Builder alchemyTab(CreativeModeTab.Builder builder) {
		return builder
				.withTabsBefore(MAIN.getId())
				.displayItems((params, output) -> {
					List<RegistryObject<? extends Item>> includeItems = List.of(
							ModItems.BIO_LAB,
							ModItems.INJECTOR,
							ModItems.VIAL,
							ModItems.ORGANIC_COMPOUND,
							ModItems.UNSTABLE_COMPOUND,
							ModItems.GENETIC_COMPOUND,
							ModItems.EXOTIC_COMPOUND,
							ModItems.HEALING_ADDITIVE,
							ModItems.DECAYING_ADDITIVE
					);
					includeItems.stream().map(RegistryObject::get).forEachOrdered(output::accept);

					ModItems.findItems(SerumItem.class).forEach(output::accept);

					output.accept(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.PRIMORDIAL_INFESTATION.get()));
					output.accept(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.PRIMORDIAL_INFESTATION.get()));
					output.accept(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.PRIMORDIAL_INFESTATION.get()));

					for (Potion potion : PotionSerumRecipes.POTIONS) {
						output.accept(ModItems.POTION_SERUM.get().getInstanceFrom(potion));
					}

				});
	}

	private static RegistryObject<CreativeModeTab> register(String name, Supplier<ItemStack> icon, UnaryOperator<CreativeModeTab.Builder> factory) {
		return CREATIVE_TABS.register(name, () -> factory.apply(CreativeModeTab.builder().icon(icon).title(ComponentUtil.translatable(translationKey(name)))).build());
	}

	private static String translationKey(String name) {
		return "creative_tab." + BiomancyMod.MOD_ID + "." + name;
	}

}
