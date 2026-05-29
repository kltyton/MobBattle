package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.Mob_battle;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.level.entity.UniquelyIdentifyable;
import java.util.Optional;

public class ModTrackedDataHandler {
    public static final EntityDataSerializer<Optional<EntityReference<UniquelyIdentifyable>>> ANY_ENTITY_LAZY_REFERENCE =
            EntityDataSerializer.forValueType(
                    EntityReference.streamCodec().apply(ByteBufCodecs::optional)
            );

    private static final ResourceLocation ANY_ENTITY_LAZY_REFERENCE_ID = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "any_entity_lazy_reference");

    public static void init() {
        FabricTrackedDataRegistry.register(ANY_ENTITY_LAZY_REFERENCE_ID, ANY_ENTITY_LAZY_REFERENCE);
    }
}
