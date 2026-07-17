package com.yaskulsky.equivox.gameObjs.gui;

import com.yaskulsky.equivox.PECore;
import com.yaskulsky.equivox.gameObjs.container.AlchBagContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class AlchBagScreen extends PEContainerScreen<AlchBagContainer> {

	private static final Identifier texture = PECore.rl("textures/gui/alchchest.png");

	public AlchBagScreen(AlchBagContainer container, Inventory invPlayer, Component title) {
		super(container, invPlayer, title, 255, 230);
	}

	@Override
	protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(font, title, (imageWidth - font.width(title)) / 2, 4, PEGuiGraphics.LABEL_COLOR, false);
		graphics.text(font, playerInventoryTitle, 48, 140, PEGuiGraphics.LABEL_COLOR, false);
	}

	@Override
	protected void peExtractBackground(@NotNull GuiGraphicsExtractor graphics, float partialTicks, int mouseX, int mouseY) {
		PEGuiGraphics.blit(graphics, texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
}
