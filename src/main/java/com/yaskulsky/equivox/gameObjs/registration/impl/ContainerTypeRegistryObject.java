package com.yaskulsky.equivox.gameObjs.registration.impl;

import com.yaskulsky.equivox.gameObjs.registration.PEDeferredHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ContainerTypeRegistryObject<CONTAINER extends AbstractContainerMenu> extends PEDeferredHolder<MenuType<?>, MenuType<CONTAINER>> {

	public ContainerTypeRegistryObject(ResourceKey<MenuType<?>> key) {
		super(key);
	}
}