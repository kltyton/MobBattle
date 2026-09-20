package com.kltyton.mob_battle.event.world.witchhut;

import java.util.List;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/**
 * 原版 Witch Hut 女巫规则的唯一事件初始化入口。
 *
 * <p>所有回调均只在服务端执行线程中读写 {@link WitchHutState}。结构关联只接受主世界
 * 中实际位于原版 {@code minecraft:swamp_hut} 结构 piece 内的 Witch；其它女巫不会进入
 * 状态表，因此不会受到重生或掉落规则影响。</p>
 */
public final class WitchHutLifecycle {
    static final long RESPAWN_COOLDOWN_TICKS = 5L * 24_000L;

    private WitchHutLifecycle() {
    }

    /** 注册 Witch Hut 的加载、死亡、重生和精确掉落回调；主入口只需调用此方法一次。 */
    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register(WitchHutLifecycle::onEntityLoad);
        ServerLivingEntityEvents.AFTER_DEATH.register(WitchHutLifecycle::onAfterDeath);
        ServerTickEvents.END_SERVER_TICK.register(WitchHutLifecycle::onServerTick);
        LootTableEvents.MODIFY_DROPS.register(WitchHutLifecycle::modifyDrops);
    }

    private static void onEntityLoad(Entity entity, ServerLevel level) {
        if (!(entity instanceof Witch witch) || level.dimension() != Level.OVERWORLD) {
            return;
        }

        WitchHutState state = WitchHutState.get(level.getServer());
        if (state.hutForWitch(witch.getUUID()) != null) {
            return;
        }

        StructureStart hut = findHut(level, witch.blockPosition());
        if (hut.isValid()) {
            state.registerLoadedWitch(hut.getChunkPos().pack(), witch.blockPosition(), witch.getUUID());
        }
    }

    private static void onAfterDeath(net.minecraft.world.entity.LivingEntity entity,
                                     net.minecraft.world.damagesource.DamageSource source) {
        if (!(entity instanceof Witch witch)) {
            return;
        }

        MinecraftServer server = entity.level().getServer();
        WitchHutState.get(server).markDeath(witch.getUUID(), server.overworld().getGameTime());
    }

    private static void onServerTick(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        WitchHutState state = WitchHutState.get(server);
        long now = overworld.getGameTime();

        for (WitchHutState.Hut hut : state.huts()) {
            if (hut.nextRespawnTick() < 0L || hut.nextRespawnTick() > now || hut.activeWitch() != null) {
                continue;
            }

            BlockPos spawnPos = hut.spawnPos();
            if (!overworld.hasChunkAt(spawnPos)) {
                continue;
            }

            Witch witch = net.minecraft.world.entity.EntityType.WITCH.create(overworld, EntitySpawnReason.STRUCTURE);
            if (witch == null) {
                continue;
            }

            witch.setPersistenceRequired();
            witch.snapTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, 0.0F, 0.0F);
            witch.finalizeSpawn(overworld, overworld.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.STRUCTURE, null);
            if (overworld.tryAddFreshEntityWithPassengers(witch)) {
                state.markSpawned(hut.structureChunk(), witch.getUUID());
            }
        }
    }

    private static void modifyDrops(net.minecraft.core.Holder<net.minecraft.world.level.storage.loot.LootTable> holder,
                                    LootContext context, List<ItemStack> drops) {
        Entity entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof Witch witch)) {
            return;
        }

        if (WitchHutState.get(entity.level().getServer()).hutForWitch(witch.getUUID()) != null) {
            drops.clear();
            drops.add(new ItemStack(Items.REDSTONE, 1));
        }
    }

    private static StructureStart findHut(ServerLevel level, BlockPos pos) {
        Structure swampHut = level.registryAccess().lookupOrThrow(Registries.STRUCTURE)
                .getValueOrThrow(BuiltinStructures.SWAMP_HUT);
        return level.structureManager().getStructureWithPieceAt(pos, swampHut);
    }
}
