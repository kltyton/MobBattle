package com.kltyton.mob_battle.mixin;

import com.kltyton.mob_battle.command.FriendlyProjectileDamageCommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PersistentProjectileEntity.class)
public abstract class ArrowEntityMixin extends ProjectileEntity{

    public ArrowEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
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
        if (world instanceof ServerWorld serverWorld) {
            if (!serverWorld.getGameRules().getBoolean(FriendlyProjectileDamageCommand.ENABLE_FRIENDLY_PROJECTILE_DAMAGE)) {
                if (owner != null && target != null) {
                    Team ownerTeam = owner.getScoreboardTeam();
                    Team targetTeam = target.getScoreboardTeam();

                    // 使用原版团队匹配逻辑（包含null安全处理）
                    if (ownerTeam != null && targetTeam != null && Objects.equals(ownerTeam, targetTeam)) {
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

        if (world instanceof ServerWorld serverWorld) {
            if (!serverWorld.getGameRules().getBoolean(FriendlyProjectileDamageCommand.ENABLE_FRIENDLY_PROJECTILE_DAMAGE)) {
                if (owner != null && entity != null) {
                    Team ownerTeam = owner.getScoreboardTeam();
                    Team targetTeam = entity.getScoreboardTeam();

                    if (ownerTeam != null && targetTeam != null && Objects.equals(ownerTeam, targetTeam)) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }
}
