package com.yaskulsky.equivox.emc.mappers;

import java.util.Map;
import com.yaskulsky.equivox.PECore;
import com.yaskulsky.equivox.api.mapper.EMCMapper;
import com.yaskulsky.equivox.api.mapper.IEMCMapper;
import com.yaskulsky.equivox.api.mapper.collector.IMappingCollector;
import com.yaskulsky.equivox.api.nss.NSSItem;
import com.yaskulsky.equivox.api.nss.NormalizedSimpleStack;
import com.yaskulsky.equivox.config.PEConfigTranslations;
import com.yaskulsky.equivox.utils.EMCHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Oxidizable;

@EMCMapper
public class OxidizationMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		Registry<Block> blocks = registryAccess.lookupOrThrow(Registries.BLOCK);
		int recipeCount = 0;
		for (Map.Entry<ResourceKey<Block>, Oxidizable> entry : blocks.getDataMap(NeoForgeDataMaps.OXIDIZABLES).entrySet()) {
			//Add conversions both directions due to scraping
			Block block = blocks.getValue(entry.getKey());
			if (block != null) {
				NSSItem unweathered = NSSItem.createItem(block);
				NSSItem weathered = NSSItem.createItem(entry.getValue().nextOxidationStage());
				mapper.addConversion(1, weathered, EMCHelper.intMapOf(unweathered, 1));
				mapper.addConversion(1, unweathered, EMCHelper.intMapOf(weathered, 1));
				recipeCount += 2;
			}
		}
		PECore.debugLog("{} Statistics:", getName());
		PECore.debugLog("Found {} Oxidizable Conversions", recipeCount);
	}

	@Override
	public String getName() {
		return PEConfigTranslations.MAPPING_OXIDATION_MAPPER.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.MAPPING_OXIDATION_MAPPER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.MAPPING_OXIDATION_MAPPER.tooltip();
	}
}