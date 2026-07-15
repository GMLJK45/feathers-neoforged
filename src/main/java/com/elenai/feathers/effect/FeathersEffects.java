package com.elenai.feathers.effect;

import com.elenai.feathers.Feathers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FeathersEffects {

	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Feathers.MODID);
	
	public static final DeferredHolder<MobEffect, MobEffect> ENDURANCE = EFFECTS.register("endurance", () -> new EnduranceEffect(MobEffectCategory.BENEFICIAL, 16776960));
	public static final DeferredHolder<MobEffect, MobEffect> COLD = EFFECTS.register("cold", () -> new ColdEffect(MobEffectCategory.HARMFUL, 11993087));
	public static final DeferredHolder<MobEffect, MobEffect> ENERGIZED = EFFECTS.register("energized", () -> new EnergizedEffect(MobEffectCategory.BENEFICIAL, 7458303));

	
	public static void register(IEventBus eventBus) {
		EFFECTS.register(eventBus);
	}
}
