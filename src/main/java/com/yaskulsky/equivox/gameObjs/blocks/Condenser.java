package com.yaskulsky.equivox.gameObjs.blocks;

import com.yaskulsky.equivox.gameObjs.block_entities.CondenserBlockEntity;
import com.yaskulsky.equivox.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import com.yaskulsky.equivox.gameObjs.registries.PEBlockEntityTypes;
import org.jetbrains.annotations.Nullable;

public class Condenser extends AlchemicalChest {

	public Condenser(Properties props) {
		super(props);
	}

	@Nullable
	@Override
	public BlockEntityTypeRegistryObject<? extends CondenserBlockEntity> getType() {
		return PEBlockEntityTypes.CONDENSER;
	}
}