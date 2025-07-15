package com.kltyton.mob_battle.event;

import java.util.*;

public class TeamFightManager {
    private static final Set<TeamPair> activeFights = new HashSet<>();

    public static void addFight(String team1, String team2) {
        activeFights.add(new TeamPair(team1, team2));
    }

    public static Set<TeamPair> getActiveFights() {
        return Collections.unmodifiableSet(activeFights);
    }

    public static class TeamPair {
        private final String team1;
        private final String team2;

        public TeamPair(String t1, String t2) {
            // 确保队伍名称按顺序存储
            if (t1.compareTo(t2) < 0) {
                this.team1 = t1;
                this.team2 = t2;
            } else {
                this.team1 = t2;
                this.team2 = t1;
            }
        }

        public boolean contains(String team) {
            return team.equals(team1) || team.equals(team2);
        }

        public String getOther(String team) {
            if (team.equals(team1)) return team2;
            if (team.equals(team2)) return team1;
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TeamPair pair = (TeamPair) o;
            return team1.equals(pair.team1) && team2.equals(pair.team2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(team1, team2);
        }
    }
}
