package com.kltyton.mob_battle.command;

import com.kltyton.mob_battle.config.whitelist.MobBattlePermissions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import software.bernie.geckolib.animatable.GeoEntity;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MobBattleDebugCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("mobBattle")
                .then(literal("debug")
                        .requires(MobBattleDebugCommand::canUseDebug)
                        .then(literal("entity")
                                .then(argument("targets", EntityArgumentType.entities())
                                        .then(argument("animation", StringArgumentType.word())
                                                .executes(context -> playAnimation(
                                                        context.getSource(),
                                                        EntityArgumentType.getEntities(context, "targets"),
                                                        StringArgumentType.getString(context, "animation")
                                                )))))));
    }

    private static boolean canUseDebug(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        return player != null && MobBattlePermissions.canUseProtectedContent(player);
    }

    private static int playAnimation(ServerCommandSource source, Collection<? extends Entity> targets, String animation) {
        int count = 0;
        for (Entity entity : targets) {
            if (entity instanceof GeoEntity geoEntity) {
                geoEntity.triggerAnim("skill_controller", animation);
                geoEntity.triggerAnim("main_controller", animation);
                count++;
            } else {
                source.sendError(Text.literal(entity.getDisplayName().getString() + " 不是 Geckolib 实体"));
            }
        }

        int finalCount = count;
        source.sendFeedback(() -> Text.literal("已尝试播放动画 " + animation + "，目标数: " + finalCount), false);
        return count;
    }
}
