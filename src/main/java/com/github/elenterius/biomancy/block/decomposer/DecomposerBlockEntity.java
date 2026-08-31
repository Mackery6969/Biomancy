package com.github.elenterius.biomancy.block.decomposer;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.nutrients.FuelHandler;
import com.github.elenterius.biomancy.api.nutrients.FuelHandlerImpl;
import com.github.elenterius.biomancy.block.base.MachineBlock;
import com.github.elenterius.biomancy.block.base.MachineBlockEntity;
import com.github.elenterius.biomancy.crafting.VariableOutput;
import com.github.elenterius.biomancy.crafting.recipe.DecomposingRecipe;
import com.github.elenterius.biomancy.crafting.recipe.SimpleRecipeType;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.inventory.InventoryHandler;
import com.github.elenterius.biomancy.inventory.InventoryHandlers;
import com.github.elenterius.biomancy.inventory.ItemHandlerUtil;
import com.github.elenterius.biomancy.menu.DecomposerMenu;
import com.github.elenterius.biomancy.util.sounds.LoopingSoundHelper;
import com.github.elenterius.biomancy.util.sounds.SoundUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.keyframe.event.SoundKeyframeEvent;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class DecomposerBlockEntity extends MachineBlockEntity<DecomposingRecipe, DecomposerStateData> implements MenuProvider, GeoBlockEntity {

	public static final int FUEL_SLOTS = 1;
	public static final int INPUT_SLOTS = DecomposingRecipe.MAX_INGREDIENTS;
	public static final int OUTPUT_SLOTS = DecomposingRecipe.MAX_OUTPUTS;

	public static final int MAX_FUEL = 1_000;

	public static final DeferredHolder<RecipeType<?>, SimpleRecipeType.AdvancedRecipeType<DecomposingRecipe>> RECIPE_TYPE = ModRecipes.DECOMPOSING_RECIPE_TYPE;

	protected static final RawAnimation WORKING_ANIM = RawAnimation.begin().thenLoop("decomposer.working");
	protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("decomposer.idle");

	private final DecomposerStateData stateData;
	private final FuelHandlerImpl fuelHandler;
	private final InventoryHandler<?> fuelInventory;
	private final InventoryHandler<?> inputInventory;
	private final InventoryHandler<?> outputInventory;

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private LoopingSoundHelper loopingSoundHelper = LoopingSoundHelper.NULL;

	private @Nullable DecomposerRecipeResult computedRecipeResult;

	public DecomposerBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.DECOMPOSER.get(), pos, state);

		inputInventory = InventoryHandlers.standard(INPUT_SLOTS, this::onInventoryChanged);
		outputInventory = InventoryHandlers.denyInput(OUTPUT_SLOTS, this::onInventoryChanged);

		fuelInventory = InventoryHandlers.filterFuel(FUEL_SLOTS, this::onInventoryChanged);
		fuelHandler = FuelHandlerImpl.createNutrientFuelHandler(MAX_FUEL, this::onInventoryChanged);

		stateData = new DecomposerStateData(fuelHandler);
	}

	public IFluidHandler getFluidConsumer() {
		return fuelHandler.getFluidConsumer();
	}

	@Override
	public void onLoad() {
		if (level != null && level.isClientSide) {
			loopingSoundHelper = SoundUtil.Client.createLoopingSoundHandler();
		}
	}

	@Override
	public Component getDisplayName() {
		return getName();
	}

	@Override
	public Component getName() {
		return BiomancyMod.translatableFrom("container", "decomposer");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return DecomposerMenu.createServerMenu(containerId, playerInventory, this);
	}

	@Override
	public DecomposerStateData getStateData() {
		return stateData;
	}

	@Override
	public InventoryHandler<?> getInputInventory() {
		return inputInventory;
	}

	public InventoryHandler<?> getFuelInventory() {
		return fuelInventory;
	}

	public InventoryHandler<?> getOutputInventory() {
		return outputInventory;
	}

	@Override
	protected FuelHandler getFuelHandler() {
		return fuelHandler;
	}

	@Override
	public ItemStack getStackInFuelSlot() {
		return fuelInventory.getStackInSlot(0);
	}

	@Override
	public void setStackInFuelSlot(ItemStack stack) {
		fuelInventory.setStackInSlot(0, stack);
	}

	@Override
	protected boolean doesRecipeResultFitIntoOutputInv(RecipeHolder<DecomposingRecipe> craftingGoal, ItemStack ignored) {
		DecomposerRecipeResult precomputedResult = getComputedRecipeResult(craftingGoal);
		return ItemHandlerUtil.doAllItemsFit(outputInventory.getRaw(), precomputedResult.items);
	}

	DecomposerRecipeResult getComputedRecipeResult(RecipeHolder<DecomposingRecipe> craftingGoal) {
		if (computedRecipeResult == null || !computedRecipeResult.recipeId.equals(craftingGoal.id())) {
			computedRecipeResult = DecomposerRecipeResult.computeRecipeResult(craftingGoal.value(), craftingGoal.id(), level.random.nextInt());
		}

		return computedRecipeResult;
	}

	@Override
	protected @Nullable RecipeHolder<DecomposingRecipe> resolveRecipeFromInput(Level level) {
		return RECIPE_TYPE.get().getBestRecipeFor(level, inputInventory.getRecipeWrapper()).orElse(null);
	}

	@Override
	protected boolean doesRecipeMatchInput(DecomposingRecipe recipeToTest, Level level) {
		return recipeToTest.matches(inputInventory.getRecipeWrapper(), level);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		stateData.serialize(tag);
		if (computedRecipeResult != null) {
			tag.put("ComputedRecipeResult", computedRecipeResult.serialize());
		}
		tag.put("Fuel", fuelHandler.serializeNBT(registries));
		tag.put("FuelSlots", fuelInventory.serializeNBT(registries));
		tag.put("InputSlots", inputInventory.serializeNBT(registries));
		tag.put("OutputSlots", outputInventory.serializeNBT(registries));
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		stateData.deserialize(tag);
		if (level != null && tag.contains("ComputedRecipeResult")) {
			computedRecipeResult = DecomposerRecipeResult.deserialize(tag.getCompound("ComputedRecipeResult"), level.getRecipeManager());
		}
		fuelHandler.deserializeNBT(registries, tag.getCompound("Fuel"));
		fuelInventory.deserializeNBT(registries, tag.getCompound("FuelSlots"));
		inputInventory.deserializeNBT(registries, tag.getCompound("InputSlots"));
		outputInventory.deserializeNBT(registries, tag.getCompound("OutputSlots"));
	}

	@Override
	public void dropAllInvContents(Level level, BlockPos pos) {
		ItemHandlerUtil.dropContents(level, pos, fuelInventory);
		ItemHandlerUtil.dropContents(level, pos, inputInventory);
		ItemHandlerUtil.dropContents(level, pos, outputInventory);
	}

	@Override
	protected boolean craftRecipe(RecipeHolder<DecomposingRecipe> recipeToCraft, Level level) {
		DecomposerRecipeResult precomputedResult = getComputedRecipeResult(recipeToCraft);

		if (!ItemHandlerUtil.doAllItemsFit(outputInventory.getRaw(), precomputedResult.items)) return false;

		inputInventory.extractItem(0, recipeToCraft.value().getIngredientQuantity().count(), false); //consume input

		for (ItemStack stack : precomputedResult.items) {  //output result
			ItemHandlerUtil.insertItem(outputInventory.getRaw(), stack);
		}
		computedRecipeResult = null;

		SoundUtil.Server.playBlockSound((ServerLevel) level, getBlockPos(), ModSoundEvents.DECOMPOSER_CRAFTING_COMPLETED);

		setChanged();
		return true;
	}

	private <T extends DecomposerBlockEntity> void onSoundKeyframe(final SoundKeyframeEvent<T> event) {
		if (event.getKeyframeData().getSound().equals("eat") && level != null && !isRemoved()) {
			SoundUtil.Client.playBlockSound(level, getBlockPos(), ModSoundEvents.DECOMPOSER_EAT);
		}
	}

	private <T extends DecomposerBlockEntity> PlayState handleAnimationState(final AnimationState<T> event) {
		Boolean isCrafting = event.getAnimatable().getBlockState().getValue(MachineBlock.CRAFTING);

		if (Boolean.TRUE.equals(isCrafting)) {
			event.getController().setAnimation(WORKING_ANIM);
			loopingSoundHelper.startLoop(this, ModSoundEvents.DECOMPOSER_CRAFTING.get(), 0.65f);
		}
		else {
			event.getController().setAnimation(IDLE_ANIM);
			loopingSoundHelper.stopLoop();
		}

		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		AnimationController<DecomposerBlockEntity> controller = new AnimationController<>(this, "controller", 10, this::handleAnimationState);
		controller.setSoundKeyframeHandler(this::onSoundKeyframe);
		controllers.add(controller);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public void setRemoved() {
		if (level != null && level.isClientSide) {
			loopingSoundHelper.clear();
		}
		super.setRemoved();
	}

	record DecomposerRecipeResult(ResourceLocation recipeId, int seed, List<ItemStack> items) {

		@Nullable
		public static DecomposerRecipeResult deserialize(CompoundTag tag, RecipeManager recipeManager) {
			String id = tag.getString("recipeId");
			ResourceLocation recipeId = ResourceLocation.tryParse(id);
			if (recipeId == null) return null;

			return recipeManager.byKey(recipeId)
					.map(RecipeHolder::value)
					.filter(DecomposingRecipe.class::isInstance)
					.map(DecomposingRecipe.class::cast)
					.map(recipe -> computeRecipeResult(recipe, recipeId, tag.getInt("seed")))
					.orElse(null);
		}

		public static DecomposerRecipeResult computeRecipeResult(DecomposingRecipe recipe, ResourceLocation recipeId, int seed) {
			RandomSource random = RandomSource.create(seed);

			List<ItemStack> items = new ArrayList<>();
			for (VariableOutput output : recipe.getOutputs()) {
				ItemStack stack = output.getItemStack(random);
				if (!stack.isEmpty()) items.add(stack);
			}

			return new DecomposerRecipeResult(recipeId, seed, items);
		}

		public CompoundTag serialize() {
			CompoundTag tag = new CompoundTag();
			tag.putString("recipeId", recipeId().toString());
			tag.putInt("seed", seed);
			return tag;
		}

	}

}
