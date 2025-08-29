package com.github.elenterius.biomancy.client.gui.tooltip;

import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

/**
 * Horizontal Line element for Tooltips
 */
public class HrTooltipClientComponent implements ClientTooltipComponent {

	private final int colorARGB;

	public HrTooltipClientComponent(HrTooltipComponent component) {
		this.colorARGB = component.colorARGB();
	}

	@Override
	public int getHeight() {
		return 8;
	}

	@Override
	public int getWidth(Font font) {
		return 1; //placeholder
	}

	/**
	 * @param tooltipWidth inner width
	 * @param lineNumber   zero-indexed
	 * @param color        colorARGB
	 */
	public void renderLine(GuiGraphics guiGraphics, int posX, int posY, int tooltipWidth, int lineNumber) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		int yOffset = lineNumber == 1 ? 0 : 2; //handle 2px bottom margin of the first line
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(0, 0, 400);
		guiGraphics.fill(posX, posY + yOffset, posX + tooltipWidth, posY + yOffset + 4, colorARGB);
		guiGraphics.pose().popPose();
	}

}
