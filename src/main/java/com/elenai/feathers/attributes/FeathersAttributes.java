package com.elenai.feathers.attributes;

import com.elenai.feathers.Feathers;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

public class FeathersAttributes {

    public static final HashMap<DeferredHolder<Attribute, Attribute>, UUID> UUIDS = new HashMap<>();
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, Feathers.MODID);

    public static final DeferredHolder<Attribute, Attribute> MAX_FEATHERS = registerAttribute("feathers.max_feathers", (id) -> new RangedAttribute(id, 20.0D, 0.0D, 1024.0D).setSyncable(true), "1ce4960d-c50e-44bf-ad23-7bcd77f4c1dc");
    public static final DeferredHolder<Attribute, Attribute> FEATHER_REGEN = registerAttribute("feathers.feather_regen", (id) -> new RangedAttribute(id, 1.0D, 0.0D, 1024.0D).setSyncable(true), "d74ded8f-c5b6-4222-80e2-dbea7ccf8d02");

    public static DeferredHolder<Attribute, Attribute> registerAttribute(String name, Function<String, Attribute> attribute, String uuid) {
        return registerAttribute(name, attribute, UUID.fromString(uuid));
    }

    public static DeferredHolder<Attribute, Attribute> registerAttribute(String name, Function<String, Attribute> attribute, UUID uuid) {
        DeferredHolder<Attribute, Attribute> registryObject = ATTRIBUTES.register(name, () -> attribute.apply(name));
        UUIDS.put(registryObject, uuid);
        return registryObject;
    }

    public static void register(IEventBus modEventBus) {
        ATTRIBUTES.register(modEventBus);
        modEventBus.addListener(FeathersAttributes::modifyEntityAttributes);
    }

    @SuppressWarnings("unchecked")
    public static void modifyEntityAttributes(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> e : event.getTypes())
            if (e == EntityType.PLAYER) for (DeferredHolder<Attribute, ? extends Attribute> v : ATTRIBUTES.getEntries())
                event.add(e, (Holder<Attribute>) (Holder<?>) v);
    }

}