package com.elenai.feathers.effect;

import com.elenai.feathers.Feathers;
import com.elenai.feathers.capability.FeathersAttachments;
import com.elenai.feathers.capability.PlayerFeathers;
import com.elenai.feathers.networking.FeathersMessages;
import com.elenai.feathers.networking.packet.ColdSyncSTCPacket;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public class ColdEffect extends MobEffect {

	public ColdEffect(MobEffectCategory mobEffectCategory, int color) {
		super(mobEffectCategory, color);
	}

	@EventBusSubscriber(modid = Feathers.MODID)
	public static class ColdEffectSync {

		@SubscribeEvent
		public static void onEffectAdded(MobEffectEvent.Added event) {
			if (isColdEffect(event.getEffectInstance()) && event.getEntity() instanceof ServerPlayer player) {
				setCold(player, true);
			}
		}

		@SubscribeEvent
		public static void onEffectExpired(MobEffectEvent.Expired event) {
			if (isColdEffect(event.getEffectInstance()) && event.getEntity() instanceof ServerPlayer player) {
				setCold(player, false);
			}
		}

		@SubscribeEvent
		public static void onEffectRemoved(MobEffectEvent.Remove event) {
			if (isColdEffect(event.getEffectInstance()) && event.getEntity() instanceof ServerPlayer player) {
				setCold(player, false);
			}
		}

		private static boolean isColdEffect(MobEffectInstance instance) {
			return instance != null && instance.getEffect().value() == FeathersEffects.COLD.get();
		}

		private static void setCold(ServerPlayer player, boolean cold) {
			PlayerFeathers f = player.getData(FeathersAttachments.PLAYER_FEATHERS);
			if (f.isCold() != cold) {
				f.setCold(cold);
				FeathersMessages.sendToPlayer(new ColdSyncSTCPacket(f.isCold()), player);
			}
		}
	}
}