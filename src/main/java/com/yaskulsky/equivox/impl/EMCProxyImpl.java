package com.yaskulsky.equivox.impl;

import java.util.Objects;
import com.yaskulsky.equivox.api.ItemInfo;
import com.yaskulsky.equivox.api.proxy.IEMCProxy;
import com.yaskulsky.equivox.emc.components.DataComponentManager;
import com.yaskulsky.equivox.utils.EMCHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class EMCProxyImpl implements IEMCProxy {

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long getValue(@NotNull ItemInfo info) {
		return DataComponentManager.getEmcValue(Objects.requireNonNull(info));
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long getSellValue(@NotNull ItemInfo info) {
		return EMCHelper.getEmcSellValue(getValue(info));
	}
}