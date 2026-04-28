package com.kltyton.mob_battle.mixin.net.minecraft.server.command;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.config.whitelist.MobBattlePermissions;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.GiveCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(GiveCommand.class)
public abstract class GiveCommandMixin {

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void mob_battle$blockGive(
            ServerCommandSource source,
            ItemStackArgument item,
            Collection<ServerPlayerEntity> targets,
            int count,
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

        Item mcItem = item.getItem();
        Identifier id = Registries.ITEM.getId(mcItem);
        if (Mob_battle.MOD_ID.equals(id.getNamespace())) {
            source.sendError(Text.literal("你没有权限使用 " + Mob_battle.MOD_ID + " 的物品。"));
            cir.setReturnValue(0);
        }
    }
}
