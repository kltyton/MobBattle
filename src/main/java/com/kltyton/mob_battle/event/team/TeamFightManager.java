package com.kltyton.mob_battle.event.team;

import java.util.*;
import net.minecraft.scoreboard.Team;

public class TeamFightManager {
    private static final Map<Team, Team> FIGHTING_TEAMS = new HashMap<>();
    private static final Set<Team> ACTIVE_TEAMS = new HashSet<>();

    public static void startTeamFight(Team team1, Team team2) {
        FIGHTING_TEAMS.put(team1, team2);
        FIGHTING_TEAMS.put(team2, team1);
        ACTIVE_TEAMS.add(team1);
        ACTIVE_TEAMS.add(team2);
    }

    public static void stopTeamFight(Team team) {
        Team opponent = FIGHTING_TEAMS.remove(team);
        if (opponent != null) {
            FIGHTING_TEAMS.remove(opponent);
            ACTIVE_TEAMS.remove(team);
            ACTIVE_TEAMS.remove(opponent);
        }
    }

    public static boolean isInFight(Team team) {
        return ACTIVE_TEAMS.contains(team);
    }

    public static Team getOpponent(Team team) {
        return FIGHTING_TEAMS.get(team);
    }
    public static int clearAllFights() {
        int count = FIGHTING_TEAMS.size() / 2; // 因为每个对战存了两次
        FIGHTING_TEAMS.clear();
        ACTIVE_TEAMS.clear();
        return count;
    }

    // 新增调试方法（可选）
    public static String getActiveFights() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Team, Team> entry : FIGHTING_TEAMS.entrySet()) {
            sb.append(entry.getKey().getName())
                    .append(" vs ")
                    .append(entry.getValue().getName())
                    .append("\n");
        }
        return sb.toString().trim();
    }
}

