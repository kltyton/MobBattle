package com.kltyton.mob_battle.command;

import com.kltyton.mob_battle.event.team.TeamFightManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.PlayerTeam;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TeamFightCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("teamFight")
                .then(argument("team1", TeamArgument.team())
                        .then(argument("team2", TeamArgument.team())
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    String team1Name = StringArgumentType.getString(context, "team1");
                                    String team2Name = StringArgumentType.getString(context, "team2");

                                    PlayerTeam team1 = source.getServer().getScoreboard().getPlayerTeam(team1Name);
                                    PlayerTeam team2 = source.getServer().getScoreboard().getPlayerTeam(team2Name);

                                    if (team1 == null || team2 == null) {
                                        source.sendFailure(Component.literal("一个或多个队伍不存在"));
                                        return 0;
                                    }

                                    TeamFightManager.startTeamFight(team1, team2);
                                    source.sendSuccess(() -> Component.literal("已启动队伍对战: " + team1Name + " vs " + team2Name), false);
                                    return 1;
                                }))));
        // 新增停止指令
        dispatcher.register(literal("stopTeamFight")
                .then(argument("team", TeamArgument.team())
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            String teamName = StringArgumentType.getString(context, "team");
                            PlayerTeam team = source.getServer().getScoreboard().getPlayerTeam(teamName);

                            if (team == null) {
                                source.sendFailure(Component.literal("队伍不存在"));
                                return 0;
                            }

                            if (!TeamFightManager.isInFight(team)) {
                                source.sendFailure(Component.literal("该队伍未处于战斗中"));
                                return 0;
                            }

                            TeamFightManager.stopTeamFight(team);
                            source.sendSuccess(() -> Component.literal("已停止队伍战斗: " + teamName), false);
                            return 1;
                        })));

        // 停止所有战斗
        dispatcher.register(literal("stopAllTeamFights")
                .executes(context -> {
                    int count = TeamFightManager.clearAllFights();
                    context.getSource().sendSuccess(() ->
                                    Component.literal("已停止所有队伍战斗，共清除" + count + "组对战"),
                            false
                    );
                    return 1;
                }));
        // 查询指令
        dispatcher.register(literal("listTeamFights")
                .executes(context -> {
                    String fights = TeamFightManager.getActiveFights();
                    context.getSource().sendSuccess(() ->
                                    Component.literal(fights.isEmpty() ? "当前没有进行中的战斗" : "当前对战：\n" + fights),
                            false
                    );
                    return 1;
                }));

    }
}
