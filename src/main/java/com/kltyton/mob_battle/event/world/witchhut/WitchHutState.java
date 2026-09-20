package com.kltyton.mob_battle.event.world.witchhut;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * 记录每座原版 Witch Hut 的活动女巫和重生冷却。
 *
 * <p>状态放在主世界的 {@link SavedData} 中，而不是静态集合中：结构坐标、活动实体
 * UUID 和下一次允许重生的游戏刻会随存档写入，并在服务端重启后恢复。一个 hut 只保留
 * 一个活动 UUID，避免重复重生。</p>
 */
public final class WitchHutState extends SavedData {
    static final long NO_RESPAWN_SCHEDULE = -1L;
    private static final Identifier DATA_ID = Identifier.fromNamespaceAndPath("mob_battle", "witch_huts");
    private static final Codec<Hut> HUT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("structure_chunk").forGetter(Hut::structureChunk),
            Codec.INT.fieldOf("spawn_x").forGetter(hut -> hut.spawnPos().getX()),
            Codec.INT.fieldOf("spawn_y").forGetter(hut -> hut.spawnPos().getY()),
            Codec.INT.fieldOf("spawn_z").forGetter(hut -> hut.spawnPos().getZ()),
            Codec.LONG.fieldOf("next_respawn_tick").forGetter(Hut::nextRespawnTick),
            UUIDUtil.CODEC.optionalFieldOf("active_witch").forGetter(hut -> java.util.Optional.ofNullable(hut.activeWitch()))
    ).apply(instance, (structureChunk, x, y, z, nextRespawnTick, activeWitch) ->
            new Hut(structureChunk, new BlockPos(x, y, z), nextRespawnTick, activeWitch.orElse(null))));
    public static final Codec<WitchHutState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            HUT_CODEC.listOf().fieldOf("huts").forGetter(state -> List.copyOf(state.huts.values()))
    ).apply(instance, WitchHutState::new));
    private static final SavedDataType<WitchHutState> TYPE =
            new SavedDataType<>(DATA_ID, WitchHutState::new, CODEC, DataFixTypes.LEVEL);

    private final Map<Long, Hut> huts = new HashMap<>();
    private final Map<UUID, Long> witchToHut = new HashMap<>();

    public WitchHutState() {
    }

    private WitchHutState(List<Hut> savedHuts) {
        for (Hut hut : savedHuts) {
            this.huts.put(hut.structureChunk(), hut);
            if (hut.activeWitch() != null) {
                this.witchToHut.put(hut.activeWitch(), hut.structureChunk());
            }
        }
    }

    static WitchHutState get(MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(TYPE);
    }

    Long hutForWitch(UUID witchUuid) {
        return this.witchToHut.get(witchUuid);
    }

    void registerLoadedWitch(long structureChunk, BlockPos spawnPos, UUID witchUuid) {
        Hut current = this.huts.get(structureChunk);
        if (current == null) {
            this.huts.put(structureChunk, new Hut(structureChunk, spawnPos, NO_RESPAWN_SCHEDULE, witchUuid));
            this.witchToHut.put(witchUuid, structureChunk);
            this.setDirty();
            return;
        }

        if (current.activeWitch() == null) {
            this.huts.put(structureChunk, new Hut(structureChunk, current.spawnPos(), current.nextRespawnTick(), witchUuid));
            this.witchToHut.put(witchUuid, structureChunk);
            this.setDirty();
        }
    }

    void markDeath(UUID witchUuid, long deathTick) {
        Long structureChunk = this.witchToHut.remove(witchUuid);
        if (structureChunk == null) {
            return;
        }

        Hut current = this.huts.get(structureChunk);
        if (current != null && witchUuid.equals(current.activeWitch())) {
            this.huts.put(structureChunk, new Hut(
                    current.structureChunk(),
                    current.spawnPos(),
                    deathTick + WitchHutLifecycle.RESPAWN_COOLDOWN_TICKS,
                    null
            ));
            this.setDirty();
        }
    }

    void markSpawned(long structureChunk, UUID witchUuid) {
        Hut current = this.huts.get(structureChunk);
        if (current == null) {
            return;
        }

        this.huts.put(structureChunk, new Hut(
                current.structureChunk(), current.spawnPos(), NO_RESPAWN_SCHEDULE, witchUuid));
        this.witchToHut.put(witchUuid, structureChunk);
        this.setDirty();
    }

    List<Hut> huts() {
        return List.copyOf(this.huts.values());
    }

    record Hut(long structureChunk, BlockPos spawnPos, long nextRespawnTick, UUID activeWitch) {
    }
}
