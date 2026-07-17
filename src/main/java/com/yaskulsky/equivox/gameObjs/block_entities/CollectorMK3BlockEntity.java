package com.yaskulsky.equivox.gameObjs.block_entities;

import com.yaskulsky.equivox.gameObjs.EnumCollectorTier;
import com.yaskulsky.equivox.gameObjs.container.CollectorMK3Container;
import com.yaskulsky.equivox.gameObjs.registries.PEBlockEntityTypes;
import com.yaskulsky.equivox.gameObjs.registries.PEBlocks;
import com.yaskulsky.equivox.utils.text.TextComponentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CollectorMK3BlockEntity extends CollectorMK1BlockEntity {

	public CollectorMK3BlockEntity(BlockPos pos, BlockState state) {
		super(PEBlockEntityTypes.COLLECTOR_MK3, pos, state, EnumCollectorTier.MK3);
	}

	@Override
	protected int getInvSize() {
		return 16;
	}

	@NotNull
	@Override
	public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player playerIn) {
		return new CollectorMK3Container(windowId, playerInventory, this);
	}

	@NotNull
	@Override
	public Component getDisplayName() {
		return TextComponentUtil.build(PEBlocks.COLLECTOR_MK3);
	}
}