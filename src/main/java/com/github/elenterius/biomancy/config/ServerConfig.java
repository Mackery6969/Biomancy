package com.github.elenterius.biomancy.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {

	public final ModConfigSpec.BooleanValue doBioForgeRecipeProgression;
	public final ModConfigSpec.BooleanValue addTradesToVillagers;
	public final ModConfigSpec.BooleanValue addTradesToWanderingTrader;
	public final ModConfigSpec.EnumValue<PrimalEnergySettings.SupplyAmount> primalEnergySupplyOfCradle;

	public final ModConfigSpec.DoubleValue absorptionMaxHearts;
	public final ModConfigSpec.DoubleValue absorptionHearts;

	public final ModConfigSpec.DoubleValue pehkuiMaxScale;
	public final ModConfigSpec.DoubleValue pehkuiMinScale;
	public final ModConfigSpec.DoubleValue pehkuiScaleIncrement;
	public final ModConfigSpec.DoubleValue pehkuiScaleDecrement;

	public ServerConfig(ModConfigSpec.Builder builder) {
		builder.push("recipes");
		doBioForgeRecipeProgression = builder
				.comment("Determines if the BioForge recipes need to be unlocked to be able to craft them")
				.define("doBioForgeRecipeProgression", true);
		builder.pop();

		builder.push("trades");
		addTradesToVillagers = builder
				.comment("Determines if villagers will sell biomancy items")
				.define("addTradesToVillagers", true);

		addTradesToWanderingTrader = builder
				.comment("Determines if wandering traders will sell biomancy items")
				.define("addTradesToWanderingTrader", true);
		builder.pop();

		builder.push("flesh-growth");
		primalEnergySupplyOfCradle = builder
				.comment("Determines how much primal energy the Cradle can supply to nearby malignant flesh veins")
				.defineEnum("primalEnergySupplyOfCradle", PrimalEnergySettings.SupplyAmount.LIMITED);
		builder.pop();

		builder.push("absorption-serum");
		absorptionMaxHearts = builder
				.comment("Maximum number of absorption hearts.")
				.defineInRange("maxHearts", 10d, 0.5d, 1000d);
		absorptionHearts = builder
				.comment("How many absorption hearts to add on each injection.")
				.defineInRange("heartsIncrement", 2d, 0.5d, 100d);
		builder.pop();

		builder.push("pehkui-integration");
		builder.push("enlargement-serum");
		pehkuiMaxScale = builder
				.comment("Maximum scale a mob/player can reach. A value of 2.0 doubles the size of the mob/player.")
				.defineInRange("maxScale", 2d, 0.01d, 100d);
		pehkuiScaleIncrement = builder
				.comment("How much to add to the scale on each injection.")
				.defineInRange("scaleStep", 0.25d, 0.01d, 100d);
		builder.pop();
		builder.push("shrinking-serum");
		pehkuiMinScale = builder
				.comment("Minimum scale a mob/player can reach. A value of 0.5 half's the size of the mob/player.")
				.defineInRange("minScale", 0.25d, 0.01d, 100d);
		pehkuiScaleDecrement = builder
				.comment("How much to subtract from the scale on each injection.")
				.defineInRange("scaleStep", 0.25d, 0.01d, 100d);
		builder.pop();
		builder.pop();
	}

}
