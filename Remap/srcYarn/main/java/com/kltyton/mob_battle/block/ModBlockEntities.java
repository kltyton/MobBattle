package com.kltyton.mob_battle.block;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.block.doubleblock.scarecrow.ScarecrowBlockEntity;
import com.kltyton.mob_battle.block.doubleblock.target.TargetBlockEntity;
import com.kltyton.mob_battle.block.mushroom.MushroomBlockEntity;
import com.kltyton.mob_battle.block.nest.NestBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<ScarecrowBlockEntity> SCARECROW_ENTITY;
    public static BlockEntityType<TargetBlockEntity> TARGET_ENTITY;
    public static BlockEntityType<NestBlockEntity> NEST_ENTITY;
    public static BlockEntityType<MushroomBlockEntity> MUSHROOM_ENTITY;

    public static void init() {
        SCARECROW_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(Mob_battle.MOD_ID, "scarecrow"),
                FabricBlockEntityTypeBuilder.create(ScarecrowBlockEntity::new, ModBlocks.SCARECROW_BLOCK).build()
        );
        TARGET_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(Mob_battle.MOD_ID, "target"),
                FabricBlockEntityTypeBuilder.create(TargetBlockEntity::new, ModBlocks.TARGET_BLOCK).build()
        );
        NEST_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(Mob_battle.MOD_ID, "nest"),
                FabricBlockEntityTypeBuilder.create(NestBlockEntity::new, ModBlocks.NEST_BLOCK).build()
        );
        MUSHROOM_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(Mob_battle.MOD_ID, "mushroom"),
                FabricBlockEntityTypeBuilder.create(MushroomBlockEntity::new, ModBlocks.MUSHROOM_BLOCK).build()
        );
    }
}
