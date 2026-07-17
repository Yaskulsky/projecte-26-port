package com.yaskulsky.equivox.gameObjs.registration.impl;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import com.yaskulsky.equivox.gameObjs.registration.PEDeferredHolder;
import com.yaskulsky.equivox.gameObjs.registration.PEDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockTypeDeferredRegister extends PEDeferredRegister<MapCodec<? extends Block>> {

	public BlockTypeDeferredRegister(String modid) {
		super(Registries.BLOCK_TYPE, modid);
	}

	public <BLOCK extends Block> PEDeferredHolder<MapCodec<? extends Block>, MapCodec<BLOCK>> registerSimple(String name, Function<BlockBehaviour.Properties, BLOCK> factory) {
		return register(name, () -> BlockBehaviour.simpleCodec(factory));
	}
}