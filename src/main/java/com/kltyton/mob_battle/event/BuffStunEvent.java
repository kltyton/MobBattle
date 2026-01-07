package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.effect.ModEffects;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.util.ActionResult;

public class BuffStunEvent {
    public static void init() {
        // 阻止攻击
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.hasStatusEffect(ModEffects.STUN_ENTRY)) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        // 阻止使用物品
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (player.hasStatusEffect(ModEffects.STUN_ENTRY)) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        // 阻止与实体交互（例如右键村民）
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.hasStatusEffect(ModEffects.STUN_ENTRY)) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }
}
