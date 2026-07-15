package com.elenai.feathers.networking.packet;

import com.elenai.feathers.Feathers;
import com.elenai.feathers.client.ClientFeathersData;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EnergizedSyncSTCPacket(boolean energized) implements CustomPacketPayload {

	public static final Type<EnergizedSyncSTCPacket> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(Feathers.MODID, "energized_sync"));

	public static final StreamCodec<RegistryFriendlyByteBuf, EnergizedSyncSTCPacket> STREAM_CODEC =
			StreamCodec.composite(
					ByteBufCodecs.BOOL, EnergizedSyncSTCPacket::energized,
					EnergizedSyncSTCPacket::new
			);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(EnergizedSyncSTCPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			ClientFeathersData.setEnergized(packet.energized());
		});
	}
}
