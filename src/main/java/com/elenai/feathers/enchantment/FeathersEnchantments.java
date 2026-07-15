package com.elenai.feathers.enchantment;

import com.elenai.feathers.Feathers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class FeathersEnchantments {

	public static final ResourceKey<Enchantment> LIGHTWEIGHT = ResourceKey.create(Registries.ENCHANTMENT,
			ResourceLocation.fromNamespaceAndPath(Feathers.MODID, "lightweight"));

	public static final ResourceKey<Enchantment> HEAVY = ResourceKey.create(Registries.ENCHANTMENT,
			ResourceLocation.fromNamespaceAndPath(Feathers.MODID, "heavy"));
}
