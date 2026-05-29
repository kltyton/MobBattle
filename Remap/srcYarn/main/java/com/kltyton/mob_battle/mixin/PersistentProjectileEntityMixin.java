package com.kltyton.mob_battle.mixin;

import com.kltyton.mob_battle.command.FriendlyDamageCommand;
import com.kltyton.mob_battle.entity.bullet.ITrueDamageProjectile;
import com.kltyton.mob_battle.entity.villager.militia.MilitiaArcherVillager;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends ProjectileEntity{

    @Shadow
    private double damage;

    public PersistentProjectileEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }
    @Redirect(
            method = "onEntityHit",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;sidedDamage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
    )
    private boolean cancelDamage(Entity instance, DamageSource source, float amount) {
        if ((PersistentProjectileEntity) (Object) this instanceof ITrueDamageProjectile trueDamageProjectile && trueDamageProjectile.isTrueDamage()) {
            trueDamageProjectile.additionalDamage(instance);
            return instance.sidedDamage(source, (float) this.damage);
        } else {
            return instance.sidedDamage(source, amount);
        }
    }
    @Redirect(
            method = "onEntityHit",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSources;arrow(Lnet/minecraft/entity/projectile/PersistentProjectileEntity;Lnet/minecraft/entity/Entity;)Lnet/minecraft/entity/damage/DamageSource;")
    )
    private DamageSource modifyDamageSource(DamageSources instance, PersistentProjectileEntity source, Entity attacker) {
        if ((PersistentProjectileEntity) (Object) this instanceof ITrueDamageProjectile trueDamageProjectile && trueDamageProjectile.isMage()) {
            return instance.indirectMagic(source, (attacker != null ? attacker : this));
        } else {
            return instance.arrow(source, (attacker != null ? attacker : this));
        }
    }
    @Inject(
            method = "onEntityHit",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onHitDog(EntityHitResult entityHitResult, CallbackInfo ci) {
        Entity target = entityHitResult.getEntity();
        PersistentProjectileEntity projectile = (PersistentProjectileEntity) (Object) this;
        Entity owner = projectile.getOwner();
        World world = projectile.getWorld();
        if (owner instanceof MilitiaArcherVillager && target instanceof LivingEntity living && mobBattle$isMilitiaArrowPassThroughTarget(living)) {
            ci.cancel();
            return;
        }
        if (owner != null && target instanceof LivingEntity living && EntityUtil.shouldBlockOwnedSummonDamage(owner, living)) {
            ci.cancel();
            return;
        }
        if (world instanceof ServerWorld serverWorld) {
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
            method = "canHit(Lnet/minecraft/entity/Entity;)Z",
            at = @At("RETURN"),
            cancellable = true)
    private void modifyCanHit(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        PersistentProjectileEntity projectile = (PersistentProjectileEntity) (Object) this;
        Entity owner = projectile.getOwner();
        World world = projectile.getWorld();

        if (owner instanceof MilitiaArcherVillager && entity instanceof LivingEntity living && mobBattle$isMilitiaArrowPassThroughTarget(living)) {
            cir.setReturnValue(false);
            return;
        }
        if (owner != null && entity instanceof LivingEntity living && EntityUtil.shouldBlockOwnedSummonDamage(owner, living)) {
            cir.setReturnValue(false);
            return;
        }
        if (world instanceof ServerWorld serverWorld) {
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
        if (!this.getWorld().isClient && ((PersistentProjectileEntity) (Object) this instanceof ITrueDamageProjectile trueDamageProjectile && trueDamageProjectile.isMage()) && this.age >= 30) {
            this.discard();
        }
    }
    @Inject(
            method = "onBlockHit",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onBlockHitDog(BlockHitResult blockHitResult, CallbackInfo ci) {
        if (blockHitResult.getType() == BlockHitResult.Type.BLOCK && ((PersistentProjectileEntity) (Object) this instanceof ITrueDamageProjectile trueDamageProjectile && trueDamageProjectile.isMage())) {
            this.discard();
            ci.cancel();
        }
    }

    private boolean mobBattle$isMilitiaArrowPassThroughTarget(LivingEntity target) {
        if (target instanceof GolemEntity || target instanceof VillagerEntity) {
            return true;
        }
        Package pkg = target.getClass().getPackage();
        return pkg != null && pkg.getName().contains(".entity.villager.");
    }
}
