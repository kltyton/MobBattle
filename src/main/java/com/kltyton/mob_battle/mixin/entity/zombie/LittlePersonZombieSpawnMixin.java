package com.kltyton.mob_battle.mixin.entity.zombie;

import com.kltyton.mob_battle.event.littleperson.zombie.ZombieLittlePersonLifecycle;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 记录原版生成原因，使 ENTITY_LOAD 能区分新生成与存档重载。 */
@Mixin(Mob.class)
public abstract class LittlePersonZombieSpawnMixin {
    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void mob_battle$markLittlePersonRoll(ServerLevelAccessor level, DifficultyInstance difficulty,
            EntitySpawnReason reason, SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        ZombieLittlePersonLifecycle.markSpawn((Mob) (Object) this, reason);
    }
}
