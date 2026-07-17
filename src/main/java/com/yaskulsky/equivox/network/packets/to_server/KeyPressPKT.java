package com.yaskulsky.equivox.network.packets.to_server;

import io.netty.buffer.ByteBuf;
import com.yaskulsky.equivox.PECore;
import com.yaskulsky.equivox.api.capabilities.PECapabilities;
import com.yaskulsky.equivox.api.capabilities.item.IExtraFunction;
import com.yaskulsky.equivox.api.capabilities.item.IItemCharge;
import com.yaskulsky.equivox.api.capabilities.item.IModeChanger;
import com.yaskulsky.equivox.api.capabilities.item.IProjectileShooter;
import com.yaskulsky.equivox.config.EquivoxConfig;
import com.yaskulsky.equivox.gameObjs.items.armor.GemArmorBase;
import com.yaskulsky.equivox.gameObjs.items.armor.GemChest;
import com.yaskulsky.equivox.gameObjs.items.armor.GemFeet;
import com.yaskulsky.equivox.gameObjs.items.armor.GemHelmet;
import com.yaskulsky.equivox.gameObjs.registries.PEAttachmentTypes;
import com.yaskulsky.equivox.gameObjs.registries.PEItems;
import com.yaskulsky.equivox.network.packets.IPEPacket;
import com.yaskulsky.equivox.utils.PEKeybind;
import com.yaskulsky.equivox.utils.PlayerHelper;
import com.yaskulsky.equivox.utils.text.ILangEntry;
import com.yaskulsky.equivox.utils.text.PELang;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record KeyPressPKT(PEKeybind key) implements IPEPacket {

	public static final CustomPacketPayload.Type<KeyPressPKT> TYPE = new CustomPacketPayload.Type<>(PECore.rl("key_press"));
	public static final StreamCodec<ByteBuf, KeyPressPKT> STREAM_CODEC = PEKeybind.STREAM_CODEC.map(KeyPressPKT::new, KeyPressPKT::key);

	@NotNull
	@Override
	public CustomPacketPayload.Type<KeyPressPKT> type() {
		return TYPE;
	}

	@Override
	public void handle(IPayloadContext context) {
		Player player = context.player();
		if (player.isSpectator()) {
			return;
		}
		if (key == PEKeybind.HELMET_TOGGLE) {
			ItemStack helm = player.getItemBySlot(EquipmentSlot.HEAD);
			if (!helm.isEmpty() && helm.is(PEItems.GEM_HELMET)) {
				GemHelmet.toggleNightVision(helm, player);
			}
			return;
		} else if (key == PEKeybind.BOOTS_TOGGLE) {
			ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
			if (!boots.isEmpty() && boots.is(PEItems.GEM_BOOTS)) {
				GemFeet.toggleStepAssist(boots, player);
			}
			return;
		}
		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack stack = player.getItemInHand(hand);
			switch (key) {
				case CHARGE -> {
					if (tryPerformCapability(player, stack, hand, PECapabilities.CHARGE_ITEM_CAPABILITY, IItemCharge::changeCharge)) {
						return;
					} else if (hand == InteractionHand.MAIN_HAND && isSafe(stack) && GemArmorBase.hasAnyPiece(player)) {
						player.setData(PEAttachmentTypes.GEM_ARMOR_STATE, !player.getData(PEAttachmentTypes.GEM_ARMOR_STATE));
						ILangEntry langEntry = player.getData(PEAttachmentTypes.GEM_ARMOR_STATE) ? PELang.GEM_ACTIVATE : PELang.GEM_DEACTIVATE;
						player.sendSystemMessage(langEntry.translate());
						return;
					}
				}
				case EXTRA_FUNCTION -> {
					if (tryPerformCapability(player, stack, hand, PECapabilities.EXTRA_FUNCTION_ITEM_CAPABILITY, IExtraFunction::doExtraFunction)) {
						return;
					} else if (hand == InteractionHand.MAIN_HAND && isSafe(stack) && player.getData(PEAttachmentTypes.GEM_ARMOR_STATE)) {
						ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
						if (!chestplate.isEmpty() && chestplate.is(PEItems.GEM_CHESTPLATE) &&
							PlayerHelper.checkCooldown(player, PEItems.GEM_CHESTPLATE.get(), EquivoxConfig.server.cooldown.player.gemChest)) {
							GemChest.doExplode(player);
							return;
						}
					}
				}
				case FIRE_PROJECTILE -> {
					if (!stack.isEmpty() && PlayerHelper.checkCooldown(player, stack.getItem(), EquivoxConfig.server.cooldown.player.projectile)
						&& tryPerformCapability(player, stack, hand, PECapabilities.PROJECTILE_SHOOTER_ITEM_CAPABILITY, IProjectileShooter::shootProjectile)) {
						PlayerHelper.swingItem(player, hand);
					}
					if (hand == InteractionHand.MAIN_HAND && isSafe(stack) && player.getData(PEAttachmentTypes.GEM_ARMOR_STATE)) {
						ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
						if (!helmet.isEmpty() && helmet.is(PEItems.GEM_HELMET)) {
							GemHelmet.doZap(player);
							return;
						}
					}
				}
				case MODE -> {
					if (tryPerformCapability(player, stack, hand, PECapabilities.MODE_CHANGER_ITEM_CAPABILITY, IModeChanger::changeMode)) {
						return;
					}
				}
			}
		}
	}

	private static <CAPABILITY> boolean tryPerformCapability(Player player, ItemStack stack, InteractionHand hand, ItemCapability<CAPABILITY, Void> capability,
			CapabilityProcessor<CAPABILITY> processor) {
		CAPABILITY impl = stack.getCapability(capability);
		return impl != null && processor.process(impl, player, stack, hand);
	}

	private static boolean isSafe(ItemStack stack) {
		return EquivoxConfig.server.misc.unsafeKeyBinds.get() || stack.isEmpty();
	}

	@FunctionalInterface
	private interface CapabilityProcessor<CAPABILITY> {

		boolean process(CAPABILITY capability, Player player, ItemStack stack, InteractionHand hand);
	}
}