package com.yaskulsky.equivox;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.yaskulsky.equivox.client.PEModelProvider;
import com.yaskulsky.equivox.client.PESpriteSourceProvider;
import com.yaskulsky.equivox.client.lang.PELangProvider;
import com.yaskulsky.equivox.client.sound.PESoundProvider;
import com.yaskulsky.equivox.common.PEAdvancementsGenerator;
import com.yaskulsky.equivox.common.PECustomConversionProvider;
import com.yaskulsky.equivox.common.PEDataMapsProvider;
import com.yaskulsky.equivox.common.PEDatapackRegistryProvider;
import com.yaskulsky.equivox.common.PEPackMetadataGenerator;
import com.yaskulsky.equivox.common.PEWorldTransmutationProvider;
import com.yaskulsky.equivox.common.loot.PEBlockLootTable;
import com.yaskulsky.equivox.common.recipe.PERecipeProvider;
import com.yaskulsky.equivox.common.tag.PEBlockEntityTypeTagsProvider;
import com.yaskulsky.equivox.common.tag.PEBlockTagsProvider;
import com.yaskulsky.equivox.common.tag.PEDamageTypeTagsProvider;
import com.yaskulsky.equivox.common.tag.PEEntityTypeTagsProvider;
import com.yaskulsky.equivox.common.tag.PEItemTagsProvider;
import com.yaskulsky.equivox.common.tag.PEPotionsTagsProvider;
import com.yaskulsky.equivox.emc.EMCMappingHandler;
import com.yaskulsky.equivox.integration.IntegrationHelper;
import com.yaskulsky.equivox.utils.text.PELang;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = PECore.MODID)
public class EquivoxDataGenerator {

	@SubscribeEvent
	public static void gatherServerData(GatherDataEvent.Server event) {
		EMCMappingHandler.loadMappers();

		PEDatapackRegistryProvider drProvider = event.createProvider(PEDatapackRegistryProvider::new);
		CompletableFuture<Provider> lookupProvider = drProvider.getRegistryProvider();

		event.createProvider((output, lookup) -> new PEBlockTagsProvider(output, lookupProvider));
		event.createProvider((output, lookup) -> new PEItemTagsProvider(output, lookupProvider));
		event.createProvider((output, lookup) -> new PEEntityTypeTagsProvider(output, lookupProvider));
		event.createProvider((output, lookup) -> new PEBlockEntityTypeTagsProvider(output, lookupProvider));
		event.createProvider((output, lookup) -> new PEDamageTypeTagsProvider(output, lookupProvider));
		event.createProvider((output, lookup) -> new PEPotionsTagsProvider(output, lookupProvider));
		event.createProvider((output, lookup) -> new AdvancementProvider(output, lookupProvider, List.of(new PEAdvancementsGenerator())));
		event.createProvider((output, lookup) -> new LootTableProvider(output, Collections.emptySet(), List.of(
				new SubProviderEntry(PEBlockLootTable::new, LootContextParamSets.BLOCK)
		), lookupProvider));
		event.createProvider((output, lookup) -> new RecipeProvider.Runner(output, lookupProvider) {
			@Override
			protected RecipeProvider createRecipeProvider(Provider registries, RecipeOutput output) {
				return new PERecipeProvider(registries, output);
			}

			@Override
			public String getName() {
				return "Equivox recipes";
			}
		});
		event.createProvider((output, lookup) -> new PEDataMapsProvider(output, lookupProvider));
		event.createProvider((output, lookup) -> new PECustomConversionProvider(output, lookupProvider));
		event.createProvider((output, lookup) -> new PEWorldTransmutationProvider(output, lookupProvider));
	}

	@SubscribeEvent
	public static void gatherClientData(GatherDataEvent.Client event) {
		EMCMappingHandler.loadMappers();

		PEDatapackRegistryProvider drProvider = event.createProvider(PEDatapackRegistryProvider::new);
		CompletableFuture<Provider> lookupProvider = drProvider.getRegistryProvider();

		event.addProvider(new PEPackMetadataGenerator(event.getGenerator().getPackOutput(), PELang.PACK_DESCRIPTION));
		event.createProvider(PELangProvider::new);
		event.createProvider(PESoundProvider::new);
		event.createProvider(PEModelProvider::new);
		event.createProvider((output, lookup) -> new PESpriteSourceProvider(output, lookupProvider));

		if (ModList.get().isLoaded(IntegrationHelper.EMI_MODID)) {
			registerEmiProviders(event, lookupProvider);
		}
	}

	private static void registerEmiProviders(GatherDataEvent.Client event, CompletableFuture<Provider> lookupProvider) {
		try {
			Class<?> aliasMappingClass = Class.forName("com.yaskulsky.equivox.integration.recipe_viewer.alias.EquivoxAliasMapping");
			Class<?> aliasProvider = Class.forName("com.yaskulsky.equivox.client.integration.emi.EmiAliasProvider");
			Class<?> emiDefaults = Class.forName("com.yaskulsky.equivox.client.integration.emi.EquivoxEmiDefaults");
			java.util.function.Supplier<?> mappingSupplier = () -> {
				try {
					return aliasMappingClass.getDeclaredConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					throw new IllegalStateException(e);
				}
			};
			event.createProvider((output, lookup) -> {
				try {
					return (net.minecraft.data.DataProvider) aliasProvider
							.getConstructor(net.minecraft.data.PackOutput.class, CompletableFuture.class, String.class, java.util.function.Supplier.class)
							.newInstance(output, lookupProvider, PECore.MODID, mappingSupplier);
				} catch (ReflectiveOperationException e) {
					throw new IllegalStateException(e);
				}
			});
			event.createProvider((output, lookup) -> {
				try {
					return (net.minecraft.data.DataProvider) emiDefaults
							.getConstructor(net.minecraft.data.PackOutput.class, CompletableFuture.class)
							.newInstance(output, lookupProvider);
				} catch (ReflectiveOperationException e) {
					throw new IllegalStateException(e);
				}
			});
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Failed to register EMI data providers", e);
		}
	}
}
