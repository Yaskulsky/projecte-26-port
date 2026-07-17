package com.yaskulsky.equivox.emc.components.processor;

import com.yaskulsky.equivox.api.ItemInfo;
import com.yaskulsky.equivox.api.components.DataComponentProcessor;
import com.yaskulsky.equivox.api.proxy.IEMCProxy;
import com.yaskulsky.equivox.config.PEConfigTranslations;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@DataComponentProcessor
public class ArmorTrimProcessor extends PersistentComponentProcessor<ArmorTrim> {

	@Override
	public String getName() {
		return PEConfigTranslations.DCP_ARMOR_TRIM.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.DCP_ARMOR_TRIM.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.DCP_ARMOR_TRIM.tooltip();
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long recalculateEMC(@NotNull ItemInfo info, @Range(from = 1, to = Long.MAX_VALUE) long currentEMC, @NotNull ArmorTrim trim) throws ArithmeticException {
		// TODO 26.1: ArmorTrimMaterial API changed; skip trim EMC adjustment for now.
		return currentEMC;
	}

	@Override
	protected boolean validItem(@NotNull ItemInfo info) {
		return info.getItem().is(ItemTags.TRIMMABLE_ARMOR);
	}

	@Override
	protected boolean shouldPersist(@NotNull ItemInfo info, @NotNull ArmorTrim component) {
		return true;
	}

	@Override
	protected DataComponentType<ArmorTrim> getComponentType(@NotNull ItemInfo info) {
		return DataComponents.TRIM;
	}
}
