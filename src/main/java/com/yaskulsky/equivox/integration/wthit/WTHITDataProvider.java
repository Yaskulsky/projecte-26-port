package com.yaskulsky.equivox.integration.wthit;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import com.yaskulsky.equivox.api.proxy.IEMCProxy;
import com.yaskulsky.equivox.config.EquivoxConfig;
import com.yaskulsky.equivox.utils.EMCHelper;

public class WTHITDataProvider implements IBlockComponentProvider {

	static final WTHITDataProvider INSTANCE = new WTHITDataProvider();

	@Override
	public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
		if (EquivoxConfig.server.misc.lookingAtDisplay.get()) {
			long value = IEMCProxy.INSTANCE.getValue(accessor.getBlock());
			if (value > 0) {
				tooltip.addLine(EMCHelper.getEmcTextComponent(value, 1));
			}
		}
	}
}