package com.kltyton.mob_battle.event.littleperson;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.skillentity.requested.Xbot002Entity;
import com.kltyton.mob_battle.entity.littleperson.summon.Xbot002SummonPattern;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

/**
 * 铁锄激活 Xbot002 多方块结构的服务端入口。
 */
public final class Xbot002SummonEvent {
    private Xbot002SummonEvent() {
    }

    public static void init() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (!player.getItemInHand(hand).is(Items.IRON_HOE)
                    || !level.getBlockState(hitResult.getBlockPos()).is(Blocks.GOLD_BLOCK)) {
                return InteractionResult.PASS;
            }
            var match = Xbot002SummonPattern.find(level, hitResult.getBlockPos());
            if (match.isEmpty()) {
                return InteractionResult.PASS;
            }
            if (!(level instanceof ServerLevel serverLevel)) {
                return InteractionResult.SUCCESS;
            }
            Xbot002Entity xbot = ModEntities.XBOT002.create(serverLevel, EntitySpawnReason.TRIGGERED);
            if (xbot == null) {
                return InteractionResult.FAIL;
            }
            Xbot002SummonPattern.Match resolved = match.orElseThrow();
            for (var pos : resolved.consumedBlocks()) {
                serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
            xbot.snapTo(
                    resolved.spawnX(),
                    resolved.spawnY(),
                    resolved.spawnZ(),
                    resolved.axis() == net.minecraft.core.Direction.Axis.X ? 90.0F : 0.0F,
                    0.0F
            );
            xbot.finalizeSpawn(
                    serverLevel,
                    serverLevel.getCurrentDifficultyAt(xbot.blockPosition()),
                    EntitySpawnReason.TRIGGERED,
                    null
            );
            serverLevel.addFreshEntity(xbot);
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, xbot);
            }
            return InteractionResult.SUCCESS_SERVER;
        });
    }
}
