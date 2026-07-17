package com.yaskulsky.equivox.gameObjs.blocks;

import com.yaskulsky.equivox.gameObjs.block_entities.CondenserMK2BlockEntity;
import com.yaskulsky.equivox.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import com.yaskulsky.equivox.gameObjs.registries.PEBlockEntityTypes;
import org.jetbrains.annotations.Nullable;

public class CondenserMK2 extends Condenser {

	public CondenserMK2(Properties props) {
		super(props);
	}

	@Nullable
	@Override
	public BlockEntityTypeRegistryObject<CondenserMK2BlockEntity> getType() {
		return PEBlockEntityTypes.CONDENSER_MK2;
	}
}