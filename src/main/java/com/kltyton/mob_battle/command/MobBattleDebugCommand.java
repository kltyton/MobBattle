package com.kltyton.mob_battle.command;

import com.kltyton.mob_battle.config.whitelist.MobBattlePermissions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import software.bernie.geckolib.animatable.GeoEntity;

import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class MobBattleDebugCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("mobBattle")
                .then(literal("debug")
                        .requires(MobBattleDebugCommand::canUseDebug)
                        .then(literal("entity")
                                .then(argument("targets", EntityArgument.entities())
                                        .then(argument("animation", StringArgumentType.word())
                                                .executes(context -> playAnimation(
                                                        context.getSource(),
                                                        EntityArgument.getEntities(context, "targets"),
                                                        StringArgumentType.getString(context, "animation")
                                                )))))));
    }

    private static boolean canUseDebug(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        return player != null && MobBattlePermissions.canUseProtectedContent(player);
    }

    private static int playAnimation(CommandSourceStack source, Collection<? extends Entity> targets, String animation) {
        int count = 0;
        for (Entity entity : targets) {
            if (entity instanceof GeoEntity geoEntity) {
                geoEntity.triggerAnim("skill_controller", animation);
                geoEntity.triggerAnim("main_controller", animation);
                count++;
            } else {
                source.sendFailure(Component.literal(entity.getDisplayName().getString() + " 不是 Geckolib 实体"));
            }
        }

        int finalCount = count;
        source.sendSuccess(() -> Component.literal("已尝试播放动画 " + animation + "，目标数: " + finalCount), false);
        return count;
    }
}
