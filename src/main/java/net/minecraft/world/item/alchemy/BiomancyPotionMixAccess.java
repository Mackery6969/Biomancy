package net.minecraft.world.item.alchemy;

import com.github.elenterius.biomancy.mixin.accessor.PotionBrewingAccessor;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public final class BiomancyPotionMixAccess {

	private BiomancyPotionMixAccess() {}

	public record PotionMix(Holder<Potion> from, Ingredient ingredient, Holder<Potion> to) {}

	@SuppressWarnings("unchecked")
	public static List<PotionMix> getPotionMixes(PotionBrewing potionBrewing) {
		List<PotionBrewing.Mix<Potion>> mixes = ((PotionBrewingAccessor) (Object) potionBrewing).biomancy$potionMixes();

		List<PotionMix> result = new ArrayList<>(mixes.size());
		for (PotionBrewing.Mix<Potion> mix : mixes) {
			result.add(new PotionMix(mix.from(), mix.ingredient(), mix.to()));
		}

		return result;
	}

}
