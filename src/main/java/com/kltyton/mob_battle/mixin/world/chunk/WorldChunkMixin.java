package com.kltyton.mob_battle.mixin.world.chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Heightmap;

@Mixin(LevelChunk.class)
public class WorldChunkMixin {
    /**
     * 场景 1: 服务器从磁盘加载旧区块
     * 在构造函数的末尾，当旧的高度图数据已经从 ProtoChunk 复制进 WorldChunk 后，强制重算。
     */
    @Inject(
            method = "<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ProtoChunk;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;)V",
            at = @At("TAIL")
    )
    private void onServerLoadForceHeightmapRecalc(ServerLevel world, ProtoChunk protoChunk, LevelChunk.PostLoadProcessor entityLoader, CallbackInfo ci) {
        recalculateWeatherHeightmaps((LevelChunk) (Object) this);
    }

    /**
     * 场景 2: 客户端接收区块数据包
     * 在 loadFromPacket 接收完服务器发来的（可能也是错误的）高度图数据后，强制重算。
     */
    @Inject(
            method = "replaceWithPacketData",
            at = @At("TAIL")
    )
    private void onClientLoadForceHeightmapRecalc(FriendlyByteBuf buf, Map<Heightmap.Types, long[]> heightmaps, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> blockEntityVisitorConsumer, CallbackInfo ci) {
        recalculateWeatherHeightmaps((LevelChunk) (Object) this);
    }

    /**
     * 核心逻辑：强制重新计算与雨雪渲染相关的高度图
     */
    @Unique
    private void recalculateWeatherHeightmaps(LevelChunk chunk) {
        // 我们只需要重新填充那些会影响渲染和物理的类型
        // MOTION_BLOCKING 是雨水判定的核心
        Heightmap.primeHeightmaps(chunk, EnumSet.of(
                Heightmap.Types.MOTION_BLOCKING,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Heightmap.Types.WORLD_SURFACE,
                Heightmap.Types.OCEAN_FLOOR
        ));
    }
}