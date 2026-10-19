package com.kltyton.mob_battle.mixin.wither;

import com.kltyton.mob_battle.entity.enhancedwither.EnhancedWitherEntity;
import com.kltyton.mob_battle.entity.enhancedwither.EnhancedWitherSkullEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.WindCharge;
import net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** 保留凋零原版受伤校验，将二阶段弹射物免疫替换为 80% 减伤，并定制强化凋零的骷髅头。 */
@Mixin(WitherBoss.class)
public abstract class WitherBossMixin {
    @ModifyExpressionValue(method = "hurtServer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;isPowered()Z"))
    private boolean mob_battle$allowPoweredProjectileDamage(boolean powered) {
        return false;
    }

    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true)
    private float mob_battle$reducePoweredProjectileDamage(float damage, ServerLevel level, DamageSource source, float originalDamage) {
        // 仅替换受伤分支，不影响二阶段外观、AI、出生无敌以及同阵营免疫。
        boolean projectile = source.is(DamageTypeTags.IS_PROJECTILE)
                || source.getDirectEntity() instanceof AbstractArrow
                || source.getDirectEntity() instanceof WindCharge;
        return ((WitherBoss) (Object) this).isPowered() && projectile ? damage * 0.2F : damage;
    }

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
