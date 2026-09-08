package com.kltyton.mob_battle.mixin.leashable;

import com.kltyton.mob_battle.accessor.ILead;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentUser;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobEntityMixin extends LivingEntity implements EquipmentUser, Leashable, Targeting {
    private static final int GLOBAL_REGENERATION_INTERVAL_TICKS = 10 * 20;

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(
            method = "canBeLeashed",
            at = @At("RETURN"),
            cancellable = true
    )
    private void allowUniversalLead(CallbackInfoReturnable<Boolean> cir) {
        if (this.isLeashed()) cir.setReturnValue(false); // 已经被拴住，不允许再次拴住
        if (((ILead)this).getIsUniversalLeadEnyity()) {
            cir.setReturnValue(true); // 允许被拴住
        }
    }
    @Inject(method = "leashTooFarBehaviour", at = @At("RETURN"), cancellable = true)
    private void allowUniversalLead(CallbackInfo ci) {
        if (((ILead)this).getIsUniversalLeadEnyity()) ci.cancel();
    }
    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void allowUniversalLead(LivingEntity target, CallbackInfo ci) {
        if (target != null && (EntityQueries.areTeammates(this, target)
                || EntityQueries.shouldBlockOwnedSummonDamage(this, target))) ci.cancel();
    }
    @Inject(method = "serverAiStep", at = @At("HEAD"))
    private void clearAlliedCurrentTarget(CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;
        LivingEntity target = mob.getTarget();
        if (target != null && !EntityQueries.isValidCombatTarget(mob, target)) {
            mob.setTarget(null);
            mob.getNavigation().stop();
        }
    }

    /**
     * 为全部 Mob 提供独立的基础恢复。这里直接追加一次 heal，不读取也不修改其他恢复效果，
     * 因而实体自带的回血、药水效果和本规则会自然叠加。
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void mobBattle$applyGlobalMobRegeneration(CallbackInfo ci) {
        if (!this.level().isClientSide()
                && this.tickCount % GLOBAL_REGENERATION_INTERVAL_TICKS == 0
                && this.deathTime == 0
                && !this.isDeadOrDying()
                && this.getHealth() < this.getMaxHealth()) {
            this.heal(1.0F);
        }
    }

    @Inject(method = "doHurtTarget", at = @At("HEAD"), cancellable = true)
    private void preventOwnedSummonMelee(ServerLevel world, Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof LivingEntity living && !EntityQueries.isValidCombatTarget(this, living)) {
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void cancelMeleeAttack(ServerLevel world, Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (this.getAttributes().hasAttribute(ModEntityAttributes.MAGIC_DAMAGE)) {
            target.hurtServer(world, this.damageSources().indirectMagic(this, this), (float) this.getAttributeValue(ModEntityAttributes.MAGIC_DAMAGE));
        }
    }
}
