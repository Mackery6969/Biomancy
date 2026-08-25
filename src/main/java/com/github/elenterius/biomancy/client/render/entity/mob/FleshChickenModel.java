package com.github.elenterius.biomancy.client.render.entity.mob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.mob.FleshChicken;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.loading.math.value.Variable;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FleshChickenModel<T extends FleshChicken> extends DefaultedEntityGeoModel<T> {

	public FleshChickenModel() {
		super(BiomancyMod.rl("mob/flesh_chicken"), true);
	}

	@Override
	public void setCustomAnimations(T chicken, long instanceId, AnimationState<T> animationState) {
		super.setCustomAnimations(chicken, instanceId, animationState);

		float wingRotation = getWingRotation(chicken, animationState.getPartialTick());

		GeoBone rightWing = getAnimationProcessor().getBone("right_wing");
		if (rightWing != null) rightWing.setRotZ(wingRotation);

		GeoBone leftWing = getAnimationProcessor().getBone("left_wing");
		if (leftWing != null) leftWing.setRotZ(-wingRotation);
	}

	protected float getWingRotation(T chicken, float partialTicks) {
		float flapProgress = Mth.lerp(partialTicks, chicken.oFlap, chicken.flap);
		float flapSpeed = Mth.lerp(partialTicks, chicken.oFlapSpeed, chicken.flapSpeed);
		return (Mth.sin(flapProgress) + 1f) * flapSpeed;
	}

	@Override
	public void applyMolangQueries(AnimationState<T> animationState, double animTime) {
		super.applyMolangQueries(animationState, animTime);

		T animatable = animationState.getAnimatable();

		MathParser.registerVariable(new Variable("variable.limb_swing", () -> {
			boolean shouldSit = animatable.isPassenger() && (animatable.getVehicle() != null && animatable.getVehicle().shouldRiderSit());

			float limbSwing = 0;

			if (!shouldSit && animatable.isAlive()) {
				limbSwing = animatable.walkAnimation.position(animationState.getPartialTick());
				if (animatable.isBaby()) limbSwing *= 3f;
			}

			return limbSwing;
		}));

		MathParser.registerVariable(new Variable("variable.limb_swing_amount", () -> {
			boolean shouldSit = animatable.isPassenger() && (animatable.getVehicle() != null && animatable.getVehicle().shouldRiderSit());

			float limbSwingAmount = 0;

			if (!shouldSit && animatable.isAlive()) {
				limbSwingAmount = animatable.walkAnimation.speed(animationState.getPartialTick());
				if (limbSwingAmount > 1f) limbSwingAmount = 1f;
			}

			return limbSwingAmount;
		}));
	}

}
