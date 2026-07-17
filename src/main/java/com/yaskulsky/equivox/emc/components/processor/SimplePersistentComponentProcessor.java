package com.yaskulsky.equivox.emc.components.processor;

import com.yaskulsky.equivox.api.ItemInfo;
import com.yaskulsky.equivox.api.components.IDataComponentProcessor;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import org.jetbrains.annotations.NotNull;

public abstract class SimplePersistentComponentProcessor<TYPE> implements IDataComponentProcessor {

	protected abstract DataComponentType<TYPE> getComponentType(@NotNull ItemInfo info);

	protected boolean validItem(@NotNull ItemInfo info) {
		return true;
	}

	protected abstract boolean shouldPersist(@NotNull ItemInfo info, @NotNull TYPE component);

	@Override
	public final boolean hasPersistentComponents() {
		return true;
	}

	protected TYPE cleanPersistentComponent(@NotNull TYPE component) {
		return component;
	}

	@Override
	public final void collectPersistentComponents(@NotNull ItemInfo info, @NotNull DataComponentPatch.Builder builder) {
		if (validItem(info)) {
			DataComponentType<TYPE> componentType = getComponentType(info);
			TYPE component = info.getOrNull(componentType);
			if (component != null && shouldPersist(info, component)) {
				builder.set(componentType, cleanPersistentComponent(component));
			}
		}
	}
}