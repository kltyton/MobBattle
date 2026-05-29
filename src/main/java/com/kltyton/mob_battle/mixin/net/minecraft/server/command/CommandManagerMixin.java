package com.kltyton.mob_battle.mixin.net.minecraft.server.command;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.config.whitelist.MobBattlePermissions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

@Mixin(Commands.class)
public abstract class CommandManagerMixin {

    @Inject(method = "performPrefixedCommand", at = @At("HEAD"), cancellable = true)
    private void mob_battle$blockProtectedNamespace(CommandSourceStack source, String command, CallbackInfo ci) {
        ServerPlayer player;
        try {
            player = source.getPlayer();
        } catch (Exception e) {
            return;
        }

        if (player == null) {
            return;
        }

        if (MobBattlePermissions.canUseProtectedContent(player)) {
            return;
        }

        String stripped = Commands.trimOptionalPrefix(command);
        String lower = stripped.toLowerCase(Locale.ROOT);
        String namespace = (Mob_battle.MOD_ID + ":").toLowerCase(Locale.ROOT);

        if (lower.contains(namespace)) {
            source.sendFailure(Component.literal("你没有权限使用 " + Mob_battle.MOD_ID + " 的内容。"));
            ci.cancel();
        }
    }
}

