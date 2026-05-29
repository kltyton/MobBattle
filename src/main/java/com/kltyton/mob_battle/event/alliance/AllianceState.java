package com.kltyton.mob_battle.event.alliance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import java.util.*;

public class AllianceState extends SavedData {
    // 存储：同盟名 -> 团队名列表
    private final Map<String, List<String>> alliances;
    // 运行时缓存：团队名 -> 同盟名
    private final Map<String, String> teamToAllianceCache = new HashMap<>();

    public static final Codec<AllianceState> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf())
                            .fieldOf("alliances")
                            .forGetter(state -> state.alliances)
            ).apply(instance, AllianceState::new)
    );

    private AllianceState(Map<String, List<String>> alliances) {
        this.alliances = new HashMap<>(alliances);
        this.rebuildCache();
    }

    public AllianceState() {
        this.alliances = new HashMap<>();
    }

    private void rebuildCache() {
        teamToAllianceCache.clear();
        alliances.forEach((alliance, teams) ->
                teams.forEach(team -> teamToAllianceCache.put(team, alliance)));
    }
    public Map<String, String> getCache() {
        return teamToAllianceCache;
    }
    // --- 业务方法 ---
    public void addTeamToAlliance(String allianceName, String teamName) {
        alliances.computeIfAbsent(allianceName, k -> new ArrayList<>());
        if (!alliances.get(allianceName).contains(teamName)) {
            // 一个团队只能属于一个同盟，移除旧关系
            String old = teamToAllianceCache.get(teamName);
            if (old != null) alliances.get(old).remove(teamName);

            alliances.get(allianceName).add(teamName);
            teamToAllianceCache.put(teamName, allianceName);
            this.setDirty();
        }
    }

    public void removeTeamFromAlliance(String allianceName, String teamName) {
        if (alliances.containsKey(allianceName)) {
            alliances.get(allianceName).remove(teamName);
            teamToAllianceCache.remove(teamName);
            this.setDirty();
        }
    }

    public void deleteAlliance(String name) {
        List<String> teams = alliances.remove(name);
        if (teams != null) {
            teams.forEach(teamToAllianceCache::remove);
            this.setDirty();
        }
    }

    public Map<String, List<String>> getAlliances() {
        return Collections.unmodifiableMap(alliances);
    }

    public static AllianceState get(MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(createStateType());
    }

    public static SavedDataType<AllianceState> createStateType() {
        return new SavedDataType<>("team_alliances", AllianceState::new, CODEC, DataFixTypes.LEVEL);
    }
}