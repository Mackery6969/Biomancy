package com.github.elenterius.biomancy.client.render.entity.mob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.mob.FleshCow;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.loading.math.value.Variable;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FleshCowModel<T extends FleshCow> extends DefaultedEntityGeoModel<T> {

	public FleshCowModel() {
		super(BiomancyMod.rl("mob/flesh_cow"), true);
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
