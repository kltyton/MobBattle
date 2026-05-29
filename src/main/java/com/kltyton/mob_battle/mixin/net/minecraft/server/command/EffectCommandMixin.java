package com.kltyton.mob_battle.mixin.net.minecraft.server.command;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.config.whitelist.MobBattlePermissions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;

@Mixin(EffectCommands.class)
public abstract class EffectCommandMixin {

    @Inject(method = "giveEffect", at = @At("HEAD"), cancellable = true)
    private static void mob_battle$blockEffectGive(
            CommandSourceStack source,
            Collection<? extends Entity> targets,
            Holder<MobEffect> statusEffect,
            Integer seconds,
            int amplifier,
            boolean showParticles,
            CallbackInfoReturnable<Integer> cir
    ) {
        ServerPlayer player;
        try {
            player = source.getPlayer();
        } catch (Exception e) {
            return;
        }

        if (player == null || MobBattlePermissions.canUseProtectedContent(player)) {
            return;
        }

        ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(statusEffect.value());
        if (id != null && Mob_battle.MOD_ID.equals(id.getNamespace())) {
            source.sendFailure(Component.literal("你没有权限使用 " + Mob_battle.MOD_ID + " 的效果。"));
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "clearEffect(Lnet/minecraft/commands/CommandSourceStack;Ljava/util/Collection;Lnet/minecraft/core/Holder;)I", at = @At("HEAD"), cancellable = true)
    private static void mob_battle$blockEffectClearSpecific(
            CommandSourceStack source,
            Collection<? extends Entity> targets,
            Holder<MobEffect> statusEffect,
            CallbackInfoReturnable<Integer> cir
    ) {
        ServerPlayer player;
        try {
            player = source.getPlayer();
        } catch (Exception e) {
            return;
        }

        if (player == null || MobBattlePermissions.canUseProtectedContent(player)) {
            return;
        }

        ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(statusEffect.value());
        if (id != null && Mob_battle.MOD_ID.equals(id.getNamespace())) {
            source.sendFailure(Component.literal("你没有权限使用 " + Mob_battle.MOD_ID + " 的效果。"));
            cir.setReturnValue(0);
        }
    }
}
