package com.yaskulsky.equivox.integration.recipe_viewer.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import com.yaskulsky.equivox.PECore;
import com.yaskulsky.equivox.utils.text.ILangEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;

public class PEEmiCategory extends EmiRecipeCategory {

	private final Component name;

	public PEEmiCategory(String path, ItemLike icon, ILangEntry name) {
		super(PECore.rl(path), EmiStack.of(icon));
		this.name = name.translate();
	}

	@Override
	public Component getName() {
		return name;
	}
}