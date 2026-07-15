package com.elenai.feathers.event;

import com.elenai.feathers.Feathers;
import com.elenai.feathers.api.FeathersHelper;
import com.elenai.feathers.attributes.FeathersAttributes;
import com.elenai.feathers.capability.FeathersAttachments;
import com.elenai.feathers.capability.PlayerFeathers;
import com.elenai.feathers.config.FeathersCommonConfig;
import com.elenai.feathers.effect.FeathersEffects;
import com.elenai.feathers.networking.FeathersMessages;
import com.elenai.feathers.networking.packet.ColdSyncSTCPacket;
import com.elenai.feathers.networking.packet.EnergizedSyncSTCPacket;
import com.elenai.feathers.networking.packet.FeatherSyncSTCPacket;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Feathers.MODID)
public class CommonEvents {

	@SubscribeEvent
	public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
		if (!event.getLevel().isClientSide() && (event.getEntity() instanceof ServerPlayer player)) {
			PlayerFeathers f = player.getData(FeathersAttachments.PLAYER_FEATHERS);
			FeathersMessages.sendToPlayer(
					new FeatherSyncSTCPacket(f.getFeathers(), f.getMaxFeathers(), f.getRegen(), FeathersHelper.getPlayerWeight(player), f.getEnduranceFeathers()), player);
			FeathersMessages.sendToPlayer(new ColdSyncSTCPacket(f.isCold()), player);
			FeathersMessages.sendToPlayer(new EnergizedSyncSTCPacket(player.hasEffect(FeathersEffects.ENERGIZED)), player);
		}
	}

	/**
	 * Handle the beta cold mechanic here
	 */
	private static void handleFrostEffect(ServerPlayer player) {
		if (FeathersCommonConfig.ENABLE_FROST_EFFECTS.get()) {
			var level = player.level();
			if ((player.isInPowderSnow || player.wasInPowderSnow
					|| level.getBiome(player.blockPosition()).value()
					.coldEnoughToSnow(player.blockPosition()))) { // TODO: Make fire stop ice effect AND make this a potion effect instead
				if (!player.hasEffect(FeathersEffects.COLD) || player.getActiveEffectsMap().get(FeathersEffects.COLD).getDuration() < 1000) {
					player.addEffect(new MobEffectInstance(FeathersEffects.COLD, 999999, 0, false, true));
				}
			} else if (player.hasEffect(FeathersEffects.COLD) && player.getActiveEffectsMap().get(FeathersEffects.COLD).getDuration() > 201) {
				player.removeEffect(FeathersEffects.COLD);
				player.addEffect(new MobEffectInstance(FeathersEffects.COLD, 200, 0, false, true));
			}
		}
	}

	/**
	 * Handle the Endurance mechanic here, where the potion leaves if the player has no endurance feathers left
	 */
	private static void handleEnduranceEffect(ServerPlayer player) {
		if (player.hasEffect(FeathersEffects.ENDURANCE) && FeathersHelper.getEndurance(player) == 0) {
			player.removeEffect(FeathersEffects.ENDURANCE);
		}
	}

	/**
	 * As of 1.21.1, MobEffect#addAttributeModifiers/removeAttributeModifiers no longer receive the
	 * LivingEntity, so EnduranceEffect can no longer sync the player from those overrides. Doing it
	 * here instead, since MobEffectEvent.Added/Remove still expose getEntity().
	 */
	@SubscribeEvent
	public static void onEnduranceEffectAdded(MobEffectEvent.Added event) {
		if (event.getEntity() instanceof ServerPlayer player && event.getEffectInstance().getEffect().is(FeathersEffects.ENDURANCE.getKey())) {
			PlayerFeathers f = player.getData(FeathersAttachments.PLAYER_FEATHERS);
			f.setEnduranceFeathers((event.getEffectInstance().getAmplifier() + 1) * 8);
			FeathersMessages.sendToPlayer(new FeatherSyncSTCPacket(f.getFeathers(), f.getMaxFeathers(), f.getRegen(),
					FeathersHelper.getPlayerWeight(player), f.getEnduranceFeathers()), player);
		}
	}

	@SubscribeEvent
	public static void onEnduranceEffectRemoved(MobEffectEvent.Remove event) {
		if (event.getEntity() instanceof ServerPlayer player && event.getEffectInstance() != null
				&& event.getEffectInstance().getEffect().is(FeathersEffects.ENDURANCE.getKey())) {
			PlayerFeathers f = player.getData(FeathersAttachments.PLAYER_FEATHERS);
			f.setEnduranceFeathers(0);
			FeathersMessages.sendToPlayer(new FeatherSyncSTCPacket(f.getFeathers(), f.getMaxFeathers(), f.getRegen(),
					FeathersHelper.getPlayerWeight(player), f.getEnduranceFeathers()), player);
		}
	}

	/**
	 * Same story as Endurance above: EnergizedEffect can no longer sync the player from
	 * addAttributeModifiers/removeAttributeModifiers since they no longer receive the LivingEntity.
	 */
	@SubscribeEvent
	public static void onEnergizedEffectAdded(MobEffectEvent.Added event) {
		if (event.getEntity() instanceof ServerPlayer player && event.getEffectInstance().getEffect().is(FeathersEffects.ENERGIZED.getKey())) {
			FeathersMessages.sendToPlayer(new EnergizedSyncSTCPacket(true), player);
		}
	}

	@SubscribeEvent
	public static void onEnergizedEffectRemoved(MobEffectEvent.Remove event) {
		if (event.getEntity() instanceof ServerPlayer player && event.getEffectInstance() != null
				&& event.getEffectInstance().getEffect().is(FeathersEffects.ENERGIZED.getKey())) {
			FeathersMessages.sendToPlayer(new EnergizedSyncSTCPacket(false), player);
		}
	}

	/**
	 * Regenerate the player's feathers, taking the energized potion into account
	 * @param player the ticking server player
	 */
	private static void regenerateFeathers(ServerPlayer player) {
		PlayerFeathers f = player.getData(FeathersAttachments.PLAYER_FEATHERS);
		syncFeatherAttributes(player);
		if (f.getFeathers() < f.getMaxFeathers() && (!f.isCold())) {
			if (!player.hasEffect(FeathersEffects.ENERGIZED)) {
				f.addCooldown(f.getRegen());
			} else {
				f.addCooldown(player.getActiveEffectsMap().get(FeathersEffects.ENERGIZED).getAmplifier() + 1 + f.getRegen());
			}
		}
		if (f.getCooldown() >= FeathersCommonConfig.COOLDOWN.get()) {
			FeathersHelper.addFeathers(player, 1);
		}
	}

	private static void syncFeatherAttributes(ServerPlayer player) {
		int maxFeathers = (int) player.getAttributeValue(FeathersAttributes.MAX_FEATHERS);
		int regen = (int) player.getAttributeValue(FeathersAttributes.FEATHER_REGEN);
		FeathersHelper.setMaxFeathers(player, maxFeathers);
		FeathersHelper.setFeatherRegen(player, regen);
	}

	@SubscribeEvent
	public static void playerTickEvent(PlayerTickEvent.Pre event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			regenerateFeathers(player);
			handleFrostEffect(player);
			handleEnduranceEffect(player);
		}
	}

	@SubscribeEvent
	public static void onPlayerChangeArmor(LivingEquipmentChangeEvent event) {
		if (event.getEntity() instanceof ServerPlayer player && event.getSlot().isArmor()) {
			PlayerFeathers f = player.getData(FeathersAttachments.PLAYER_FEATHERS);
			FeathersMessages.sendToPlayer(
					new FeatherSyncSTCPacket(f.getFeathers(), f.getMaxFeathers(), f.getRegen(), FeathersHelper.getPlayerWeight(player), f.getEnduranceFeathers()), player);
		}
	}

	@SubscribeEvent
	public static void onPlayerCloned(PlayerEvent.Clone event) {
		if (!event.isWasDeath()) {
			PlayerFeathers oldStore = event.getOriginal().getData(FeathersAttachments.PLAYER_FEATHERS);
			PlayerFeathers newStore = event.getEntity().getData(FeathersAttachments.PLAYER_FEATHERS);
			newStore.copyFrom(oldStore);
		}
	}
}