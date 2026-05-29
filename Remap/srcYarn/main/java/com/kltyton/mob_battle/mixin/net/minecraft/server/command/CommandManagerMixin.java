package com.kltyton.mob_battle.mixin.net.minecraft.server.command;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.config.whitelist.MobBattlePermissions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {

    @Inject(method = "executeWithPrefix", at = @At("HEAD"), cancellable = true)
    private void mob_battle$blockProtectedNamespace(ServerCommandSource source, String command, CallbackInfo ci) {
        ServerPlayerEntity player;
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

        String stripped = CommandManager.stripLeadingSlash(command);
        String lower = stripped.toLowerCase(Locale.ROOT);
        String namespace = (Mob_battle.MOD_ID + ":").toLowerCase(Locale.ROOT);

        if (lower.contains(namespace)) {
            source.sendError(Text.literal("你没有权限使用 " + Mob_battle.MOD_ID + " 的内容。"));
            ci.cancel();
        }
    }
}

