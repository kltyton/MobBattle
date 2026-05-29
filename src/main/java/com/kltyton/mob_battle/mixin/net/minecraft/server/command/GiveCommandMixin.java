package com.kltyton.mob_battle.mixin.net.minecraft.server.command;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.config.whitelist.MobBattlePermissions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

@Mixin(GiveCommand.class)
public abstract class GiveCommandMixin {

    @Inject(method = "giveItem", at = @At("HEAD"), cancellable = true)
    private static void mob_battle$blockGive(
            CommandSourceStack source,
            ItemInput item,
            Collection<ServerPlayer> targets,
            int count,
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

        Item mcItem = item.getItem();
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(mcItem);
        if (Mob_battle.MOD_ID.equals(id.getNamespace())) {
            source.sendFailure(Component.literal("你没有权限使用 " + Mob_battle.MOD_ID + " 的物品。"));
            cir.setReturnValue(0);
        }
    }
}
