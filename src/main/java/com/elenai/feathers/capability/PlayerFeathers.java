package com.elenai.feathers.capability;

import com.elenai.feathers.config.FeathersCommonConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PlayerFeathers {

    public static final Codec<PlayerFeathers> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("feathers").orElse(20).forGetter(PlayerFeathers::getFeathers),
            Codec.INT.fieldOf("max_feathers").orElse(20).forGetter(PlayerFeathers::getMaxFeathers),
            Codec.INT.fieldOf("cooldown").orElse(0).forGetter(PlayerFeathers::getCooldown),
            Codec.INT.fieldOf("cooldown_reduction").orElse(1).forGetter(PlayerFeathers::getRegen),
            Codec.INT.fieldOf("endurance_feathers").orElse(0).forGetter(PlayerFeathers::getEnduranceFeathers),
            Codec.BOOL.fieldOf("cold").orElse(false).forGetter(PlayerFeathers::isCold)
    ).apply(instance, PlayerFeathers::new));

    private int feathers = 20;
    private int maxFeathers = 20;
    private int cooldownReduction = 1;
    private final int MIN_FEATHERS = 0;

    private int enduranceFeathers = 0;

    private int cooldown = 0;
    private final int MIN_COOLDOWN = 0;

    private boolean cold = false;

    public PlayerFeathers() {
    }

    // Used by CODEC to reconstruct a saved instance
    private PlayerFeathers(int feathers, int maxFeathers, int cooldown, int cooldownReduction, int enduranceFeathers, boolean cold) {
        this.feathers = feathers;
        this.maxFeathers = maxFeathers;
        this.cooldown = cooldown;
        this.cooldownReduction = cooldownReduction;
        this.enduranceFeathers = enduranceFeathers;
        this.cold = cold;
    }

    public int getFeathers() {
        return feathers;
    }

    public void setFeathers(int feathers) {
        this.feathers = feathers;
    }

    public int getMaxFeathers() {
        return maxFeathers;
    }

    public void setMaxFeathers(int feathers) {
        this.maxFeathers = feathers;
        if (getFeathers() > feathers) {
            setFeathers(feathers);
        }
    }

    public int getRegen() {
        return this.cooldownReduction;
    }

    public void setRegen(int ticks) {
        this.cooldownReduction = ticks;
    }

    public void addFeathers(int feathers) {
        this.feathers = Math.min(this.feathers + feathers, maxFeathers);
    }

    public void subFeathers(int feathers) {
        this.feathers = Math.max(this.feathers - feathers, MIN_FEATHERS);
    }

    public void copyFrom(PlayerFeathers source) {
        this.feathers = source.feathers;
        this.cooldown = source.cooldown;
        this.enduranceFeathers = source.enduranceFeathers;
        this.cold = source.cold;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public void addCooldown(int ticks) {
        this.cooldown = Math.min(this.cooldown + ticks, FeathersCommonConfig.COOLDOWN.get());
    }

    public void subCooldown(int ticks) {
        this.cooldown = Math.max(this.cooldown - ticks, MIN_COOLDOWN);
    }

    public boolean isCold() {
        return cold;
    }

    public void setCold(boolean cold) {
        this.cold = cold;
    }

    public int getEnduranceFeathers() {
        return enduranceFeathers;
    }

    public void setEnduranceFeathers(int enduranceFeathers) {
        this.enduranceFeathers = enduranceFeathers;
    }

    public void addEndurance(int feathers) {
        this.enduranceFeathers = this.enduranceFeathers + feathers;
    }

    public void subEndurance(int feathers) { //TODO: Put this in the api to access it, we need to ensure if we overlap into regular feathers
        this.enduranceFeathers = Math.max(this.enduranceFeathers - feathers, 0);
    }
}