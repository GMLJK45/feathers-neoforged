package com.elenai.feathers.util;

import java.util.HashMap;

import com.elenai.feathers.Feathers;
import com.elenai.feathers.config.FeathersCommonConfig;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class ArmorHandler {
    private static HashMap<String, Integer> map = new HashMap<>();
    private static boolean loaded = false;

    public static HashMap<String, Integer> getWeights() {
        if (!loaded) {
            populateWeights();
        }
        return map;
    }

    public static void populateWeights() {
        map.clear();
        FeathersCommonConfig.ARMOR_WEIGHTS.get().forEach(value -> {

            int lastColon = value.lastIndexOf(':');
            try {
                String name = value.substring(0, lastColon);
                int weight = Integer.parseInt(value.substring(lastColon + 1));
                map.putIfAbsent(name, weight);
            } catch (Exception e) {
                Feathers.logger.warn(e + " error! Armor value not set as an integer.");
            }
        });
        loaded = true;
    }

    /**
     * Looks weight up by the item's registry name (e.g. "minecraft:leather_helmet"),
     * rather than its translation key. Registry names are stable, locale-independent,
     * and match what users would naturally type into the config.
     */
    public static int getArmorWeight(ArmorItem armor) {
        String key = BuiltInRegistries.ITEM.getKey(armor).toString();
        return getWeights().getOrDefault(key, armor.getDefense());
    }

    /**
     * Returns the enchantment's level across the entity's applicable equipment slots.
     *
     * NOTE: as of the 1.21 enchantment rewrite, enchantments are data-driven and referenced via
     * Holder<Enchantment> rather than a plain Enchantment instance. Also, unlike the old Forge
     * helper (which summed levels across all equipped items), vanilla's
     * EnchantmentHelper.getEnchantmentLevel(Holder, LivingEntity) returns the HIGHEST level found
     * in the enchantment's applicable slots, not a cumulative total. If you actually need a sum
     * across pieces (e.g. multiple lightweight-enchanted armor pieces stacking), you'll need to
     * manually iterate the entity's equipment slots and add up itemstack.getEnchantmentLevel(enchantment)
     * yourself instead of using this helper.
     *
     * @author Diesieben07
     * @param enchantment
     * @param entity
     * @return
     */
    public static int getTotalEnchantmentLevel(Holder<Enchantment> enchantment, LivingEntity entity) {
        return EnchantmentHelper.getEnchantmentLevel(enchantment, entity);
    }

    /**
     * Returns the level of an item enchantment type, looked up by ResourceKey.
     * <p>
     * NOTE: FeathersEnchantments now exposes ResourceKey<Enchantment> rather than
     * Holder<Enchantment> (enchantments are a datapack registry as of 1.21, so there's
     * no DeferredHolder to pull a Holder from at class-load time). ItemStack's own
     * enchantment map is still keyed by Holder<Enchantment>, but Holder#is(ResourceKey)
     * lets us match against a key without needing a RegistryAccess lookup.
     *
     * @author Elenai
     * @param enchantment
     * @param itemstack
     * @return
     */
    public static int getItemEnchantmentLevel(ResourceKey<Enchantment> enchantment, ItemStack itemstack) {
        for (var entry : itemstack.getEnchantments().entrySet()) {
            if (entry.getKey().is(enchantment)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    /**
     * Returns the level of an item enchantment type, looked up by an already-resolved Holder.
     * <p>
     * Use this overload when the caller already has a Holder<Enchantment> on hand (e.g. from
     * iterating an ItemStack's own enchantment map elsewhere), rather than a ResourceKey from
     * FeathersEnchantments. Delegates straight to ItemStack's own vanilla lookup.
     *
     * @author Elenai
     * @param enchantment
     * @param itemstack
     * @return
     */
    public static int getItemEnchantmentLevel(Holder<Enchantment> enchantment, ItemStack itemstack) {
        return itemstack.getEnchantmentLevel(enchantment);
    }
}