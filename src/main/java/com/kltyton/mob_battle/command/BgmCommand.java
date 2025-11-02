package com.kltyton.mob_battle.command;

import com.kltyton.mob_battle.sounds.bgm.BgmZone;
import com.kltyton.mob_battle.sounds.bgm.ServerBgmManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

public class BgmCommand {
    private static final SuggestionProvider<ServerCommandSource> MY_BGM_NAMES =
            (ctx, builder) -> {
                // 1. 拿到所有已有分区名
                ServerBgmManager.getAllZones()
                        .stream()
                        .map(BgmZone::name)
                        .forEach(builder::suggest);
                return builder.buildFuture();
            };
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess) {

        /* ---------- 根节点 /bgm ---------- */
        dispatcher.register(
                CommandManager.literal("bgm")
                        .requires(src -> src.hasPermissionLevel(2))

                        /* ====== add ====== */
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("music", IdentifierArgumentType.identifier())
                                        .suggests(SuggestionProviders.cast(SuggestionProviders.AVAILABLE_SOUNDS))  // <-- 就是这一行
                                        .then(CommandManager.argument("x1", FloatArgumentType.floatArg())
                                                .then(CommandManager.argument("y1", FloatArgumentType.floatArg())
                                                        .then(CommandManager.argument("z1", FloatArgumentType.floatArg())
                                                                .then(CommandManager.argument("x2", FloatArgumentType.floatArg())
                                                                        .then(CommandManager.argument("y2", FloatArgumentType.floatArg())
                                                                                .then(CommandManager.argument("z2", FloatArgumentType.floatArg())
                                                                                        .then(CommandManager.argument("volume", FloatArgumentType.floatArg(0f, 10f))
                                                                                                .then(CommandManager.argument("name", StringArgumentType.word())
                                                                                                        .suggests(MY_BGM_NAMES)
                                                                                                        .executes(ctx -> {
                                                                                                            Identifier music = IdentifierArgumentType.getIdentifier(ctx, "music");
                                                                                                            float x1 = FloatArgumentType.getFloat(ctx, "x1");
                                                                                                            float y1 = FloatArgumentType.getFloat(ctx, "y1");
                                                                                                            float z1 = FloatArgumentType.getFloat(ctx, "z1");
                                                                                                            float x2 = FloatArgumentType.getFloat(ctx, "x2");
                                                                                                            float y2 = FloatArgumentType.getFloat(ctx, "y2");
                                                                                                            float z2 = FloatArgumentType.getFloat(ctx, "z2");
                                                                                                            float volume = FloatArgumentType.getFloat(ctx, "volume");
                                                                                                            String name = StringArgumentType.getString(ctx, "name");

                                                                                                            Box box = new Box(x1, y1, z1, x2, y2, z2);
                                                                                                            ServerBgmManager.addZone(new BgmZone(name, box, music, volume), ctx.getSource().getServer());
                                                                                                            ctx.getSource().sendFeedback(
                                                                                                                    () -> Text.literal("新增BGM分区: " + name), true);
                                                                                                            return 1;
                                                                                                        })))))))))))

                        /* ====== delete ====== */
                        .then(CommandManager.literal("delete")
                                .then(CommandManager.argument("name", StringArgumentType.word())
                                        .suggests(MY_BGM_NAMES)
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "name");
                                            if (ServerBgmManager.removeZone(name, ctx.getSource().getServer())) {
                                                ctx.getSource().sendFeedback(
                                                        () -> Text.literal("删除BGM分区: " + name), true);
                                                return 1;
                                            } else {
                                                ctx.getSource().sendError(
                                                        Text.literal("没有这样的BGM分区: " + name));
                                                return 0;
                                            }
                                        })))

                        /* ====== location ====== */
                        .then(CommandManager.literal("location")
                                .then(CommandManager.argument("name", StringArgumentType.word())
                                        .suggests(MY_BGM_NAMES)
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "name");
                                            BgmZone zone = ServerBgmManager.getZone(name);
                                            if (zone == null) {
                                                ctx.getSource().sendError(
                                                        Text.literal("没有这样的BGM分区: " + name));
                                                return 0;
                                            }
                                            ctx.getSource().sendFeedback(() -> Text.literal(zone.toString()), false);
                                            return 1;
                                        })))

                        /* ====== list ====== */
                        .then(CommandManager.literal("list")
                                .executes(ctx -> {
                                    var zones = ServerBgmManager.getAllZones();
                                    if (zones.isEmpty()) {
                                        ctx.getSource().sendFeedback(
                                                () -> Text.literal("未定义BGM区域."), false);
                                    } else {
                                        zones.forEach(z ->
                                                ctx.getSource().sendFeedback(() -> Text.literal(z.toString()), false));
                                    }
                                    return 1;
                                }))

                        /* ====== merge ====== */
                        .then(CommandManager.literal("merge")
                                .then(CommandManager.argument("name1", StringArgumentType.word())
                                        .suggests(MY_BGM_NAMES)
                                        .then(CommandManager.argument("name2", StringArgumentType.word())
                                                .suggests(MY_BGM_NAMES)
                                                .executes(ctx -> {
                                                    String name1 = StringArgumentType.getString(ctx, "name1");
                                                    String name2 = StringArgumentType.getString(ctx, "name2");
                                                    boolean ok = ServerBgmManager.mergeZones(name1, name2);
                                                    if (ok) {
                                                        ctx.getSource().sendFeedback(
                                                                () -> Text.literal("合并BGM分区: " + name2 + " 到 " + name1), true);
                                                        return 1;
                                                    } else {
                                                        ctx.getSource().sendError(
                                                                Text.literal("合并失败，请检查两个分区是否存在且 name2 未被占用"));
                                                        return 0;
                                                    }
                                                }))))
        );
    }
}