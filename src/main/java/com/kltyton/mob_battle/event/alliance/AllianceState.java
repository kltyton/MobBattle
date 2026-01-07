package com.kltyton.mob_battle.event.alliance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.*;

public class AllianceState extends PersistentState {
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
            this.markDirty();
        }
    }

    public void removeTeamFromAlliance(String allianceName, String teamName) {
        if (alliances.containsKey(allianceName)) {
            alliances.get(allianceName).remove(teamName);
            teamToAllianceCache.remove(teamName);
            this.markDirty();
        }
    }

    public void deleteAlliance(String name) {
        List<String> teams = alliances.remove(name);
        if (teams != null) {
            teams.forEach(teamToAllianceCache::remove);
            this.markDirty();
        }
    }

    public Map<String, List<String>> getAlliances() {
        return Collections.unmodifiableMap(alliances);
    }

    public static AllianceState get(MinecraftServer server) {
        return server.getWorld(World.OVERWORLD).getPersistentStateManager().getOrCreate(createStateType());
    }

    public static PersistentStateType<AllianceState> createStateType() {
        return new PersistentStateType<>("team_alliances", AllianceState::new, CODEC, DataFixTypes.LEVEL);
    }
}