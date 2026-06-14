package com.kltyton.mob_battle.mixin.combatlog;

import com.kltyton.mob_battle.command.CombatLogSystem;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayDeque;
import java.util.Deque;

@Mixin(LivingEntity.class)
public abstract class LivingEntityCombatLogMixin {
    @Unique
    private final Deque<Float> mobBattle$damageHealthBefore = new ArrayDeque<>();
    @Unique
    private final Deque<Float> mobBattle$healHealthBefore = new ArrayDeque<>();
    @Unique
    private boolean mobBattle$deathLogged;

    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void mobBattle$captureHealthBeforeDamage(ServerLevel level, DamageSource source, float amount,
                                                     CallbackInfoReturnable<Boolean> cir) {
        this.mobBattle$damageHealthBefore.push(mobBattle$self().getHealth());
    }

    @Inject(method = "hurtServer", at = @At("RETURN"))
    private void mobBattle$logDamage(ServerLevel level, DamageSource source, float amount,
                                     CallbackInfoReturnable<Boolean> cir) {
        float healthBefore = this.mobBattle$damageHealthBefore.isEmpty()
                ? mobBattle$self().getHealth()
                : this.mobBattle$damageHealthBefore.pop();
        CombatLogSystem.logDamage(mobBattle$self(), source, amount, healthBefore, mobBattle$self().getHealth(), cir.getReturnValue());
    }

    @Inject(method = "heal", at = @At("HEAD"))
    private void mobBattle$captureHealthBeforeHeal(float amount, CallbackInfo ci) {
        this.mobBattle$healHealthBefore.push(mobBattle$self().getHealth());
    }

    @Inject(method = "heal", at = @At("RETURN"))
    private void mobBattle$logHeal(float amount, CallbackInfo ci) {
        float healthBefore = this.mobBattle$healHealthBefore.isEmpty()
                ? mobBattle$self().getHealth()
                : this.mobBattle$healHealthBefore.pop();
        CombatLogSystem.logHeal(mobBattle$self(), amount, healthBefore, mobBattle$self().getHealth());
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void mobBattle$logDeath(DamageSource source, CallbackInfo ci) {
        if (!this.mobBattle$deathLogged) {
            this.mobBattle$deathLogged = true;
            CombatLogSystem.logDeath(mobBattle$self(), source);
        }
    }

    @Inject(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("RETURN")
    )
    private void mobBattle$logEffectAdded(MobEffectInstance effect, Entity source,
                                          CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            CombatLogSystem.logEffectAdded(mobBattle$self(), effect, source);
        }
    }

    @Inject(method = "removeEffect", at = @At("RETURN"))
    private void mobBattle$logEffectRemoved(Holder<MobEffect> effect, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            CombatLogSystem.logEffectRemoved(mobBattle$self(), effect.value().getDisplayName().getString());
        }
    }

    @Unique
    private LivingEntity mobBattle$self() {
        return (LivingEntity) (Object) this;
    }
}
