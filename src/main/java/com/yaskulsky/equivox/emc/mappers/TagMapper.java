package com.yaskulsky.equivox.emc.mappers;

import com.yaskulsky.equivox.api.mapper.IEMCMapper;
import com.yaskulsky.equivox.api.mapper.collector.IMappingCollector;
import com.yaskulsky.equivox.api.nss.AbstractNSSTag;
import com.yaskulsky.equivox.api.nss.NSSTag;
import com.yaskulsky.equivox.api.nss.NormalizedSimpleStack;
import com.yaskulsky.equivox.config.PEConfigTranslations;
import com.yaskulsky.equivox.utils.EMCHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;

public class TagMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		for (NSSTag stack : AbstractNSSTag.getAllCreatedTags()) {
			stack.forEachElement(mapper, stack, (collector, normalizedSimpleStack, tag) -> {
				//Tag -> element
				collector.addConversion(1, tag, EMCHelper.intMapOf(normalizedSimpleStack, 1));
				//Element -> tag
				collector.addConversion(1, normalizedSimpleStack, EMCHelper.intMapOf(tag, 1));
			});
		}
	}

	@Override
	public String getName() {
		return PEConfigTranslations.MAPPING_TAG_MAPPER.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.MAPPING_TAG_MAPPER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.MAPPING_TAG_MAPPER.tooltip();
	}
}