package com.elenai.feathers;

import com.elenai.feathers.attributes.FeathersAttributes;
import com.elenai.feathers.capability.FeathersAttachments;
import com.elenai.feathers.config.FeathersClientConfig;
import com.elenai.feathers.config.FeathersCommonConfig;
import com.elenai.feathers.effect.FeathersEffects;
import com.elenai.feathers.potion.FeathersPotions;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Feathers.MODID)
public class Feathers {

    public static final String MODID = "feathers";
    public static final Logger logger = LogManager.getLogger(MODID);
    public static final boolean OB_LOADED = ModList.get().isLoaded("overflowingbars");

    public Feathers(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, FeathersClientConfig.SPEC, "Feathers-Client.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, FeathersCommonConfig.SPEC, "Feathers-Common.toml");

        FeathersAttributes.register(modEventBus);
        FeathersEffects.register(modEventBus);
        FeathersPotions.register(modEventBus);
        FeathersAttachments.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::registerBrewingRecipes);
    }

    private void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        // Cold
        builder.addMix(Potions.AWKWARD, Items.SNOWBALL, FeathersPotions.COLD_POTION);

        // Endurance
        builder.addMix(Potions.AWKWARD, Items.FEATHER, FeathersPotions.ENDURANCE_POTION);
        builder.addMix(FeathersPotions.ENDURANCE_POTION, Items.REDSTONE, FeathersPotions.LONG_ENDURANCE_POTION);
        builder.addMix(FeathersPotions.ENDURANCE_POTION, Items.GLOWSTONE_DUST, FeathersPotions.STRONG_ENDURANCE_POTION);

        // Energized
        builder.addMix(Potions.AWKWARD, Items.RAW_COPPER, FeathersPotions.ENERGIZED_POTION);
        builder.addMix(FeathersPotions.ENERGIZED_POTION, Items.REDSTONE, FeathersPotions.LONG_ENERGIZED_POTION);
        builder.addMix(FeathersPotions.ENERGIZED_POTION, Items.GLOWSTONE_DUST, FeathersPotions.STRONG_ENERGIZED_POTION);
    }
}