package com.elenai.feathers.networking.packet;

import com.elenai.feathers.Feathers;
import com.elenai.feathers.capability.FeathersAttachments;
import com.elenai.feathers.capability.PlayerFeathers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FeatherSyncCTSPacket(int feathers, int endurance, int cooldown) implements CustomPacketPayload {

	public static final Type<FeatherSyncCTSPacket> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(Feathers.MODID, "feather_sync_cts"));

	public static final StreamCodec<FriendlyByteBuf, FeatherSyncCTSPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, FeatherSyncCTSPacket::feathers,
			ByteBufCodecs.VAR_INT, FeatherSyncCTSPacket::endurance,
			ByteBufCodecs.VAR_INT, FeatherSyncCTSPacket::cooldown,
			FeatherSyncCTSPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(FeatherSyncCTSPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				PlayerFeathers f = player.getData(FeathersAttachments.PLAYER_FEATHERS);
				f.setFeathers(packet.feathers());
				f.setCooldown(packet.cooldown());
				f.setEnduranceFeathers(packet.endurance());
			}
		});
	}
}
