package com.kltyton.mob_battle.block;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.block.doubleblock.ScarecrowBlockEntity;
import com.kltyton.mob_battle.block.doubleblock.TargetBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<ScarecrowBlockEntity> SCARECROW_ENTITY;
    public static BlockEntityType<TargetBlockEntity> TARGET_ENTITY;

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
    }
}
