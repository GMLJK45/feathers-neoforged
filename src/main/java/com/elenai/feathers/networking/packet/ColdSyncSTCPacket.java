package com.elenai.feathers.networking.packet;

import com.elenai.feathers.client.ClientFeathersData;
import com.elenai.feathers.config.FeathersClientConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ColdSyncSTCPacket(boolean cold) implements CustomPacketPayload {

	public static final Type<ColdSyncSTCPacket> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath("elenai.feathers", "cold_sync"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ColdSyncSTCPacket> STREAM_CODEC =
			StreamCodec.composite(
					ByteBufCodecs.BOOL, ColdSyncSTCPacket::cold,
					ColdSyncSTCPacket::new
			);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ColdSyncSTCPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			ClientFeathersData.setCold(packet.cold());
			if (ClientFeathersData.isCold() && FeathersClientConfig.FROST_SOUND.get()) {
				Minecraft instance = Minecraft.getInstance();
				instance.level.playLocalSound(
						instance.player.blockPosition(),
						SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("minecraft", "entity.player.hurt_freeze")),
						SoundSource.PLAYERS, 1f, instance.level.random.nextFloat(), false);
			}
		});
	}
}
