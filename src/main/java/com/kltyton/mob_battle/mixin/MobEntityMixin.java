package com.kltyton.mob_battle.mixin;

import com.kltyton.mob_battle.accessor.LeadAccessor;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.storage.ReadView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
        if (LeadAccessor.isUniversalLead) {
            LeadAccessor.isUniversalLead = false;
            cir.setReturnValue(true); // 允许被拴住
        }
    }
    @Inject(method = "snapLongLeash", at = @At("RETURN"), cancellable = true)
    private void allowUniversalLead(CallbackInfo ci) {
        if (this instanceof Monster) ci.cancel();
    }
}
