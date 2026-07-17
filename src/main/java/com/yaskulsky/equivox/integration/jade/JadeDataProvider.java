package com.yaskulsky.equivox.integration.jade;

import com.yaskulsky.equivox.api.proxy.IEMCProxy;
import com.yaskulsky.equivox.config.EquivoxConfig;
import com.yaskulsky.equivox.utils.EMCHelper;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class JadeDataProvider implements IBlockComponentProvider {

	static final JadeDataProvider INSTANCE = new JadeDataProvider();

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		if (EquivoxConfig.server.misc.lookingAtDisplay.get()) {
			long value = IEMCProxy.INSTANCE.getValue(accessor.getBlock());
			if (value > 0) {
				tooltip.add(EMCHelper.getEmcTextComponent(value, 1));
			}
		}
	}

	@Override
	public Identifier getUid() {
		return PEJadeConstants.EMC_PROVIDER;
	}
}