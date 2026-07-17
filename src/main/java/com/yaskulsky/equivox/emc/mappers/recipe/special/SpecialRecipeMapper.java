package com.yaskulsky.equivox.emc.mappers.recipe.special;

import com.yaskulsky.equivox.api.mapper.collector.IMappingCollector;
import com.yaskulsky.equivox.api.mapper.recipe.INSSFakeGroupManager;
import com.yaskulsky.equivox.api.mapper.recipe.IRecipeTypeMapper;
import com.yaskulsky.equivox.api.nss.NormalizedSimpleStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

public abstract class SpecialRecipeMapper<RECIPE extends CustomRecipe> implements IRecipeTypeMapper {

	protected abstract Class<RECIPE> getRecipeClass();

	protected abstract boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, RegistryAccess registryAccess, INSSFakeGroupManager fakeGroupManager);

	@Override
	public final boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, RecipeHolder<?> recipeHolder, RegistryAccess registryAccess,
			INSSFakeGroupManager fakeGroupManager) {
		if (getRecipeClass().isInstance(recipeHolder.value())) {
			return handleRecipe(mapper, registryAccess, fakeGroupManager);
		}
		return false;
	}

	@Override
	public final boolean canHandle(RecipeType<?> recipeType) {
		return recipeType == RecipeType.CRAFTING;
	}
}