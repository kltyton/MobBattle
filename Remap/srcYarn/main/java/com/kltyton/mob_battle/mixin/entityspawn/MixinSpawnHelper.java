package com.kltyton.mob_battle.mixin.entityspawn;

import com.kltyton.mob_battle.tags.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnHelper.class)
public class MixinSpawnHelper {
    @Inject(method = "canSpawn", at = @At("HEAD"), cancellable = true)
    private static void preventSculkSpawning(ServerWorld world, SpawnGroup group, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnSettings.SpawnEntry spawnEntry, BlockPos.Mutable pos, double squaredDistance, CallbackInfoReturnable<Boolean> cir) {
        EntityType<?> entityType = spawnEntry.type();
        BlockState stateBelow = world.getBlockState(pos.down());
        if (entityType != EntityType.WARDEN && stateBelow.isIn(ModTags.SCULK_BLOCKS)) {
            cir.setReturnValue(false);
        }
    }
}
