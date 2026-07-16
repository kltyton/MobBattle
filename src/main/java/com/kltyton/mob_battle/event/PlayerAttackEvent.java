package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.command.FriendlyDamageCommand;
import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.items.cooldown.StackBoundCooldowns;
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
            ItemStack stack = player.getItemInHand(hand);
            if (world.isClientSide()) {
                if (stack.getItem() instanceof IronManMissileLauncherItem
                        && entity instanceof LivingEntity
                        && !player.isAlliedTo(entity)
                        && player.getAttackStrengthScale(0.0F) >= 1.0F
                        && !StackBoundCooldowns.isCoolingDown(player, stack, IronManMissileLauncherItem.COOLDOWN_ID, IronManMissileLauncherItem.COOLDOWN_TICKS)) {
                    StackBoundCooldowns.start(player, stack, IronManMissileLauncherItem.COOLDOWN_ID, IronManMissileLauncherItem.COOLDOWN_TICKS);
                }
                return InteractionResult.PASS;
            }
            if (!world.isClientSide()) {
                if (!((ServerLevel) world).getGameRules().get(FriendlyDamageCommand.ENABLE_FRIENDLY_DAMAGE) && player.isAlliedTo(entity)){
                    return InteractionResult.FAIL;
                }
                if (stack.getItem() instanceof BloodKnifeItem
                        && StackBoundCooldowns.isCoolingDown(player, stack, BloodKnifeItem.COOLDOWN_ID, BloodKnifeItem.COOLDOWN_TICKS)) {
                    return InteractionResult.FAIL;
                }
                if (stack.getItem() instanceof IronManMissileLauncherItem
                        && StackBoundCooldowns.isCoolingDown(player, stack, IronManMissileLauncherItem.COOLDOWN_ID, IronManMissileLauncherItem.COOLDOWN_TICKS)) {
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
