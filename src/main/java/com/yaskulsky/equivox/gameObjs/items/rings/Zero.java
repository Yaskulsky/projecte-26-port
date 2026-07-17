package com.yaskulsky.equivox.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.List;
import com.yaskulsky.equivox.api.block_entity.IDMPedestal;
import com.yaskulsky.equivox.api.capabilities.item.IItemCharge;
import com.yaskulsky.equivox.api.capabilities.item.IPedestalItem;
import com.yaskulsky.equivox.config.EquivoxConfig;
import com.yaskulsky.equivox.gameObjs.items.IBarHelper;
import com.yaskulsky.equivox.gameObjs.items.ICapabilityAware;
import com.yaskulsky.equivox.gameObjs.items.ISelfCraftingRemainder;
import com.yaskulsky.equivox.gameObjs.registries.PEDataComponentTypes;
import com.yaskulsky.equivox.gameObjs.registries.PESoundEvents;
import com.yaskulsky.equivox.integration.IntegrationHelper;
import com.yaskulsky.equivox.utils.MathUtils;
import com.yaskulsky.equivox.utils.WorldHelper;
import com.yaskulsky.equivox.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Zero extends PEToggleItem implements IPedestalItem, IItemCharge, IBarHelper, ICapabilityAware, ISelfCraftingRemainder {

	public Zero(Properties props) {
		super(props.component(PEDataComponentTypes.CHARGE, 0));
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity, @Nullable EquipmentSlot slot) {
		super.inventoryTick(stack, level, entity, slot);
		if (!level.isClientSide() && entity instanceof Player player && hotBarOrOffHand(slot) && stack.getOrDefault(PEDataComponentTypes.ACTIVE, false)) {
			WorldHelper.freezeInBoundingBox(level, player.getBoundingBox().inflate(3), player, true);
		}
	}

	@NotNull
	@Override
	public InteractionResult use(Level level, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			int offset = 3 + this.getCharge(stack);
			level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			WorldHelper.freezeInBoundingBox(level, player.getBoundingBox().inflate(offset), player, false);
		}
		return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos,
			@NotNull PEDESTAL pedestal) {
		if (!level.isClientSide() && EquivoxConfig.server.cooldown.pedestal.zero.get() != -1) {
			if (pedestal.getActivityCooldown() == 0) {
				AABB aabb = pedestal.getEffectBounds();
				WorldHelper.freezeInBoundingBox(level, aabb, null, false);
				for (Entity ent : level.getEntitiesOfClass(Entity.class, aabb, e -> !e.isSpectator() && e.isOnFire())) {
					ent.clearFire();
				}
				pedestal.setActivityCooldown(level, pos, EquivoxConfig.server.cooldown.pedestal.zero.get());
			} else {
				pedestal.decrementActivityCooldown(level, pos);
			}
		}
		return false;
	}

	@NotNull
	@Override
	public List<Component> getPedestalDescription(float tickRate) {
		//Only used on the client
		List<Component> list = new ArrayList<>();
		if (EquivoxConfig.server.cooldown.pedestal.zero.get() != -1) {
			list.add(PELang.PEDESTAL_ZERO_1.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_ZERO_2.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_ZERO_3.translateColored(ChatFormatting.BLUE, MathUtils.tickToSecFormatted(EquivoxConfig.server.cooldown.pedestal.zero.get(), tickRate)));
		}
		return list;
	}

	@Override
	public int getNumCharges(@NotNull ItemStack stack) {
		return 4;
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		return true;
	}

	@Override
	public float getWidthForBar(ItemStack stack) {
		return 1 - getChargePercent(stack);
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack) {
		return getScaledBarWidth(stack);
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack) {
		return getColorForBar(stack);
	}

	@Override
	public void attachCapabilities(RegisterCapabilitiesEvent event) {
		IntegrationHelper.registerCuriosCapability(event, this);
	}
}