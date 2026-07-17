package com.yaskulsky.equivox.impl.codec;

import com.mojang.serialization.Codec;
import com.yaskulsky.equivox.api.codec.IPECodecHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test implementation of Equivox Codecs")
class CodecImplTest {

	@Test
	@DisplayName("Test lack of a crash without any conflicting keys")
	void noConflictingKeys() {
		Assertions.assertDoesNotThrow(() -> IPECodecHelper.INSTANCE.unboundedMap(
				Codec.INT.fieldOf("key"),
				Codec.BOOL.fieldOf("value")
		));
	}

	@Test
	@DisplayName("Test crash on conflicting keys")
	void conflictingKeys() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> IPECodecHelper.INSTANCE.unboundedMap(
				Codec.INT.fieldOf("test"),
				Codec.BOOL.fieldOf("test")
		));
	}
}