package com.yaskulsky.equivox.network.packets.to_client.container;

import java.util.Optional;
import com.yaskulsky.equivox.PECore;
import com.yaskulsky.equivox.api.ItemInfo;
import com.yaskulsky.equivox.gameObjs.container.CondenserContainer;
import com.yaskulsky.equivox.network.packets.IPEPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record UpdateCondenserLockPKT(short windowId, @Nullable ItemInfo lockInfo) implements IPEPacket {

	public static final CustomPacketPayload.Type<UpdateCondenserLockPKT> TYPE = new CustomPacketPayload.Type<>(PECore.rl("update_condenser_lock"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateCondenserLockPKT> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.SHORT, UpdateCondenserLockPKT::windowId,
			ByteBufCodecs.optional(ItemInfo.STREAM_CODEC), pkt -> Optional.ofNullable(pkt.lockInfo()),
			(windowId, lockInfo) -> new UpdateCondenserLockPKT(windowId, lockInfo.orElse(null))
	);

	@NotNull
	@Override
	public CustomPacketPayload.Type<UpdateCondenserLockPKT> type() {
		return TYPE;
	}

	@Override
	public void handle(IPayloadContext context) {
		if (context.player().containerMenu instanceof CondenserContainer container && container.containerId == windowId) {
			container.updateLockInfo(lockInfo);
		}
	}
}