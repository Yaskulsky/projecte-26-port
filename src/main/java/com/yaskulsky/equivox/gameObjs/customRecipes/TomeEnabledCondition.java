package com.yaskulsky.equivox.gameObjs.customRecipes;

import com.mojang.serialization.MapCodec;
import com.yaskulsky.equivox.config.EquivoxConfig;
import com.yaskulsky.equivox.gameObjs.registries.PERecipeConditions;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

public class TomeEnabledCondition implements ICondition {

	public static final TomeEnabledCondition INSTANCE = new TomeEnabledCondition();

	private TomeEnabledCondition() {
	}

	@Override
	public boolean test(@NotNull IContext context) {
		return EquivoxConfig.common.craftableTome.get();
	}

	@NotNull
	@Override
	public MapCodec<? extends ICondition> codec() {
		return PERecipeConditions.TOME_ENABLED.value();
	}
}