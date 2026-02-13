package com.kltyton.mob_battle.datagen.server.tag;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.tags.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class ModEntityTagGenerator extends FabricTagProvider.EntityTypeTagProvider {

    public ModEntityTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        valueLookupBuilder(ModTags.SILENCE_PHANTOM_CANNOT_ATTACK)
                .add(EntityType.WARDEN)
                .add(ModEntities.XUN_SHENG)
                .add(ModEntities.DEEP_CREATURE)
                .add(ModEntities.SILENCE_PHANTOM)
                .add(ModEntities.YOUNG_MIN)
                .add(ModEntities.HIDDEN_EYE);
        valueLookupBuilder(ModTags.ATTACK_HEAL_ENTITY)
                .add(ModEntities.WITHER_SKELETON_KING)
                .add(ModEntities.VINDICATOR_GENERAL)
                .add(ModEntities.HULKBUSTER);
        valueLookupBuilder(EntityTypeTags.UNDEAD).add(
                ModEntities.WITHER_SKELETON_KING,
                ModEntities.SKULL_KING,
                ModEntities.SKULL_ARCHER,
                ModEntities.SKULL_MAGE,
                ModEntities.SKULL_WARRIOR
        );
    }
}

