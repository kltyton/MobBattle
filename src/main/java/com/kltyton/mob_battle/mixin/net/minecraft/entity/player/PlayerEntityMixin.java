package com.kltyton.mob_battle.mixin.net.minecraft.entity.player;

import com.kltyton.mob_battle.items.ModFabricItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerEntityMixin {

    /**
     * 成功暴击后执行。
     * 这个位置已经满足：
     * 1. bl3 == true，也就是暴击条件成立
     * 2. target.sidedDamage(...) == true，也就是攻击成功造成了伤害
     */
    @Inject(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;crit(Lnet/minecraft/world/entity/Entity;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void afterSuccessfulCriticalHit(Entity target, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof ModFabricItem modfabricItem) {
            modfabricItem.onSuccessfulCriticalHit(player, target, stack);
        }
    }

    @Inject(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;sweepAttack()V",
                    shift = At.Shift.AFTER
            )
    )
    private void afterSuccessfulSweepMainTarget(Entity target, CallbackInfo ci) {
        notifySuccessfulSweepHit(target);
    }

    /**
     * 成功横扫到某个实体后执行。
     * 注意：这里不是主目标，而是被横扫波及的周围实体。
     */
    @WrapOperation(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean afterSuccessfulSweepHit(
            LivingEntity sweptEntity,
            ServerLevel world,
            DamageSource damageSource,
            float amount,
            Operation<Boolean> original
    ) {
        boolean damaged = original.call(sweptEntity, world, damageSource, amount);

        if (damaged) {
            notifySuccessfulSweepHit(sweptEntity);
        }

        return damaged;
    }

    @Unique
    private void notifySuccessfulSweepHit(Entity target) {
        Player player = (Player) (Object) this;
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof ModFabricItem modfabricItem) {
            modfabricItem.onSuccessfulSweepHit(player, target, stack);
        }
    }
}
