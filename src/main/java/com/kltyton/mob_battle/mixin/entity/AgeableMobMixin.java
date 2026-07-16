package com.kltyton.mob_battle.mixin.entity;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.golem.StrongMinEntity;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AgeableMob.class)
public abstract class AgeableMobMixin {
    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void mobBattle$convertStructureVillagerToStrongMin(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        AgeableMob self = (AgeableMob) (Object) this;
        if (!(self instanceof Villager villager)
                || villager.isBaby()
                || !(villager.level() instanceof ServerLevel level)
                || (spawnReason != EntitySpawnReason.STRUCTURE && spawnReason != EntitySpawnReason.CHUNK_GENERATION)) {
            return;
        }
        if (villager.getRandom().nextFloat() >= 0.12F) {
            return;
        }
        StrongMinEntity strongMin = ModEntities.STRONG_MIN.create(level, EntitySpawnReason.STRUCTURE);
        if (strongMin == null) {
            return;
        }
        strongMin.snapTo(villager.getX(), villager.getY(), villager.getZ(), villager.getYRot(), villager.getXRot());
        strongMin.finalizeSpawn(level, level.getCurrentDifficultyAt(strongMin.blockPosition()), EntitySpawnReason.STRUCTURE, null);
        level.addFreshEntity(strongMin);
        villager.discard();
    }
}
