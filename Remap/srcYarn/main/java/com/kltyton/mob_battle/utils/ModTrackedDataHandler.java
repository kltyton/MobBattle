package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.Mob_battle;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.world.entity.UniquelyIdentifiable;

import java.util.Optional;

public class ModTrackedDataHandler {
    public static final TrackedDataHandler<Optional<LazyEntityReference<UniquelyIdentifiable>>> ANY_ENTITY_LAZY_REFERENCE =
            TrackedDataHandler.create(
                    LazyEntityReference.createPacketCodec().collect(PacketCodecs::optional)
            );

    private static final Identifier ANY_ENTITY_LAZY_REFERENCE_ID = Identifier.of(Mob_battle.MOD_ID, "any_entity_lazy_reference");

    public static void init() {
        FabricTrackedDataRegistry.register(ANY_ENTITY_LAZY_REFERENCE_ID, ANY_ENTITY_LAZY_REFERENCE);
    }
}
