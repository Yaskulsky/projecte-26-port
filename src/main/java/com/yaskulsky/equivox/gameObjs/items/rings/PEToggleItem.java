package com.yaskulsky.equivox.gameObjs.items.rings;

import com.yaskulsky.equivox.api.capabilities.item.IModeChanger;
import com.yaskulsky.equivox.gameObjs.items.ItemPE;
import com.yaskulsky.equivox.gameObjs.registries.PEDataComponentTypes;
import com.yaskulsky.equivox.gameObjs.registries.PESoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class PEToggleItem extends ItemPE implements IModeChanger<Boolean> {

	public PEToggleItem(Properties props) {
		super(props.component(PEDataComponentTypes.ACTIVE, false));
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		return false;
	}

	@Override
	public Boolean getMode(@NotNull ItemStack stack) {
		return stack.getOrDefault(PEDataComponentTypes.ACTIVE, false);
	}

	@Override
	public boolean changeMode(@NotNull Player player, @NotNull ItemStack stack, InteractionHand hand) {
		boolean isActive = getMode(stack);
		player.level().playSound(null, player.getX(), player.getY(), player.getZ(), isActive ? PESoundEvents.UNCHARGE.get() : PESoundEvents.HEAL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		stack.set(PEDataComponentTypes.ACTIVE, !isActive);
		return true;
	}
}