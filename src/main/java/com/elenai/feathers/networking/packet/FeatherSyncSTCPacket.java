package com.elenai.feathers.networking.packet;

import com.elenai.feathers.Feathers;
import com.elenai.feathers.client.ClientFeathersData;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FeatherSyncSTCPacket(int feathers, int maxFeathers, int regenRate, int weight, int endurance)
		implements CustomPacketPayload {

	public static final Type<FeatherSyncSTCPacket> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(Feathers.MODID, "feather_sync"));

	public static final StreamCodec<RegistryFriendlyByteBuf, FeatherSyncSTCPacket> STREAM_CODEC =
			StreamCodec.composite(
					ByteBufCodecs.INT, FeatherSyncSTCPacket::feathers,
					ByteBufCodecs.INT, FeatherSyncSTCPacket::maxFeathers,
					ByteBufCodecs.INT, FeatherSyncSTCPacket::regenRate,
					ByteBufCodecs.INT, FeatherSyncSTCPacket::weight,
					ByteBufCodecs.INT, FeatherSyncSTCPacket::endurance,
					FeatherSyncSTCPacket::new
			);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(FeatherSyncSTCPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			ClientFeathersData.setFeathers(packet.feathers());
			ClientFeathersData.setMaxFeathers(packet.maxFeathers());
			ClientFeathersData.setRegenRate(packet.regenRate());
			ClientFeathersData.setWeight(packet.weight());
			ClientFeathersData.setEnduranceFeathers(packet.endurance());
		});
	}
}
