package com.kltyton.mob_battle.mixin;

import com.kltyton.mob_battle.command.FriendlyDamageCommand;
import com.kltyton.mob_battle.entity.bullet.ITrueDamageProjectile;
import com.kltyton.mob_battle.entity.villager.militia.MilitiaArcherVillager;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractArrow.class)
public abstract class PersistentProjectileEntityMixin extends Projectile{

    @Shadow
    private double baseDamage;

    public PersistentProjectileEntityMixin(EntityType<? extends Projectile> entityType, Level world) {
        super(entityType, world);
    }
    @Redirect(
            method = "onHitEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z")
    )
    private boolean cancelDamage(Entity instance, DamageSource source, float amount) {
        if ((AbstractArrow) (Object) this instanceof ITrueDamageProjectile trueDamageProjectile && trueDamageProjectile.isTrueDamage()) {
            trueDamageProjectile.additionalDamage(instance);
            return instance.hurtOrSimulate(source, (float) this.baseDamage);
        } else {
            return instance.hurtOrSimulate(source, amount);
        }
    }
    @Redirect(
            method = "onHitEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;arrow(Lnet/minecraft/world/entity/projectile/AbstractArrow;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/damagesource/DamageSource;")
    )
    private DamageSource modifyDamageSource(DamageSources instance, AbstractArrow source, Entity attacker) {
        if ((AbstractArrow) (Object) this instanceof ITrueDamageProjectile trueDamageProjectile && trueDamageProjectile.isMage()) {
            return instance.indirectMagic(source, (attacker != null ? attacker : this));
        } else {
            return instance.arrow(source, (attacker != null ? attacker : this));
        }
    }
    @Inject(
            method = "onHitEntity",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onHitDog(EntityHitResult entityHitResult, CallbackInfo ci) {
        Entity target = entityHitResult.getEntity();
        AbstractArrow projectile = (AbstractArrow) (Object) this;
        Entity owner = projectile.getOwner();
        Level world = projectile.level();
        if (owner instanceof MilitiaArcherVillager && target instanceof LivingEntity living && mobBattle$isMilitiaArrowPassThroughTarget(living)) {
            ci.cancel();
            return;
        }
        if (owner != null && target instanceof LivingEntity living && EntityUtil.shouldBlockOwnedSummonDamage(owner, living)) {
            ci.cancel();
            return;
        }
        if (world instanceof ServerLevel serverWorld) {
            if (!serverWorld.getGameRules().getBoolean(FriendlyDamageCommand.ENABLE_FRIENDLY_PROJECTILE_DAMAGE)) {
                if (owner != null && target instanceof LivingEntity living) {
                    // 使用原版团队匹配逻辑（包含null安全处理）
                    if (!EntityUtil.isValidSummonCombatTarget(projectile, owner, living)) {
                        ci.cancel(); // 取消同队成员的伤害
                    }
                }
            }
        }
    }
    @Inject(
            method = "canHitEntity(Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("RETURN"),
            cancellable = true)
    private void modifyCanHit(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        AbstractArrow projectile = (AbstractArrow) (Object) this;
        Entity owner = projectile.getOwner();
        Level world = projectile.level();

        if (owner instanceof MilitiaArcherVillager && entity instanceof LivingEntity living && mobBattle$isMilitiaArrowPassThroughTarget(living)) {
            cir.setReturnValue(false);
            return;
        }
        if (owner != null && entity instanceof LivingEntity living && EntityUtil.shouldBlockOwnedSummonDamage(owner, living)) {
            cir.setReturnValue(false);
            return;
        }
        if (world instanceof ServerLevel serverWorld) {
            if (!serverWorld.getGameRules().getBoolean(FriendlyDamageCommand.ENABLE_FRIENDLY_PROJECTILE_DAMAGE)) {
                if (owner != null && entity instanceof LivingEntity living) {
                    if (!EntityUtil.isValidSummonCombatTarget(projectile, owner, living)) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void tickDog(CallbackInfo ci){
        if (!this.level().isClientSide && ((AbstractArrow) (Object) this instanceof ITrueDamageProjectile trueDamageProjectile && trueDamageProjectile.isMage()) && this.tickCount >= 30) {
            this.discard();
        }
    }
    @Inject(
            method = "onHitBlock",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onBlockHitDog(BlockHitResult blockHitResult, CallbackInfo ci) {
        if (blockHitResult.getType() == BlockHitResult.Type.BLOCK && ((AbstractArrow) (Object) this instanceof ITrueDamageProjectile trueDamageProjectile && trueDamageProjectile.isMage())) {
            this.discard();
            ci.cancel();
        }
    }

    private boolean mobBattle$isMilitiaArrowPassThroughTarget(LivingEntity target) {
        if (target instanceof AbstractGolem || target instanceof Villager) {
            return true;
        }
        Package pkg = target.getClass().getPackage();
        return pkg != null && pkg.getName().contains(".entity.villager.");
    }
}
