package com.yaskulsky.equivox.emc.mappers;

import com.yaskulsky.equivox.api.mapper.EMCMapper;
import com.yaskulsky.equivox.api.mapper.IEMCMapper;
import com.yaskulsky.equivox.api.mapper.collector.IMappingCollector;
import com.yaskulsky.equivox.api.nss.NSSItem;
import com.yaskulsky.equivox.api.nss.NormalizedSimpleStack;
import com.yaskulsky.equivox.config.PEConfigTranslations;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;

@EMCMapper
public class OreBlacklistMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@EMCMapper.Instance
	public static final OreBlacklistMapper INSTANCE = new OreBlacklistMapper();

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(Tags.Items.ORES)) {
			NSSItem ore = NSSItem.createItem(holder);
			mapper.setValueBefore(ore, 0L);
			mapper.setValueAfter(ore, 0L);
		}
	}

	@Override
	public String getName() {
		return PEConfigTranslations.MAPPING_BLACKLIST_ORE_MAPPER.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.MAPPING_BLACKLIST_ORE_MAPPER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.MAPPING_BLACKLIST_ORE_MAPPER.tooltip();
	}
}
