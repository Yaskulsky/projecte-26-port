package com.yaskulsky.equivox.integration.recipe_viewer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedSet;
import java.util.Set;
import com.yaskulsky.equivox.api.world_transmutation.IWorldTransmutation;
import com.yaskulsky.equivox.emc.FuelMapper;
import com.yaskulsky.equivox.utils.text.PELang;
import com.yaskulsky.equivox.world_transmutation.WorldTransmutationManager;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class RecipeViewerHelper {

	private RecipeViewerHelper() {
	}

	public static String stripForSynthetic(Holder<?> holder) {
		ResourceKey<?> key = holder.getKey();
		return key == null ? "unregistered" : key.identifier().toString().replace(':', '_');
	}

	public static Set<WorldTransmuteEntry> getAllTransmutations() {
		Set<WorldTransmuteEntry> visible = new LinkedHashSet<>();
		for (SequencedSet<IWorldTransmutation> transmutationsForBlock : WorldTransmutationManager.INSTANCE.getWorldTransmutations().values()) {
			for (IWorldTransmutation transmutation : transmutationsForBlock) {
				WorldTransmuteEntry entry = WorldTransmuteEntry.create(transmutation);
				if (entry != null) {
					visible.add(entry);
				}
			}
		}
		return visible;
	}

	public static List<FuelUpgradeRecipe> getFuelUpgrades() {
		List<FuelUpgradeRecipe> recipes = new ArrayList<>();
		for (Holder<Item> holder : FuelMapper.getFuelMap()) {
			Holder<Item> fuelUpgrade = FuelMapper.getFuelUpgrade(holder);
			if (fuelUpgrade != null) {
				recipes.add(new FuelUpgradeRecipe(holder, fuelUpgrade));
			}
		}
		return recipes;
	}

	public static Component getTransmuteDescription() {
		return PELang.WORLD_TRANSMUTE_DESCRIPTION.translate(Component.keybind("key.use"), Component.keybind("key.sneak"));
	}
}