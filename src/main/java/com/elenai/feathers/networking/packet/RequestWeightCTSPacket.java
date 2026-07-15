package com.elenai.feathers.networking.packet;

import com.elenai.feathers.api.FeathersHelper;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestWeightCTSPacket(int itemId, int lightweightLevel, int heavyLevel) implements CustomPacketPayload {

	public static final Type<RequestWeightCTSPacket> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath("elenai.feathers", "request_weight"));

	public static final StreamCodec<RegistryFriendlyByteBuf, RequestWeightCTSPacket> STREAM_CODEC =
			StreamCodec.composite(
					ByteBufCodecs.INT, RequestWeightCTSPacket::itemId,
					ByteBufCodecs.INT, RequestWeightCTSPacket::lightweightLevel,
					ByteBufCodecs.INT, RequestWeightCTSPacket::heavyLevel,
					RequestWeightCTSPacket::new
			);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(RequestWeightCTSPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			ServerPlayer sender = (ServerPlayer) context.player();
			int weight = FeathersHelper.getArmorWeight(
					Item.byId(packet.itemId()),
					packet.lightweightLevel(),
					packet.heavyLevel()
			);
			PacketDistributor.sendToPlayer(sender, new ReplyWithWeightSTCPacket(weight));
		});
	}
}
