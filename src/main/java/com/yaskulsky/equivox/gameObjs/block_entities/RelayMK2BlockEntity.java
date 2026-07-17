package com.yaskulsky.equivox.gameObjs.block_entities;

import com.yaskulsky.equivox.gameObjs.EnumRelayTier;
import com.yaskulsky.equivox.gameObjs.container.RelayMK2Container;
import com.yaskulsky.equivox.gameObjs.registries.PEBlockEntityTypes;
import com.yaskulsky.equivox.utils.text.PELang;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RelayMK2BlockEntity extends RelayMK1BlockEntity {

	public RelayMK2BlockEntity(BlockPos pos, BlockState state) {
		super(PEBlockEntityTypes.RELAY_MK2, pos, state, 13, EnumRelayTier.MK2);
	}

	@NotNull
	@Override
	public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
		return new RelayMK2Container(windowId, playerInventory, this);
	}

	@NotNull
	@Override
	public Component getDisplayName() {
		return PELang.GUI_RELAY_MK2.translate();
	}

	@Override
	public double getBonusToAdd() {
		return 0.15;
	}
}