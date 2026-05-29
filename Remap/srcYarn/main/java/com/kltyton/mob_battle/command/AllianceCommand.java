package com.kltyton.mob_battle.command;

import com.kltyton.mob_battle.event.alliance.AllianceState;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.argument.TeamArgumentType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class AllianceCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> mainNode = dispatcher.register(literal("TeamAlliance")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("list")
                        .executes(ctx -> listAlliances(ctx.getSource())))

                .then(literal("create").then(argument("name", StringArgumentType.string())
                        .suggests(AllianceCommand::suggestAlliances)
                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "name");
                            AllianceState.get(ctx.getSource().getServer()).addTeamToAlliance(name, "");
                            ctx.getSource().sendFeedback(() -> Text.literal("§a成功创建同盟: " + name), true);
                            return 1;
                        })))
                .then(literal("delete").then(argument("name", StringArgumentType.string())
                        .suggests(AllianceCommand::suggestAlliances)
                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "name");
                            AllianceState.get(ctx.getSource().getServer()).deleteAlliance(name);
                            ctx.getSource().sendFeedback(() -> Text.literal("§c已删除同盟: " + name), true);
                            return 1;
                        })))
        );

        // 构建 Add 和 Remove 的循环分支
        setupLogicNode(mainNode, "add", true);
        setupLogicNode(mainNode, "remove", false);
    }

    private static void setupLogicNode(LiteralCommandNode<ServerCommandSource> parent, String label, boolean isAdd) {
        // 1. 定义 team 节点
        ArgumentCommandNode<ServerCommandSource, String> teamNode = argument("team", TeamArgumentType.team()).build();

        // 2. 定义 in 节点
        LiteralCommandNode<ServerCommandSource> inNode = literal("in").build();

        // 3. 定义 alliance 节点并设置执行器
        ArgumentCommandNode<ServerCommandSource, String> allianceNode = argument("alliance", StringArgumentType.string())
                .suggests(AllianceCommand::suggestAlliances)
                .executes(ctx -> executeDynamic(ctx, isAdd))
                .build();

        // 拼接结构: add -> team (循环) -> in -> alliance
        LiteralCommandNode<ServerCommandSource> actionNode = literal(label).build();
        parent.addChild(actionNode);
        actionNode.addChild(teamNode);

        // 关键：team 节点重定向回自己，实现无限输入团队
        teamNode.addChild(inNode);
        // 通过使用自定义的 redirect 逻辑或简单地在 DSL 中构建：
        // 在 Brigadier 中，手动链接节点实现循环：
        ArgumentCommandNode<ServerCommandSource, String> loopNode = argument("next_team", TeamArgumentType.team())
                .redirect(teamNode).build();
        teamNode.addChild(loopNode);

        inNode.addChild(allianceNode);
    }

    private static int executeDynamic(CommandContext<ServerCommandSource> ctx, boolean isAdd) {
        String input = ctx.getInput(); // 例如: /TeamAlliance add t1 t2 t3 in MyAlliance
        String allianceName = StringArgumentType.getString(ctx, "alliance");
        ServerCommandSource source = ctx.getSource();
        Scoreboard scoreboard = source.getServer().getScoreboard();
        AllianceState state = AllianceState.get(source.getServer());

        // 解析输入字符串获取团队列表
        // 逻辑：提取 "add " 或 "remove " 之后，到 " in " 之前的部分
        String action = isAdd ? "add " : "remove ";
        int start = input.indexOf(action) + action.length();
        int end = input.lastIndexOf(" in ");

        if (start < 0 || end < 0 || end <= start) {
            source.sendError(Text.literal("指令解析失败，请检查格式"));
            return 0;
        }

        String teamsPart = input.substring(start, end).trim();
        String[] teamNames = teamsPart.split("\\s+");
        List<String> processedTeams = new ArrayList<>();

        for (String name : teamNames) {
            Team team = scoreboard.getTeam(name);
            if (team != null) {
                if (isAdd) state.addTeamToAlliance(allianceName, team.getName());
                else state.removeTeamFromAlliance(allianceName, team.getName());
                processedTeams.add(team.getName());
            }
        }

        String msg = "§7已将团队 §f";
        String actionMsg = isAdd ? " §7加入同盟 §6" : " §7移出同盟 §6";
        source.sendFeedback(() -> Text.literal(msg + String.join(", ", processedTeams) + actionMsg + allianceName), true);

        return processedTeams.size();
    }

    private static int listAlliances(ServerCommandSource source) {
        AllianceState state = AllianceState.get(source.getServer());
        var data = state.getAlliances();
        if (data.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§e暂无同盟数据"), false);
            return 0;
        }
        data.forEach((name, teams) -> source.sendFeedback(() -> Text.literal("§6" + name + "§7: §f" + String.join(", ", teams)), false));
        return data.size();
    }
    private static CompletableFuture<Suggestions> suggestAlliances(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        AllianceState state = AllianceState.get(context.getSource().getServer());
        for (String allianceName : state.getAlliances().keySet()) {
            builder.suggest(allianceName);
        }
        return builder.buildFuture();
    }
}