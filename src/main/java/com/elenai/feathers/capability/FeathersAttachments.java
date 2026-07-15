package com.elenai.feathers.capability;

import com.elenai.feathers.Feathers;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class FeathersAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Feathers.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerFeathers>> PLAYER_FEATHERS =
            ATTACHMENT_TYPES.register("player_feathers", () -> AttachmentType.builder(PlayerFeathers::new)
                    .serialize(PlayerFeathers.CODEC)
                    .copyOnDeath()
                    .build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}