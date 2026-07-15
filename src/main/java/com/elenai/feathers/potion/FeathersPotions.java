package com.elenai.feathers.potion;

import com.elenai.feathers.Feathers;
import com.elenai.feathers.effect.FeathersEffects;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FeathersPotions {

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, Feathers.MODID);

    public static final DeferredHolder<Potion, Potion> ENDURANCE_POTION = POTIONS.register("endurance_potion", () -> new Potion(new MobEffectInstance(FeathersEffects.ENDURANCE, 2600, 0)));
    public static final DeferredHolder<Potion, Potion> STRONG_ENDURANCE_POTION = POTIONS.register("strong_endurance_potion", () -> new Potion(new MobEffectInstance(FeathersEffects.ENDURANCE, 2000, 1)));
    public static final DeferredHolder<Potion, Potion> LONG_ENDURANCE_POTION = POTIONS.register("long_endurance_potion", () -> new Potion(new MobEffectInstance(FeathersEffects.ENDURANCE, 4200, 0)));

    public static final DeferredHolder<Potion, Potion> COLD_POTION = POTIONS.register("cold_potion", () -> new Potion(new MobEffectInstance(FeathersEffects.COLD, 5000, 0)));

    public static final DeferredHolder<Potion, Potion> ENERGIZED_POTION = POTIONS.register("energized_potion", () -> new Potion(new MobEffectInstance(FeathersEffects.ENERGIZED, 1600, 0)));
    public static final DeferredHolder<Potion, Potion> STRONG_ENERGIZED_POTION = POTIONS.register("strong_energized_potion", () -> new Potion(new MobEffectInstance(FeathersEffects.ENERGIZED, 1000, 1)));
    public static final DeferredHolder<Potion, Potion> LONG_ENERGIZED_POTION = POTIONS.register("long_energized_potion", () -> new Potion(new MobEffectInstance(FeathersEffects.ENERGIZED, 2600, 0)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }

}