package com.elenai.feathers.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class EnduranceEffect extends MobEffect {

    public EnduranceEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }


    @Override
    public void addAttributeModifiers(AttributeMap map, int amplifier) {
        super.addAttributeModifiers(map, amplifier);
    }

    @Override
    public void removeAttributeModifiers(AttributeMap map) {
        super.removeAttributeModifiers(map);
    }

}