package com.kltyton.mob_battle.event.effect;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.player.IPlayerEntityAccessor;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.InteractionResult;

public class EffectEventHandler {
    public static void init() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.hasEffect(ModEffects.DISARM_ENTRY)) {
                return InteractionResult.FAIL;
            }
            if (((IPlayerEntityAccessor)player).isUsingGeckoLib()) {
                if (!player.getMainHandItem().isEmpty()) {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

        // 2. 拦截挖掘/左键方块
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (player.hasEffect(ModEffects.DISARM_ENTRY)) {
                return InteractionResult.FAIL;
            }
            if (((IPlayerEntityAccessor)player).isUsingGeckoLib()) {
                if (!player.getMainHandItem().isEmpty()) {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });
    }
}
