package com.elenai.feathers.networking;

import com.elenai.feathers.Feathers;
import com.elenai.feathers.networking.packet.ColdSyncSTCPacket;
import com.elenai.feathers.networking.packet.EnergizedSyncSTCPacket;
import com.elenai.feathers.networking.packet.FeatherSyncCTSPacket;
import com.elenai.feathers.networking.packet.FeatherSyncSTCPacket;
import com.elenai.feathers.networking.packet.ReplyWithWeightSTCPacket;
import com.elenai.feathers.networking.packet.RequestWeightCTSPacket;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Feathers.MODID, bus = EventBusSubscriber.Bus.MOD)
public class FeathersMessages {

	@SubscribeEvent
	public static void register(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1.0");

		registrar.playToServer(FeatherSyncCTSPacket.TYPE, FeatherSyncCTSPacket.STREAM_CODEC, FeatherSyncCTSPacket::handle);
		registrar.playToServer(RequestWeightCTSPacket.TYPE, RequestWeightCTSPacket.STREAM_CODEC, RequestWeightCTSPacket::handle);

		registrar.playToClient(ReplyWithWeightSTCPacket.TYPE, ReplyWithWeightSTCPacket.STREAM_CODEC, ReplyWithWeightSTCPacket::handle);
		registrar.playToClient(FeatherSyncSTCPacket.TYPE, FeatherSyncSTCPacket.STREAM_CODEC, FeatherSyncSTCPacket::handle);
		registrar.playToClient(ColdSyncSTCPacket.TYPE, ColdSyncSTCPacket.STREAM_CODEC, ColdSyncSTCPacket::handle);
		registrar.playToClient(EnergizedSyncSTCPacket.TYPE, EnergizedSyncSTCPacket.STREAM_CODEC, EnergizedSyncSTCPacket::handle);
	}

	public static void sendToServer(CustomPacketPayload message) {
		PacketDistributor.sendToServer(message);
	}

	public static void sendToPlayer(CustomPacketPayload message, ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, message);
	}
}
