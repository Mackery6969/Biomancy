package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.integration.ModsCompatHandler;
import com.github.elenterius.biomancy.item.extractor.ExtractorItem;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID)
public final class CommonSetupHandler {

	private CommonSetupHandler() {}

	@SubscribeEvent
	public static void onSetup(final FMLCommonSetupEvent event) {
		ModRecipeBookTypes.init();

		// if not thread safe do it after the common setup event on a single thread
		event.enqueueWork(() -> {

			registerDispenserBehaviors();
			ModRecipes.registerComposterRecipes();

			AcidInteractions.register();
		});

		ModFluids.registerInteractions();
		ModsCompatHandler.onBiomancyCommonSetup(event);
	}

	private static void registerDispenserBehaviors() {
		DispenserBlock.registerBehavior(ModItems.ESSENCE_EXTRACTOR.get(), new OptionalDispenseItemBehavior() {
			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
				setSuccess(ExtractorItem.tryExtractEssence(source.level(), pos, stack));
				if (isSuccess()) {
					stack.hurtAndBreak(1, source.level(), (LivingEntity) null, item -> {});
				}
				return stack;
			}
		});

		DispenserBlock.registerBehavior(ModItems.INJECTOR.get(), new OptionalDispenseItemBehavior() {
			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
				setSuccess(InjectorItem.tryInjectLivingEntity(source.level(), pos, stack));
				if (isSuccess()) {
					stack.hurtAndBreak(1, source.level(), (LivingEntity) null, item -> {});
				}
				return stack;
			}
		});

		DispenserBlock.registerBehavior(ModItems.ACID_BUCKET.get(), new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

			public ItemStack execute(BlockSource source, ItemStack stack) {
				BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
				Level level = source.level();

				DispensibleContainerItem containerItem = (DispensibleContainerItem) stack.getItem();
				if (containerItem.emptyContents(null, level, pos, null, stack)) {
					containerItem.checkExtraContent(null, level, stack, pos);
					return new ItemStack(Items.BUCKET);
				}
				else {
					return defaultDispenseItemBehavior.dispense(source, stack);
				}
			}
		});

		DispenserBlock.registerBehavior(ModItems.TOXIN_GRENADE.get(), new ProjectileDispenseBehavior(ModItems.TOXIN_GRENADE.get()));
		DispenserBlock.registerBehavior(ModItems.ACID_GRENADE.get(), new ProjectileDispenseBehavior(ModItems.ACID_GRENADE.get()));
		DispenserBlock.registerBehavior(ModItems.DECAY_GRENADE.get(), new ProjectileDispenseBehavior(ModItems.DECAY_GRENADE.get()));
		DispenserBlock.registerBehavior(ModItems.INCENDIARY_GRENADE.get(), new ProjectileDispenseBehavior(ModItems.INCENDIARY_GRENADE.get()));
	}

}
