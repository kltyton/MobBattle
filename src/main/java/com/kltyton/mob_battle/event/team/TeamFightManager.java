package com.kltyton.mob_battle.event.team;

import java.util.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.PlayerTeam;

public class TeamFightManager {
    private static final Map<PlayerTeam, Set<PlayerTeam>> FIGHTING_TEAMS = new HashMap<>();
    private static final Set<PlayerTeam> ACTIVE_TEAMS = new HashSet<>();
    private static final Set<PlayerTeam> MAD_TEAMS = new HashSet<>();

    public static void startTeamFight(PlayerTeam team1, PlayerTeam team2) {
        if (team1 == team2) {
            return;
        }

        stopTeamFight(team1);
        stopTeamFight(team2);
        FIGHTING_TEAMS.computeIfAbsent(team1, ignored -> new HashSet<>()).add(team2);
        FIGHTING_TEAMS.computeIfAbsent(team2, ignored -> new HashSet<>()).add(team1);
        ACTIVE_TEAMS.add(team1);
        ACTIVE_TEAMS.add(team2);
    }

    public static void startMadFight(Collection<PlayerTeam> teams) {
        List<PlayerTeam> uniqueTeams = teams.stream().filter(Objects::nonNull).distinct().toList();
        uniqueTeams.forEach(TeamFightManager::stopTeamFight);
        MAD_TEAMS.addAll(uniqueTeams);
        ACTIVE_TEAMS.addAll(uniqueTeams);
    }

    public static void stopTeamFight(PlayerTeam team) {
        Set<PlayerTeam> opponents = FIGHTING_TEAMS.remove(team);
        MAD_TEAMS.remove(team);
        ACTIVE_TEAMS.remove(team);
        if (opponents == null) {
            return;
        }
        for (PlayerTeam opponent : opponents) {
            Set<PlayerTeam> opponentFights = FIGHTING_TEAMS.get(opponent);
            if (opponentFights != null) {
                opponentFights.remove(team);
                if (opponentFights.isEmpty()) {
                    FIGHTING_TEAMS.remove(opponent);
                    ACTIVE_TEAMS.remove(opponent);
                }
            }
        }
    }

    public static boolean isInFight(PlayerTeam team) {
        return ACTIVE_TEAMS.contains(team);
    }

    public static boolean isMadFight(PlayerTeam team) {
        return MAD_TEAMS.contains(team);
    }

    public static PlayerTeam getOpponent(PlayerTeam team) {
        return getOpponents(team).stream().findFirst().orElse(null);
    }

    public static Set<PlayerTeam> getOpponents(PlayerTeam team) {
        Set<PlayerTeam> opponents = FIGHTING_TEAMS.get(team);
        return opponents == null ? Set.of() : Set.copyOf(opponents);
    }

    public static boolean areForcedOpponents(LivingEntity source, LivingEntity target) {
        if (source == null || target == null || source == target) {
            return false;
        }
        PlayerTeam sourceTeam = source.getTeam();
        PlayerTeam targetTeam = target.getTeam();
        if (sourceTeam == null) {
            return false;
        }
        if (isMadFight(sourceTeam)) {
            return targetTeam != sourceTeam;
        }
        return targetTeam != null && getOpponents(sourceTeam).contains(targetTeam);
    }

    public static int clearAllFights() {
        int count = FIGHTING_TEAMS.values().stream().mapToInt(Set::size).sum() / 2 + MAD_TEAMS.size();
        FIGHTING_TEAMS.clear();
        ACTIVE_TEAMS.clear();
        MAD_TEAMS.clear();
        return count;
    }

    public static String getActiveFights() {
        Set<String> fights = new TreeSet<>();
        MAD_TEAMS.stream()
                .map(team -> team.getName() + " (madfight)")
                .forEach(fights::add);
        for (Map.Entry<PlayerTeam, Set<PlayerTeam>> entry : FIGHTING_TEAMS.entrySet()) {
            for (PlayerTeam opponent : entry.getValue()) {
                String first = entry.getKey().getName();
                String second = opponent.getName();
                fights.add(first.compareTo(second) <= 0 ? first + " vs " + second : second + " vs " + first);
            }
        }
        return String.join("\n", fights);
    }
}

