package com.yaskulsky.equivox.client.integration.emi;

import java.util.concurrent.CompletableFuture;
import com.yaskulsky.equivox.PECore;
import com.yaskulsky.equivox.gameObjs.registries.PEBlocks;
import com.yaskulsky.equivox.gameObjs.registries.PEItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

public class EquivoxEmiDefaults extends BaseEmiDefaults {

	public EquivoxEmiDefaults(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, PECore.MODID);
	}

	@Override
	protected void addDefaults(HolderLookup.Provider lookupProvider) {
		//Convert blocks to their base types
		addRecipe(PEBlocks.ALCHEMICAL_COAL);
		addRecipe(PEBlocks.MOBIUS_FUEL);
		addRecipe(PEBlocks.AETERNALIS_FUEL);
		addRecipe(PEBlocks.DARK_MATTER);
		addRecipe(PEBlocks.RED_MATTER);
		//Multi-tools should show their base tool recipe
		addRecipe(PEItems.RED_MATTER_MORNING_STAR);
		addRecipe(PEItems.RED_MATTER_KATAR);
		//Split the life stone into the individual stone types
		addRecipe(PEItems.LIFE_STONE);
	}
}
