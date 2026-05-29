package com.kltyton.mob_battle.block;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.block.doubleblock.scarecrow.ScarecrowBlockEntity;
import com.kltyton.mob_battle.block.doubleblock.target.TargetBlockEntity;
import com.kltyton.mob_battle.block.mushroom.MushroomBlockEntity;
import com.kltyton.mob_battle.block.nest.NestBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static BlockEntityType<ScarecrowBlockEntity> SCARECROW_ENTITY;
    public static BlockEntityType<TargetBlockEntity> TARGET_ENTITY;
    public static BlockEntityType<NestBlockEntity> NEST_ENTITY;
    public static BlockEntityType<MushroomBlockEntity> MUSHROOM_ENTITY;

    public static void init() {
        SCARECROW_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "scarecrow"),
                FabricBlockEntityTypeBuilder.create(ScarecrowBlockEntity::new, ModBlocks.SCARECROW_BLOCK).build()
        );
        TARGET_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "target"),
                FabricBlockEntityTypeBuilder.create(TargetBlockEntity::new, ModBlocks.TARGET_BLOCK).build()
        );
        NEST_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "nest"),
                FabricBlockEntityTypeBuilder.create(NestBlockEntity::new, ModBlocks.NEST_BLOCK).build()
        );
        MUSHROOM_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "mushroom"),
                FabricBlockEntityTypeBuilder.create(MushroomBlockEntity::new, ModBlocks.MUSHROOM_BLOCK).build()
        );
    }
}
