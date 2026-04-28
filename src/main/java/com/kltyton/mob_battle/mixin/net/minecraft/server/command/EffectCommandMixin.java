package com.kltyton.mob_battle.mixin.net.minecraft.server.command;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.config.whitelist.MobBattlePermissions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.EffectCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(EffectCommand.class)
public abstract class EffectCommandMixin {

    @Inject(method = "executeGive", at = @At("HEAD"), cancellable = true)
    private static void mob_battle$blockEffectGive(
            ServerCommandSource source,
            Collection<? extends Entity> targets,
            RegistryEntry<StatusEffect> statusEffect,
            Integer seconds,
            int amplifier,
            boolean showParticles,
            CallbackInfoReturnable<Integer> cir
    ) {
        ServerPlayerEntity player;
        try {
            player = source.getPlayer();
        } catch (Exception e) {
            return;
        }

        if (player == null || MobBattlePermissions.canUseProtectedContent(player)) {
            return;
        }

        Identifier id = Registries.STATUS_EFFECT.getId(statusEffect.value());
        if (id != null && Mob_battle.MOD_ID.equals(id.getNamespace())) {
            source.sendError(Text.literal("你没有权限使用 " + Mob_battle.MOD_ID + " 的效果。"));
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "executeClear(Lnet/minecraft/server/command/ServerCommandSource;Ljava/util/Collection;Lnet/minecraft/registry/entry/RegistryEntry;)I", at = @At("HEAD"), cancellable = true)
    private static void mob_battle$blockEffectClearSpecific(
            ServerCommandSource source,
            Collection<? extends Entity> targets,
            RegistryEntry<StatusEffect> statusEffect,
            CallbackInfoReturnable<Integer> cir
    ) {
        ServerPlayerEntity player;
        try {
            player = source.getPlayer();
        } catch (Exception e) {
            return;
        }

        if (player == null || MobBattlePermissions.canUseProtectedContent(player)) {
            return;
        }

        Identifier id = Registries.STATUS_EFFECT.getId(statusEffect.value());
        if (id != null && Mob_battle.MOD_ID.equals(id.getNamespace())) {
            source.sendError(Text.literal("你没有权限使用 " + Mob_battle.MOD_ID + " 的效果。"));
            cir.setReturnValue(0);
        }
    }
}
