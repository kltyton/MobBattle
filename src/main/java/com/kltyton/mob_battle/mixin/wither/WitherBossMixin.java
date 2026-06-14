package com.kltyton.mob_battle.mixin.wither;

import com.kltyton.mob_battle.entity.enhancedwither.EnhancedWitherEntity;
import com.kltyton.mob_battle.entity.enhancedwither.EnhancedWitherSkullEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WitherBoss.class)
public abstract class WitherBossMixin {
    @Redirect(
            method = "performRangedAttack(IDDDZ)V",
            at = @At(value = "NEW", target = "net/minecraft/world/entity/projectile/hurtingprojectile/WitherSkull")
    )
    private WitherSkull mob_battle$createEnhancedWitherSkull(Level world, LivingEntity owner, Vec3 direction) {
        if ((Object) this instanceof EnhancedWitherEntity) {
            return new EnhancedWitherSkullEntity(world, owner, direction);
        }
        return new WitherSkull(world, owner, direction);
    }
}
