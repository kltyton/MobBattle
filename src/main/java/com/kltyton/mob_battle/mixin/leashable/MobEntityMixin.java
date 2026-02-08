package com.kltyton.mob_battle.mixin.leashable;

import com.kltyton.mob_battle.accessor.ILead;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements EquipmentHolder, Leashable, Targeter {
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
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
    @Inject(method = "snapLongLeash", at = @At("RETURN"), cancellable = true)
    private void allowUniversalLead(CallbackInfo ci) {
        if (((ILead)this).getIsUniversalLeadEnyity()) ci.cancel();
    }
    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void allowUniversalLead(LivingEntity target, CallbackInfo ci) {
        if (target != null && target.isTeammate(this)) ci.cancel();
    }
    @Inject(method = "tryAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z"), cancellable = true)
    private void cancelMeleeAttack(ServerWorld world, Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (this.getAttributes().hasAttribute(ModEntityAttributes.MAGIC_DAMAGE)) {
            target.damage(world, this.getDamageSources().indirectMagic(this, this), (float) this.getAttributeValue(ModEntityAttributes.MAGIC_DAMAGE));
        }
    }
}
