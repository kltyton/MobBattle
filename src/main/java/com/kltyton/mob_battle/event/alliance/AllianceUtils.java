package com.kltyton.mob_battle.event.alliance;

import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.MinecraftServer;

public class AllianceUtils {
    public static boolean isSameAlliance(Entity entity1, Entity entity2) {
        if (entity1 == null || entity2 == null) return false;

        MinecraftServer server = entity1.getServer();
        if (server == null) return false;

        AbstractTeam team1 = entity1.getScoreboardTeam();
        AbstractTeam team2 = entity2.getScoreboardTeam();

        if (team1 == null || team2 == null) return false;

        if (team1.isEqual(team2)) return true;

        // 从持久化数据中查询同盟关系
        AllianceState state = AllianceState.get(server);
        String alliance1 = state.getCache().get(team1.getName());
        String alliance2 = state.getCache().get(team2.getName());

        // 如果两人都有同盟，且同盟名称一致，则返回 true
        return alliance1 != null && alliance1.equals(alliance2);
    }
}
