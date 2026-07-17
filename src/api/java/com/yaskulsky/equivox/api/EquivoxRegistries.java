package com.yaskulsky.equivox.api;

import com.mojang.serialization.MapCodec;
import com.yaskulsky.equivox.api.nss.NormalizedSimpleStack;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class EquivoxRegistries {

	private EquivoxRegistries() {
	}

	private static Identifier rl(String path) {
		return Identifier.fromNamespaceAndPath(EquivoxAPI.EQUIVOX_MODID, path);
	}

	/**
	 * Gets the {@link ResourceKey} representing the name of the Registry for serializing {@link NormalizedSimpleStack}s.
	 */
	public static final ResourceKey<Registry<MapCodec<? extends NormalizedSimpleStack>>> NSS_SERIALIZER_NAME = ResourceKey.createRegistryKey(rl("nss_serializer"));

	/**
	 * Gets the Registry for serializing {@link NormalizedSimpleStack}s.
	 *
	 * @see #NSS_SERIALIZER_NAME
	 */
	public static final Registry<MapCodec<? extends NormalizedSimpleStack>> NSS_SERIALIZER = new RegistryBuilder<>(NSS_SERIALIZER_NAME)
			.defaultKey(rl("item"))//Default to item serialization
			.create();
}