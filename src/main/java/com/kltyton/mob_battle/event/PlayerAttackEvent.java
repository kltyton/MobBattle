package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.command.FriendlyDamageCommand;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;

public class PlayerAttackEvent {
    public static void init() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide()) {
                if (!((ServerLevel) world).getGameRules().getBoolean(FriendlyDamageCommand.ENABLE_FRIENDLY_DAMAGE) && player.isAlliedTo(entity)){
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });
    }
}
