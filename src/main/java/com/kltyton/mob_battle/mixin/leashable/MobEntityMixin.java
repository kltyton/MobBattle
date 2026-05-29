package com.kltyton.mob_battle.mixin.leashable;

import com.kltyton.mob_battle.accessor.ILead;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
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
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(
            method = "canBeLeashed",
            at = @At("RETURN"),
            cancellable = true
    )
    private void allowUniversalLead(CallbackInfoReturnable<Boolean> cir) {
        if (this.isLeashed()) cir.setReturnValue(true); // 已经被拴住，不允许被拴住
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
        if (target != null && (target.isAlliedTo(this) || EntityUtil.shouldBlockOwnedSummonDamage(this, target))) ci.cancel();
    }
    @Inject(method = "doHurtTarget", at = @At("HEAD"), cancellable = true)
    private void preventOwnedSummonMelee(ServerLevel world, Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof LivingEntity living && EntityUtil.shouldBlockOwnedSummonDamage(this, living)) {
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
