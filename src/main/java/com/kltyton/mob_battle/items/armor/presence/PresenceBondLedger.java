package com.kltyton.mob_battle.items.armor.presence;

import com.kltyton.mob_battle.Mob_battle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** 世界级召唤契约：离线死亡、区块卸载和重启不能丢失最大生命预留或重复返还。 */
public final class PresenceBondLedger extends SavedData {
    public record Preference(boolean aggressive, int ammunition, long readyAt) {
        private static final Codec<Preference> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("aggressive", false).forGetter(Preference::aggressive),
                Codec.intRange(0, 5).optionalFieldOf("ammunition", 0).forGetter(Preference::ammunition),
                Codec.LONG.optionalFieldOf("ready_at", 0L).forGetter(Preference::readyAt)
        ).apply(instance, Preference::new));
    }

    private static final Codec<PresenceBondLedger> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, Codec.unboundedMap(Codec.STRING, Codec.INT))
                    .fieldOf("bonds").forGetter(data -> data.bonds),
            Codec.unboundedMap(Codec.STRING, Preference.CODEC)
                    .fieldOf("preferences").forGetter(data -> data.preferences)
    ).apply(instance, PresenceBondLedger::new));
    private static final SavedDataType<PresenceBondLedger> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "presence_bonds"),
            PresenceBondLedger::new, CODEC, null);
    private final Map<String, Map<String, Integer>> bonds = new HashMap<>();
    private final Map<String, Preference> preferences = new HashMap<>();

    private PresenceBondLedger() {}

    private PresenceBondLedger(Map<String, Map<String, Integer>> bonds, Map<String, Preference> preferences) {
        bonds.forEach((owner, entries) -> this.bonds.put(owner, new HashMap<>(entries)));
        this.preferences.putAll(preferences);
    }

    /** 所有维度共用主世界存储，确保换维度时仍能解除原召唤契约。 */
    public static PresenceBondLedger get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public Preference preference(UUID owner) {
        return preferences.getOrDefault(owner.toString(), new Preference(false, 0, 0));
    }

    public void preference(UUID owner, Preference preference) {
        preferences.put(owner.toString(), preference);
        setDirty();
    }

    public void bind(UUID owner, UUID summon, int health) {
        bonds.computeIfAbsent(owner.toString(), key -> new HashMap<>()).put(summon.toString(), health);
        setDirty();
    }

    public boolean contains(UUID owner, UUID summon) {
        Map<String, Integer> owned = bonds.get(owner.toString());
        return owned != null && owned.containsKey(summon.toString());
    }

    public int reservedHealth(UUID owner) {
        Map<String, Integer> owned = bonds.get(owner.toString());
        return owned == null ? 0 : owned.values().stream().mapToInt(Integer::intValue).sum();
    }

    /** 只由召唤物死亡或主人死亡、脱甲导致的契约终止调用。 */
    public void release(UUID owner, UUID summon) {
        Map<String, Integer> owned = bonds.get(owner.toString());
        if (owned != null && owned.remove(summon.toString()) != null) {
            if (owned.isEmpty()) bonds.remove(owner.toString());
            setDirty();
        }
    }

    /** 卸载中的方块在重新加载时会因契约已终止而死亡，不重新扣除生命。 */
    public void releaseAll(UUID owner) {
        if (bonds.remove(owner.toString()) != null) setDirty();
    }
}
