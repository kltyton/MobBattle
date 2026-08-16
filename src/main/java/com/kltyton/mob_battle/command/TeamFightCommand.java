package com.kltyton.mob_battle.command;

import com.kltyton.mob_battle.event.team.TeamFightManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.scores.PlayerTeam;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TeamFightCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        registerTeamFight(dispatcher, "teamfight");
        registerTeamFight(dispatcher, "teamFight");
        registerStopTeamFight(dispatcher, "stopteamfight");
        registerStopTeamFight(dispatcher, "stopTeamFight");
        registerStopAllTeamFights(dispatcher, "stopallteamfights");
        registerStopAllTeamFights(dispatcher, "stopAllTeamFights");
        registerListTeamFights(dispatcher, "listteamfights");
        registerListTeamFights(dispatcher, "listTeamFights");
    }

    private static void registerTeamFight(CommandDispatcher<CommandSourceStack> dispatcher, String command) {
        dispatcher.register(literal(command)
                .requires(TeamFightCommand::isGameMaster)
                .then(argument("team1", TeamArgument.team())
                        .then(argument("team2", TeamArgument.team())
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    PlayerTeam team1 = TeamArgument.getTeam(context, "team1");
                                    PlayerTeam team2 = TeamArgument.getTeam(context, "team2");
                                    if (team1 == team2) {
                                        source.sendFailure(Component.literal("teamfight 需要两个不同的队伍"));
                                        return 0;
                                    }

                                    TeamFightManager.startTeamFight(team1, team2);
                                    source.sendSuccess(() -> Component.literal(
                                            "已启动队伍对战: " + team1.getName() + " vs " + team2.getName()), false);
                                    return 1;
                                }))));
    }

    private static void registerStopTeamFight(CommandDispatcher<CommandSourceStack> dispatcher, String command) {
        dispatcher.register(literal(command)
                .requires(TeamFightCommand::isGameMaster)
                .then(argument("team", TeamArgument.team())
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            PlayerTeam team = TeamArgument.getTeam(context, "team");

                            if (!TeamFightManager.isInFight(team)) {
                                source.sendFailure(Component.literal("该队伍未处于战斗中"));
                                return 0;
                            }

                            TeamFightManager.stopTeamFight(team);
                            source.sendSuccess(() -> Component.literal("已停止队伍战斗: " + team.getName()), false);
                            return 1;
                        })));
    }

    private static void registerStopAllTeamFights(CommandDispatcher<CommandSourceStack> dispatcher, String command) {
        dispatcher.register(literal(command)
                .requires(TeamFightCommand::isGameMaster)
                .executes(context -> {
                    int count = TeamFightManager.clearAllFights();
                    context.getSource().sendSuccess(() ->
                                    Component.literal("已停止所有队伍战斗，共清除" + count + "组对战"),
                            false
                    );
                    return 1;
                }));
    }

    private static void registerListTeamFights(CommandDispatcher<CommandSourceStack> dispatcher, String command) {
        dispatcher.register(literal(command)
                .requires(TeamFightCommand::isGameMaster)
                .executes(context -> {
                    String fights = TeamFightManager.getActiveFights();
                    context.getSource().sendSuccess(() ->
                                    Component.literal(fights.isEmpty() ? "当前没有进行中的战斗" : "当前对战：\n" + fights),
                            false
                    );
                    return 1;
                }));
    }

    private static boolean isGameMaster(CommandSourceStack source) {
        return source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }
}
