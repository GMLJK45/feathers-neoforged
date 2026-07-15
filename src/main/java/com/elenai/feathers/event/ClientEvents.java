package com.elenai.feathers.event;

import java.util.List;

import com.elenai.feathers.Feathers;
import com.elenai.feathers.client.ClientFeathersData;
import com.elenai.feathers.client.gui.FeathersHudOverlay;
import com.elenai.feathers.config.FeathersClientConfig;
import com.elenai.feathers.enchantment.FeathersEnchantments;
import com.elenai.feathers.networking.FeathersMessages;
import com.elenai.feathers.networking.packet.RequestWeightCTSPacket;
import com.elenai.feathers.util.ArmorHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class ClientEvents {

	public static int currentWeight;

	@EventBusSubscriber(modid = Feathers.MODID, value = Dist.CLIENT)
	public static class ClientModBusEvents {
		@SubscribeEvent
		public static void registerGuiLayers(RegisterGuiLayersEvent event) {


			event.registerAbove(VanillaGuiLayers.FOOD_LEVEL,
					ResourceLocation.fromNamespaceAndPath(Feathers.MODID, "feathers"), FeathersHudOverlay.FEATHERS);
		}
	}

	@EventBusSubscriber(modid = Feathers.MODID, value = Dist.CLIENT)
	public static class ClientForgeEvents {

		@SubscribeEvent
		public static void clientTickEvents(ClientTickEvent.Pre event) {
			if (Minecraft.getInstance().level != null) {
				ClientFeathersData.setOverflowing(ClientFeathersData.getFeathers() > 20);

				if (ClientFeathersData.getAnimationCooldown() > 0) { // TODO: improve this animation
					ClientFeathersData.setAnimationCooldown(ClientFeathersData.getAnimationCooldown() - 1);
				}

				if (ClientFeathersData.getFeathers() != ClientFeathersData.getPreviousFeathers()) {
					if (ClientFeathersData.getFeathers() > ClientFeathersData.getPreviousFeathers()
							&& FeathersClientConfig.REGEN_EFFECT.get()) {
						ClientFeathersData.setAnimationCooldown(18);
					}
					ClientFeathersData.setPreviousFeathers(ClientFeathersData.getFeathers());
				}

				if (FeathersClientConfig.FADE_WHEN_FULL.get()) {
					int cooldown = ClientFeathersData.getFadeCooldown();
					if (ClientFeathersData.getFeathers() == ClientFeathersData.getMaxFeathers()
							|| ClientFeathersData.getEnduranceFeathers() > 0) {
						if (cooldown < FeathersClientConfig.FADE_COOLDOWN.get()) {
							ClientFeathersData.setFadeCooldown(ClientFeathersData.getFadeCooldown() + 1);
						}
					} else {
						ClientFeathersData.setFadeCooldown(0);
					}
				}
			}
		}

		@SubscribeEvent
		public static void tooltipRenderer(ItemTooltipEvent event) {
			if (Minecraft.getInstance().level != null) {
				if (!event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof ArmorItem
						&& FeathersClientConfig.DISPLAY_WEIGHTS.get()) { // Surprisingly easy way to render feathers using
					// fonts

					// ASSUMPTION: FeathersEnchantments.LIGHTWEIGHT/HEAVY are now ResourceKey<Enchantment>
					// constants (data-driven enchantments), so they need resolving to a Holder<Enchantment>
					// via registry access before use. Send FeathersEnchantments.java over to confirm this.
					HolderLookup.Provider registries = Minecraft.getInstance().level.registryAccess();
					Holder<Enchantment> lightweight = registries.holderOrThrow(FeathersEnchantments.LIGHTWEIGHT);
					Holder<Enchantment> heavy = registries.holderOrThrow(FeathersEnchantments.HEAVY);

					FeathersMessages.sendToServer(new RequestWeightCTSPacket(Item.getId(event.getItemStack().getItem()),
							ArmorHandler.getItemEnchantmentLevel(lightweight, event.getItemStack()),
							ArmorHandler.getItemEnchantmentLevel(heavy, event.getItemStack())));
					if (currentWeight > 0) {
						StringBuilder s = new StringBuilder();
						List<Component> tooltip = event.getToolTip();
						if (FeathersClientConfig.VISUAL_WEIGHTS.get()) {
							for (int i = 2; i <= currentWeight + 1; i += 2) {
								if (i - 1 == currentWeight) {
									s.append("b");
								} else {
									s.append("a ");
								}
							}
							s.reverse();
							tooltip.add(Component.literal(s.toString())
									.withStyle(Style.EMPTY.withFont(ResourceLocation.fromNamespaceAndPath(Feathers.MODID, "feather_font"))));
						} else {
							tooltip.add(Component.translatable("text.feathers.tooltip", currentWeight).withStyle(ChatFormatting.BLUE));
						}
					}
				}
			}
		}
	}
}