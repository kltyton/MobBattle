package com.kltyton.mob_battle.command;

import com.kltyton.mob_battle.event.team.TeamFightManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class MadFightCommand {
    private MadFightCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("madfight")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(argument("teams", StringArgumentType.greedyString())
                        .suggests(MadFightCommand::suggestTeams)
                        .executes(MadFightCommand::execute)));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Scoreboard scoreboard = source.getServer().getScoreboard();
        String rawTeams = StringArgumentType.getString(context, "teams").trim();
        Set<String> teamNames = new LinkedHashSet<>(Arrays.asList(rawTeams.split("\\s+")));
        List<PlayerTeam> teams = new ArrayList<>();

        for (String teamName : teamNames) {
            PlayerTeam team = scoreboard.getPlayerTeam(teamName);
            if (team == null) {
                source.sendFailure(Component.literal("队伍不存在: " + teamName));
                return 0;
            }
            teams.add(team);
        }
        if (teams.size() < 2) {
            source.sendFailure(Component.literal("madfight 至少需要两个不同的队伍"));
            return 0;
        }

        TeamFightManager.startMadFight(teams);
        source.sendSuccess(() -> Component.literal("已启动多队混战: " + String.join(", ", teamNames)), false);
        return teams.size();
    }

    private static CompletableFuture<Suggestions> suggestTeams(
            CommandContext<CommandSourceStack> context,
            SuggestionsBuilder builder
    ) {
        String remaining = builder.getRemaining();
        int lastSpace = remaining.lastIndexOf(' ');
        String completed = lastSpace < 0 ? "" : remaining.substring(0, lastSpace).trim();
        Set<String> selected = completed.isEmpty()
                ? Set.of()
                : new LinkedHashSet<>(Arrays.asList(completed.split("\\s+")));
        SuggestionsBuilder currentTeamBuilder = lastSpace < 0
                ? builder
                : builder.createOffset(builder.getStart() + lastSpace + 1);
        return SharedSuggestionProvider.suggest(
                context.getSource().getServer().getScoreboard().getTeamNames().stream()
                        .filter(name -> !selected.contains(name)),
                currentTeamBuilder
        );
    }
}
