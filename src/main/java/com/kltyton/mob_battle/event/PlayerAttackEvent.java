package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.command.FriendlyDamageCommand;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

public class PlayerAttackEvent {
    public static void init() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient()) {
                if (!((ServerWorld) world).getGameRules().getBoolean(FriendlyDamageCommand.ENABLE_FRIENDLY_DAMAGE) && player.isTeammate(entity)){
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });
    }
}
