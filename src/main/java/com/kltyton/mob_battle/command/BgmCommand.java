package com.kltyton.mob_battle.command;

import com.kltyton.mob_battle.sounds.bgm.BgmZone;
import com.kltyton.mob_battle.sounds.bgm.ServerBgmManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

public class BgmCommand {
    private static final SuggestionProvider<CommandSourceStack> MY_BGM_NAMES =
            (ctx, builder) -> {
                // 1. 拿到所有已有分区名
                ServerBgmManager.getAllZones()
                        .stream()
                        .map(BgmZone::name)
                        .forEach(builder::suggest);
                return builder.buildFuture();
            };
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext registryAccess) {

        /* ---------- 根节点 /bgm ---------- */
        dispatcher.register(
                Commands.literal("bgm")
                        .requires(src -> src.hasPermission(2))

                        /* ====== add ====== */
                        .then(Commands.literal("add")
                                .then(Commands.argument("music", ResourceLocationArgument.id())
                                        .suggests(SuggestionProviders.cast(SuggestionProviders.AVAILABLE_SOUNDS))  // <-- 就是这一行
                                        .then(Commands.argument("x1", FloatArgumentType.floatArg())
                                                .then(Commands.argument("y1", FloatArgumentType.floatArg())
                                                        .then(Commands.argument("z1", FloatArgumentType.floatArg())
                                                                .then(Commands.argument("x2", FloatArgumentType.floatArg())
                                                                        .then(Commands.argument("y2", FloatArgumentType.floatArg())
                                                                                .then(Commands.argument("z2", FloatArgumentType.floatArg())
                                                                                        .then(Commands.argument("volume", FloatArgumentType.floatArg(0f, 10f))
                                                                                                .then(Commands.argument("name", StringArgumentType.word())
                                                                                                        .suggests(MY_BGM_NAMES)
                                                                                                        .executes(ctx -> {
                                                                                                            ResourceLocation music = ResourceLocationArgument.getId(ctx, "music");
                                                                                                            float x1 = FloatArgumentType.getFloat(ctx, "x1");
                                                                                                            float y1 = FloatArgumentType.getFloat(ctx, "y1");
                                                                                                            float z1 = FloatArgumentType.getFloat(ctx, "z1");
                                                                                                            float x2 = FloatArgumentType.getFloat(ctx, "x2");
                                                                                                            float y2 = FloatArgumentType.getFloat(ctx, "y2");
                                                                                                            float z2 = FloatArgumentType.getFloat(ctx, "z2");
                                                                                                            float volume = FloatArgumentType.getFloat(ctx, "volume");
                                                                                                            String name = StringArgumentType.getString(ctx, "name");

                                                                                                            AABB box = new AABB(x1, y1, z1, x2, y2, z2);
                                                                                                            ServerBgmManager.addZone(new BgmZone(name, box, music, volume), ctx.getSource().getServer());
                                                                                                            ctx.getSource().sendSuccess(
                                                                                                                    () -> Component.literal("新增BGM分区: " + name), true);
                                                                                                            return 1;
                                                                                                        })))))))))))

                        /* ====== delete ====== */
                        .then(Commands.literal("delete")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .suggests(MY_BGM_NAMES)
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "name");
                                            if (ServerBgmManager.removeZone(name, ctx.getSource().getServer())) {
                                                ctx.getSource().sendSuccess(
                                                        () -> Component.literal("删除BGM分区: " + name), true);
                                                return 1;
                                            } else {
                                                ctx.getSource().sendFailure(
                                                        Component.literal("没有这样的BGM分区: " + name));
                                                return 0;
                                            }
                                        })))

                        /* ====== location ====== */
                        .then(Commands.literal("location")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .suggests(MY_BGM_NAMES)
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "name");
                                            BgmZone zone = ServerBgmManager.getZone(name);
                                            if (zone == null) {
                                                ctx.getSource().sendFailure(
                                                        Component.literal("没有这样的BGM分区: " + name));
                                                return 0;
                                            }
                                            ctx.getSource().sendSuccess(() -> Component.literal(zone.toString()), false);
                                            return 1;
                                        })))

                        /* ====== list ====== */
                        .then(Commands.literal("list")
                                .executes(ctx -> {
                                    var zones = ServerBgmManager.getAllZones();
                                    if (zones.isEmpty()) {
                                        ctx.getSource().sendSuccess(
                                                () -> Component.literal("未定义BGM区域."), false);
                                    } else {
                                        zones.forEach(z ->
                                                ctx.getSource().sendSuccess(() -> Component.literal(z.toString()), false));
                                    }
                                    return 1;
                                }))

                        /* ====== merge ====== */
                        .then(Commands.literal("merge")
                                .then(Commands.argument("name1", StringArgumentType.word())
                                        .suggests(MY_BGM_NAMES)
                                        .then(Commands.argument("name2", StringArgumentType.word())
                                                .suggests(MY_BGM_NAMES)
                                                .executes(ctx -> {
                                                    String name1 = StringArgumentType.getString(ctx, "name1");
                                                    String name2 = StringArgumentType.getString(ctx, "name2");
                                                    boolean ok = ServerBgmManager.mergeZones(name1, name2);
                                                    if (ok) {
                                                        ctx.getSource().sendSuccess(
                                                                () -> Component.literal("合并BGM分区: " + name2 + " 到 " + name1), true);
                                                        return 1;
                                                    } else {
                                                        ctx.getSource().sendFailure(
                                                                Component.literal("合并失败，请检查两个分区是否存在且 name2 未被占用"));
                                                        return 0;
                                                    }
                                                }))))
        );
    }
}