package com.yaskulsky.equivox.gameObjs.registries;

import com.yaskulsky.equivox.PECore;
import com.yaskulsky.equivox.api.EquivoxRegistries;
import com.yaskulsky.equivox.api.nss.NSSFake;
import com.yaskulsky.equivox.api.nss.NSSFluid;
import com.yaskulsky.equivox.api.nss.NSSItem;
import com.yaskulsky.equivox.api.nss.NormalizedSimpleStack;
import com.yaskulsky.equivox.gameObjs.registration.DeferredCodecHolder;
import com.yaskulsky.equivox.gameObjs.registration.DeferredCodecRegister;

public class PENormalizedSimpleStacks {

	private PENormalizedSimpleStacks() {
	}

	public static final DeferredCodecRegister<NormalizedSimpleStack> NSS_SERIALIZERS = new DeferredCodecRegister<>(EquivoxRegistries.NSS_SERIALIZER_NAME, PECore.MODID);

	public static final DeferredCodecHolder<NormalizedSimpleStack, NSSItem> ITEM = NSS_SERIALIZERS.registerCodec("item", () -> NSSItem.CODEC);
	public static final DeferredCodecHolder<NormalizedSimpleStack, NSSFluid> FLUID = NSS_SERIALIZERS.registerCodec("fluid", () -> NSSFluid.CODEC);
	public static final DeferredCodecHolder<NormalizedSimpleStack, NSSFake> FAKE = NSS_SERIALIZERS.registerCodec("fake", () -> NSSFake.CODEC);
}