package com.elenai.feathers.config;

import java.util.List;

import com.google.common.collect.Lists;

import net.neoforged.neoforge.common.ModConfigSpec;

public class FeathersCommonConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Integer> COOLDOWN;
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_ARMOR_WEIGHTS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ARMOR_WEIGHTS;
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_FROST_EFFECTS;
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_LIGHTWEIGHT_ENCHANTMENT;

    static {
        BUILDER.push("Feathers' Config");

        COOLDOWN = BUILDER.comment("How many ticks it takes to regenerate half a feather.")
                .define("Feathers Cooldown", 40);

        // Default list is empty; any armor not listed here falls back to its vanilla
        // defense value (see ArmorHandler#getArmorWeight). Add entries as
        // "namespace:item_id:weight", e.g. "minecraft:leather_helmet:3".
        ARMOR_WEIGHTS = BUILDER.comment("How many half feathers each item weighs. Format: \"namespace:item_id:weight\", e.g. \"minecraft:leather_helmet:3\". Any armor not listed uses its vanilla defense value instead.")
                .defineList("Armor Weights Override", Lists.newArrayList(), o -> o instanceof String);

        ENABLE_ARMOR_WEIGHTS = BUILDER.comment("If enabled, armor types have weight, this reduces the amount of feathers you can use based on how heavy your armor is").define("Enable Armor Weights", true);
        ENABLE_FROST_EFFECTS = BUILDER.comment("Whether feathers freeze in cold biomes. If they do, they don't regenerate until in a different biome")
                .define("Enable Frost In Cold Biomes", false);
        ENABLE_LIGHTWEIGHT_ENCHANTMENT = BUILDER.comment("Whether the Lightweight enchantment can be enhanted in an enchantment table, or if it is treasure only.")
                .define("Enable Lightweight Enchantment in Table", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}