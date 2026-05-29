package com.kltyton.mob_battle.command;

import com.kltyton.mob_battle.event.alliance.AllianceState;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class AllianceCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> mainNode = dispatcher.register(literal("TeamAlliance")
                .requires(source -> source.hasPermission(2))
                .then(literal("list")
                        .executes(ctx -> listAlliances(ctx.getSource())))

                .then(literal("create").then(argument("name", StringArgumentType.string())
                        .suggests(AllianceCommand::suggestAlliances)
                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "name");
                            AllianceState.get(ctx.getSource().getServer()).addTeamToAlliance(name, "");
                            ctx.getSource().sendSuccess(() -> Component.literal("§a成功创建同盟: " + name), true);
                            return 1;
                        })))
                .then(literal("delete").then(argument("name", StringArgumentType.string())
                        .suggests(AllianceCommand::suggestAlliances)
                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "name");
                            AllianceState.get(ctx.getSource().getServer()).deleteAlliance(name);
                            ctx.getSource().sendSuccess(() -> Component.literal("§c已删除同盟: " + name), true);
                            return 1;
                        })))
        );

        // 构建 Add 和 Remove 的循环分支
        setupLogicNode(mainNode, "add", true);
        setupLogicNode(mainNode, "remove", false);
    }

    private static void setupLogicNode(LiteralCommandNode<CommandSourceStack> parent, String label, boolean isAdd) {
        // 1. 定义 team 节点
        ArgumentCommandNode<CommandSourceStack, String> teamNode = argument("team", TeamArgument.team()).build();

        // 2. 定义 in 节点
        LiteralCommandNode<CommandSourceStack> inNode = literal("in").build();

        // 3. 定义 alliance 节点并设置执行器
        ArgumentCommandNode<CommandSourceStack, String> allianceNode = argument("alliance", StringArgumentType.string())
                .suggests(AllianceCommand::suggestAlliances)
                .executes(ctx -> executeDynamic(ctx, isAdd))
                .build();

        // 拼接结构: add -> team (循环) -> in -> alliance
        LiteralCommandNode<CommandSourceStack> actionNode = literal(label).build();
        parent.addChild(actionNode);
        actionNode.addChild(teamNode);

        // 关键：team 节点重定向回自己，实现无限输入团队
        teamNode.addChild(inNode);
        // 通过使用自定义的 redirect 逻辑或简单地在 DSL 中构建：
        // 在 Brigadier 中，手动链接节点实现循环：
        ArgumentCommandNode<CommandSourceStack, String> loopNode = argument("next_team", TeamArgument.team())
                .redirect(teamNode).build();
        teamNode.addChild(loopNode);

        inNode.addChild(allianceNode);
    }

    private static int executeDynamic(CommandContext<CommandSourceStack> ctx, boolean isAdd) {
        String input = ctx.getInput(); // 例如: /TeamAlliance add t1 t2 t3 in MyAlliance
        String allianceName = StringArgumentType.getString(ctx, "alliance");
        CommandSourceStack source = ctx.getSource();
        Scoreboard scoreboard = source.getServer().getScoreboard();
        AllianceState state = AllianceState.get(source.getServer());

        // 解析输入字符串获取团队列表
        // 逻辑：提取 "add " 或 "remove " 之后，到 " in " 之前的部分
        String action = isAdd ? "add " : "remove ";
        int start = input.indexOf(action) + action.length();
        int end = input.lastIndexOf(" in ");

        if (start < 0 || end < 0 || end <= start) {
            source.sendFailure(Component.literal("指令解析失败，请检查格式"));
            return 0;
        }

        String teamsPart = input.substring(start, end).trim();
        String[] teamNames = teamsPart.split("\\s+");
        List<String> processedTeams = new ArrayList<>();

        for (String name : teamNames) {
            PlayerTeam team = scoreboard.getPlayerTeam(name);
            if (team != null) {
                if (isAdd) state.addTeamToAlliance(allianceName, team.getName());
                else state.removeTeamFromAlliance(allianceName, team.getName());
                processedTeams.add(team.getName());
            }
        }

        String msg = "§7已将团队 §f";
        String actionMsg = isAdd ? " §7加入同盟 §6" : " §7移出同盟 §6";
        source.sendSuccess(() -> Component.literal(msg + String.join(", ", processedTeams) + actionMsg + allianceName), true);

        return processedTeams.size();
    }

    private static int listAlliances(CommandSourceStack source) {
        AllianceState state = AllianceState.get(source.getServer());
        var data = state.getAlliances();
        if (data.isEmpty()) {
            source.sendSuccess(() -> Component.literal("§e暂无同盟数据"), false);
            return 0;
        }
        data.forEach((name, teams) -> source.sendSuccess(() -> Component.literal("§6" + name + "§7: §f" + String.join(", ", teams)), false));
        return data.size();
    }
    private static CompletableFuture<Suggestions> suggestAlliances(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        AllianceState state = AllianceState.get(context.getSource().getServer());
        for (String allianceName : state.getAlliances().keySet()) {
            builder.suggest(allianceName);
        }
        return builder.buildFuture();
    }
}