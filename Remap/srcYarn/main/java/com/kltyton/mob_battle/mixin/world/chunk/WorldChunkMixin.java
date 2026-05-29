package com.kltyton.mob_battle.mixin.world.chunk;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {
    /**
     * 场景 1: 服务器从磁盘加载旧区块
     * 在构造函数的末尾，当旧的高度图数据已经从 ProtoChunk 复制进 WorldChunk 后，强制重算。
     */
    @Inject(
            method = "<init>(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/ProtoChunk;Lnet/minecraft/world/chunk/WorldChunk$EntityLoader;)V",
            at = @At("TAIL")
    )
    private void onServerLoadForceHeightmapRecalc(ServerWorld world, ProtoChunk protoChunk, WorldChunk.EntityLoader entityLoader, CallbackInfo ci) {
        recalculateWeatherHeightmaps((WorldChunk) (Object) this);
    }

    /**
     * 场景 2: 客户端接收区块数据包
     * 在 loadFromPacket 接收完服务器发来的（可能也是错误的）高度图数据后，强制重算。
     */
    @Inject(
            method = "loadFromPacket",
            at = @At("TAIL")
    )
    private void onClientLoadForceHeightmapRecalc(PacketByteBuf buf, Map<Heightmap.Type, long[]> heightmaps, Consumer<ChunkData.BlockEntityVisitor> blockEntityVisitorConsumer, CallbackInfo ci) {
        recalculateWeatherHeightmaps((WorldChunk) (Object) this);
    }

    /**
     * 核心逻辑：强制重新计算与雨雪渲染相关的高度图
     */
    @Unique
    private void recalculateWeatherHeightmaps(WorldChunk chunk) {
        // 我们只需要重新填充那些会影响渲染和物理的类型
        // MOTION_BLOCKING 是雨水判定的核心
        Heightmap.populateHeightmaps(chunk, EnumSet.of(
                Heightmap.Type.MOTION_BLOCKING,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                Heightmap.Type.WORLD_SURFACE,
                Heightmap.Type.OCEAN_FLOOR
        ));
    }
}