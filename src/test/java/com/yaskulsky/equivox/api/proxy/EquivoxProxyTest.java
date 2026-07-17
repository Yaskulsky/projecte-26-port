package com.yaskulsky.equivox.api.proxy;

import com.yaskulsky.equivox.api.codec.IPECodecHelper;
import com.yaskulsky.equivox.api.components.IComponentProcessorHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test Equivox's Proxies")
class EquivoxProxyTest {

	@Test
	@DisplayName("Test getting the Data Component Processor Helper")
	void testGetComponentProcessorHelper() {
		Assertions.assertNotNull(IComponentProcessorHelper.INSTANCE);
	}

	@Test
	@DisplayName("Test getting the EMC Proxy")
	void testGetEMCProxy() {
		Assertions.assertNotNull(IEMCProxy.INSTANCE);
	}

	@Test
	@DisplayName("Test getting the Transmutation Proxy")
	void testGetTransmutationProxy() {
		Assertions.assertNotNull(ITransmutationProxy.INSTANCE);
	}

	@Test
	@DisplayName("Test getting the Codec Helper")
	void testGetCodecHelper() {
		Assertions.assertNotNull(IPECodecHelper.INSTANCE);
	}
}