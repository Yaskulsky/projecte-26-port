package com.yaskulsky.equivox.network.packets.to_client;

import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.util.LinkedHashSet;
import java.util.SequencedSet;
import com.yaskulsky.equivox.PECore;
import com.yaskulsky.equivox.api.world_transmutation.IWorldTransmutation;
import com.yaskulsky.equivox.api.world_transmutation.SimpleWorldTransmutation;
import com.yaskulsky.equivox.api.world_transmutation.WorldTransmutation;
import com.yaskulsky.equivox.network.packets.IPEPacket;
import com.yaskulsky.equivox.world_transmutation.WorldTransmutationManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncWorldTransmutations(Reference2ObjectMap<Block, SequencedSet<IWorldTransmutation>> transmutations) implements IPEPacket {

	public static final Type<SyncWorldTransmutations> TYPE = new Type<>(PECore.rl("sync_world_transmutations"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncWorldTransmutations> STREAM_CODEC =
			ByteBufCodecs.<RegistryFriendlyByteBuf, Block, SequencedSet<IWorldTransmutation>, Reference2ObjectMap<Block, SequencedSet<IWorldTransmutation>>>
					map(Reference2ObjectLinkedOpenHashMap::new, ByteBufCodecs.registry(Registries.BLOCK),
					ByteBufCodecs.collection(LinkedHashSet::new, new StreamCodec<>() {
						@Override
						public void encode(@NotNull RegistryFriendlyByteBuf buffer, @NotNull IWorldTransmutation value) {
							switch (value) {
								case SimpleWorldTransmutation transmutation -> {
									buffer.writeBoolean(false);
									SimpleWorldTransmutation.STREAM_CODEC.encode(buffer, transmutation);
								}
								case WorldTransmutation transmutation -> {
									buffer.writeBoolean(true);
									WorldTransmutation.STREAM_CODEC.encode(buffer, transmutation);
								}
								default -> throw new EncoderException("Unknown world transmutation implementation: " + value);
							}
						}

						@NotNull
						@Override
						public IWorldTransmutation decode(@NotNull RegistryFriendlyByteBuf buffer) {
							if (buffer.readBoolean()) {
								return WorldTransmutation.STREAM_CODEC.decode(buffer);
							}
							return SimpleWorldTransmutation.STREAM_CODEC.decode(buffer);
						}
					})
			).map(SyncWorldTransmutations::new, SyncWorldTransmutations::transmutations);

	@NotNull
	@Override
	public CustomPacketPayload.Type<SyncWorldTransmutations> type() {
		return TYPE;
	}

	@Override
	public void handle(IPayloadContext context) {
		PECore.debugLog("Receiving World Transmutation data from server.");
		WorldTransmutationManager.INSTANCE.setEntries(transmutations);
	}
}