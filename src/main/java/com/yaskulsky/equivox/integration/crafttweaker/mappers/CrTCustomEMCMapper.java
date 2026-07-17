package com.yaskulsky.equivox.integration.crafttweaker.mappers;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.util.Iterator;
import com.yaskulsky.equivox.PECore;
import com.yaskulsky.equivox.api.mapper.EMCMapper;
import com.yaskulsky.equivox.api.mapper.IEMCMapper;
import com.yaskulsky.equivox.api.mapper.collector.IMappingCollector;
import com.yaskulsky.equivox.api.nss.NormalizedSimpleStack;
import com.yaskulsky.equivox.config.PEConfigTranslations;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

@EMCMapper(requiredMods = "crafttweaker")
public class CrTCustomEMCMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	private static final Object2LongMap<NormalizedSimpleStack> customEmcValues = new Object2LongOpenHashMap<>();

	public static void registerCustomEMC(@NotNull NormalizedSimpleStack stack, long emcValue) {
		customEmcValues.put(stack, emcValue);
	}

	public static void unregisterNSS(@NotNull NormalizedSimpleStack stack) {
		customEmcValues.removeLong(stack);
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		for (Iterator<Object2LongMap.Entry<NormalizedSimpleStack>> iterator = Object2LongMaps.fastIterator(customEmcValues); iterator.hasNext(); ) {
			Object2LongMap.Entry<NormalizedSimpleStack> entry = iterator.next();
			NormalizedSimpleStack normStack = entry.getKey();
			long value = entry.getLongValue();
			//Note: We set it for each of the values in the tag to make sure it is properly taken into account when calculating the individual EMC values
			normStack.forSelfAndEachElement(mapper, value, IMappingCollector::setValueBefore);
			PECore.debugLog("CraftTweaker setting value for {} to {}", normStack, value);
		}
	}

	@Override
	public String getName() {
		return PEConfigTranslations.MAPPING_CRT_EMC_MAPPER.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.MAPPING_CRT_EMC_MAPPER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.MAPPING_CRT_EMC_MAPPER.tooltip();
	}
}