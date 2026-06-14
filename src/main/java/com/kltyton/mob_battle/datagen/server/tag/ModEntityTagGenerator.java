package com.kltyton.mob_battle.datagen.server.tag;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.tags.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import java.util.concurrent.CompletableFuture;

public class ModEntityTagGenerator extends FabricTagsProvider.EntityTypeTagsProvider {

    public ModEntityTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        valueLookupBuilder(ModTags.SILENCE_PHANTOM_CANNOT_ATTACK)
                .add(EntityType.WARDEN)
                .add(ModEntities.XUN_SHENG)
                .add(ModEntities.DEEP_CREATURE)
                .add(ModEntities.SILENCE_PHANTOM)
                .add(ModEntities.YOUNG_MIN)
                .add(ModEntities.HIDDEN_EYE);
        valueLookupBuilder(EntityTypeTags.UNDEAD).add(
                ModEntities.WITHER_SKELETON_KING,
                ModEntities.SKULL_KING,
                ModEntities.SKULL_ARCHER,
                ModEntities.SKULL_MAGE,
                ModEntities.SKULL_WARRIOR,
                ModEntities.WITHER_SKELETON_DOG,
                ModEntities.DUAL_BLADE_WITHER_SKELETON,
                ModEntities.SHIELD_AXE_WITHER_SKELETON,
                ModEntities.BOW_ZOMBIE_MOD
        );
        valueLookupBuilder(EntityTypeTags.ZOMBIES)
                .add(ModEntities.BOW_ZOMBIE_MOD);
        valueLookupBuilder(EntityTypeTags.BURN_IN_DAYLIGHT)
                .add(ModEntities.BOW_ZOMBIE_MOD);
        valueLookupBuilder(EntityTypeTags.CAN_BREATHE_UNDER_WATER)
                .add(ModEntities.MAGMA_LOBSTER)
                .add(ModEntities.LOBSTER);
    }
}

