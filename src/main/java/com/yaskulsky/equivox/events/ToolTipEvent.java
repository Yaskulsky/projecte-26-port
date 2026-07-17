package com.yaskulsky.equivox.events;

import java.util.List;
import com.yaskulsky.equivox.PECore;
import com.yaskulsky.equivox.api.capabilities.IKnowledgeProvider;
import com.yaskulsky.equivox.api.capabilities.PECapabilities;
import com.yaskulsky.equivox.api.capabilities.item.IItemEmcHolder;
import com.yaskulsky.equivox.api.capabilities.item.IPedestalItem;
import com.yaskulsky.equivox.api.proxy.IEMCProxy;
import com.yaskulsky.equivox.config.EquivoxConfig;
import com.yaskulsky.equivox.gameObjs.registries.PEDataComponentTypes;
import com.yaskulsky.equivox.utils.EMCHelper;
import com.yaskulsky.equivox.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT)
public class ToolTipEvent {

	@SubscribeEvent
	public static void tTipEvent(ItemTooltipEvent event) {
		ItemStack current = event.getItemStack();
		if (current.isEmpty()) {
			return;
		}
		List<Component> tooltip = event.getToolTip();
		if (EquivoxConfig.client.pedestalToolTips.get()) {
			IPedestalItem pedestalItem = current.getCapability(PECapabilities.PEDESTAL_ITEM_CAPABILITY);
			if (pedestalItem != null) {
				tooltip.add(PELang.PEDESTAL_ON.translateColored(ChatFormatting.DARK_PURPLE));
				List<Component> description = pedestalItem.getPedestalDescription(event.getContext().tickRate());
				if (description.isEmpty()) {
					tooltip.add(PELang.PEDESTAL_DISABLED.translateColored(ChatFormatting.RED));
				} else {
					tooltip.addAll(description);
				}
			}
		}

		if (EquivoxConfig.client.tagToolTips.get()) {
			current.tags().forEach(tag -> tooltip.add(Component.literal("#" + tag.location())));
		}

		if (EquivoxConfig.client.emcToolTips.get() && (!EquivoxConfig.client.shiftEmcToolTips.get() || Minecraft.getInstance().options.keyShift.isDown())) {
			long value = IEMCProxy.INSTANCE.getValue(current);
			if (value > 0) {
				tooltip.add(EMCHelper.getEmcTextComponent(value, 1));
				if (current.getCount() > 1) {
					tooltip.add(EMCHelper.getEmcTextComponent(value, current.getCount()));
				}
				Player player = event.getEntity();
				if (player != null && (!EquivoxConfig.client.shiftLearnedToolTips.get() || Minecraft.getInstance().options.keyShift.isDown())) {
					IKnowledgeProvider knowledgeProvider = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
					if (knowledgeProvider != null && knowledgeProvider.hasKnowledge(current)) {
						tooltip.add(PELang.EMC_HAS_KNOWLEDGE.translateColored(ChatFormatting.YELLOW));
					} else {
						tooltip.add(PELang.EMC_NO_KNOWLEDGE.translateColored(ChatFormatting.RED));
					}
				}
			}
		}

		long value = current.getOrDefault(PEDataComponentTypes.STORED_EMC, 0L);
		if (value == 0) {
			IItemEmcHolder emcHolder = current.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
			if (emcHolder != null) {
				value = emcHolder.getStoredEmc(current);
			}
		}
		if (value > 0) {
			tooltip.add(PELang.EMC_STORED.translateColored(ChatFormatting.YELLOW, ChatFormatting.WHITE, EMCHelper.formatEmc(value)));
		}
	}
}