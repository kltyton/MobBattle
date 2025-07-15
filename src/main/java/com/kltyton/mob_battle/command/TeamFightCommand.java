package com.kltyton.mob_battle.command;

import com.kltyton.mob_battle.event.team.TeamFightManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.*;
import com.mojang.brigadier.CommandDispatcher;

public class TeamFightCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("teamFight")
                .then(argument("team1", StringArgumentType.string())
                        .then(argument("team2", StringArgumentType.string())
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    String team1Name = StringArgumentType.getString(context, "team1");
                                    String team2Name = StringArgumentType.getString(context, "team2");

                                    Team team1 = source.getServer().getScoreboard().getTeam(team1Name);
                                    Team team2 = source.getServer().getScoreboard().getTeam(team2Name);

                                    if (team1 == null || team2 == null) {
                                        source.sendError(Text.literal("一个或多个队伍不存在"));
                                        return 0;
                                    }

                                    TeamFightManager.startTeamFight(team1, team2);
                                    source.sendFeedback(() -> Text.literal("已启动队伍对战: " + team1Name + " vs " + team2Name), false);
                                    return 1;
                                }))));
        // 新增停止指令
        dispatcher.register(literal("stopTeamFight")
                .then(argument("team", StringArgumentType.string())
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            String teamName = StringArgumentType.getString(context, "team");
                            Team team = source.getServer().getScoreboard().getTeam(teamName);

                            if (team == null) {
                                source.sendError(Text.literal("队伍不存在"));
                                return 0;
                            }

                            if (!TeamFightManager.isInFight(team)) {
                                source.sendError(Text.literal("该队伍未处于战斗中"));
                                return 0;
                            }

                            TeamFightManager.stopTeamFight(team);
                            source.sendFeedback(() -> Text.literal("已停止队伍战斗: " + teamName), false);
                            return 1;
                        })));

        // 停止所有战斗
        dispatcher.register(literal("stopAllTeamFights")
                .executes(context -> {
                    int count = TeamFightManager.clearAllFights();
                    context.getSource().sendFeedback(() ->
                                    Text.literal("已停止所有队伍战斗，共清除" + count + "组对战"),
                            false
                    );
                    return 1;
                }));
        // 查询指令
        dispatcher.register(literal("listTeamFights")
                .executes(context -> {
                    String fights = TeamFightManager.getActiveFights();
                    context.getSource().sendFeedback(() ->
                                    Text.literal(fights.isEmpty() ? "当前没有进行中的战斗" : "当前对战：\n" + fights),
                            false
                    );
                    return 1;
                }));

    }
}
