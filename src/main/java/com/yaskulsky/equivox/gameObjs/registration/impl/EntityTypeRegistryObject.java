package com.yaskulsky.equivox.gameObjs.registration.impl;

import com.yaskulsky.equivox.gameObjs.registration.PEDeferredHolder;
import com.yaskulsky.equivox.utils.text.IHasTranslationKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class EntityTypeRegistryObject<ENTITY extends Entity> extends PEDeferredHolder<EntityType<?>, EntityType<ENTITY>> implements IHasTranslationKey {

	public EntityTypeRegistryObject(ResourceKey<EntityType<?>> key) {
		super(key);
	}

	@Override
	public String getTranslationKey() {
		return get().getDescriptionId();
	}
}