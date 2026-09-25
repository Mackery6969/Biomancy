package com.github.elenterius.biomancy.world.hivemind;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CarriedBiomass(int biomass, int lifeEnergy, int ownEnergy) {

	public static final CarriedBiomass NONE = new CarriedBiomass(0, 0, 0);

	public static final int MAX_BIOMASS = 100;
	public static final int MAX_LIFE_ENERGY = 400;
	public static final int MAX_OWN_ENERGY = 400;

	public static final Codec<CarriedBiomass> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("Biomass").forGetter(CarriedBiomass::biomass),
			Codec.INT.fieldOf("LifeEnergy").forGetter(CarriedBiomass::lifeEnergy),
			Codec.INT.optionalFieldOf("OwnEnergy", 0).forGetter(CarriedBiomass::ownEnergy)
	).apply(instance, CarriedBiomass::new));

	public boolean isGorged() {
		return biomass > 0 || lifeEnergy > 0;
	}

	public boolean isFull() {
		return biomass >= MAX_BIOMASS && lifeEnergy >= MAX_LIFE_ENERGY;
	}

	public boolean hasAnything() {
		return isGorged() || ownEnergy > 0;
	}

	public CarriedBiomass addOwnEnergy(int amount) {
		return new CarriedBiomass(biomass, lifeEnergy, Math.min(ownEnergy + amount, MAX_OWN_ENERGY));
	}

	public CarriedBiomass spendOwnEnergy(int amount) {
		return new CarriedBiomass(biomass, lifeEnergy, Math.max(ownEnergy - amount, 0));
	}

	public CarriedBiomass absorbTribute() {
		return new CarriedBiomass(0, 0, Math.min(ownEnergy + lifeEnergy, MAX_OWN_ENERGY));
	}

	public CarriedBiomass add(int biomass, int lifeEnergy) {
		return new CarriedBiomass(
				Math.min(this.biomass + biomass, MAX_BIOMASS),
				Math.min(this.lifeEnergy + lifeEnergy, MAX_LIFE_ENERGY),
				ownEnergy
		);
	}

}
