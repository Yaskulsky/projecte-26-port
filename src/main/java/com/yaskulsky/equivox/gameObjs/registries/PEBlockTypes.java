package com.yaskulsky.equivox.gameObjs.registries;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yaskulsky.equivox.PECore;
import com.yaskulsky.equivox.gameObjs.EnumMatterType;
import com.yaskulsky.equivox.gameObjs.blocks.MatterFurnace;
import com.yaskulsky.equivox.gameObjs.blocks.TransmutationStone;
import com.yaskulsky.equivox.gameObjs.registration.PEDeferredHolder;
import com.yaskulsky.equivox.gameObjs.registration.impl.BlockTypeDeferredRegister;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PEBlockTypes {

	private PEBlockTypes() {
	}

	public static final BlockTypeDeferredRegister BLOCK_TYPES = new BlockTypeDeferredRegister(PECore.MODID);

	public static final PEDeferredHolder<MapCodec<? extends Block>, MapCodec<TransmutationStone>> TRANSMUTATION_TABLE = BLOCK_TYPES.registerSimple("transmutation_table", TransmutationStone::new);
	public static final PEDeferredHolder<MapCodec<? extends Block>, MapCodec<MatterFurnace>> MATTER_FURNACE = BLOCK_TYPES.register("matter_furnace", () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
			BlockBehaviour.propertiesCodec(),
			EnumMatterType.CODEC.fieldOf("type").forGetter(MatterFurnace::getMatterType)
	).apply(instance, MatterFurnace::new)));
}