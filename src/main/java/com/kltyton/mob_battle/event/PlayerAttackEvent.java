package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.command.FriendlyDamageCommand;
import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.items.tool.sword.BloodKnifeItem;
import com.kltyton.mob_battle.items.tool.sword.IronManMissileLauncherItem;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class PlayerAttackEvent {
    public static void init() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide()) {
                if (!((ServerLevel) world).getGameRules().get(FriendlyDamageCommand.ENABLE_FRIENDLY_DAMAGE) && player.isAlliedTo(entity)){
                    return InteractionResult.FAIL;
                }
                ItemStack stack = player.getItemInHand(hand);
                if ((stack.getItem() instanceof BloodKnifeItem || stack.getItem() instanceof IronManMissileLauncherItem)
                        && player.getCooldowns().isOnCooldown(stack)) {
                    return InteractionResult.FAIL;
                }
                if (stack.getItem() instanceof ModFabricItem modFabricItem && entity instanceof LivingEntity livingEntity){
                    if (player.getAttackStrengthScale(0.0F) >= 1.0f) modFabricItem.addStatusEffect(livingEntity, player);
                }

            }
            return InteractionResult.PASS;
        });
    }
}
