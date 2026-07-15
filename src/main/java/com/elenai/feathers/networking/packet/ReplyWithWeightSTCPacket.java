package com.elenai.feathers.networking.packet;

import com.elenai.feathers.event.ClientEvents;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ReplyWithWeightSTCPacket(int weight) implements CustomPacketPayload {

	public static final Type<ReplyWithWeightSTCPacket> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath("elenai.feathers", "reply_with_weight"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ReplyWithWeightSTCPacket> STREAM_CODEC =
			StreamCodec.composite(
					ByteBufCodecs.INT, ReplyWithWeightSTCPacket::weight,
					ReplyWithWeightSTCPacket::new
			);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ReplyWithWeightSTCPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			ClientEvents.currentWeight = packet.weight();
		});
	}
}
