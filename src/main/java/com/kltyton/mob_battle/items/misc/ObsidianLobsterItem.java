package com.kltyton.mob_battle.items.misc;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.components.ModComponents;
import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public class ObsidianLobsterItem extends ShieldItem implements ModFabricItem {
    public ObsidianLobsterItem(Properties settings) {
        super(settings);
    }

    @Override
    public void onDestroyed(net.minecraft.world.entity.item.ItemEntity entity) {
        super.onDestroyed(entity);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHurtEnemy(stack, target, attacker);
    }

    @Override
    public void onDurabilityChange(ItemStack stack, int amount, ServerPlayer player) {
        int oldDamage = stack.getDamageValue();
        int maxDamage = stack.getMaxDamage();
        boolean transformed = stack.getOrDefault(ModComponents.LOBSTER_TRANSFORMED, false);
        if (oldDamage < maxDamage && oldDamage + amount >= maxDamage && !transformed) {
            ItemStack broken = new ItemStack(ModItems.BURST_OBSIDIAN_LOBSTER);

            if (player.getOffhandItem() == stack) {
                player.setItemInHand(InteractionHand.OFF_HAND, broken);
            } else if (player.getMainHandItem() == stack) {
                Mob_battle.LOGGER.info("Transformed");
                player.setItemInHand(InteractionHand.MAIN_HAND, broken);
            } else if (!player.getInventory().add(broken)) {
                player.drop(broken, false);
            }
            stack.set(ModComponents.LOBSTER_TRANSFORMED, true);
        }
    }
}
