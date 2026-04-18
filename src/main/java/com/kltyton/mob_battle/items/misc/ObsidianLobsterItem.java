package com.kltyton.mob_battle.items.misc;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.components.ModComponents;
import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class ObsidianLobsterItem extends ShieldItem implements ModFabricItem {
    public ObsidianLobsterItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onItemEntityDestroyed(net.minecraft.entity.ItemEntity entity) {
        super.onItemEntityDestroyed(entity);
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postDamageEntity(stack, target, attacker);
    }

    @Override
    public void onDurabilityChange(ItemStack stack, int amount, ServerPlayerEntity player) {
        int oldDamage = stack.getDamage();
        int maxDamage = stack.getMaxDamage();
        boolean transformed = stack.getOrDefault(ModComponents.LOBSTER_TRANSFORMED, false);
        if (oldDamage < maxDamage && oldDamage + amount >= maxDamage && !transformed) {
            ItemStack broken = new ItemStack(ModItems.BURST_OBSIDIAN_LOBSTER);

            if (player.getOffHandStack() == stack) {
                player.setStackInHand(Hand.OFF_HAND, broken);
            } else if (player.getMainHandStack() == stack) {
                Mob_battle.LOGGER.info("Transformed");
                player.setStackInHand(Hand.MAIN_HAND, broken);
            } else if (!player.getInventory().insertStack(broken)) {
                player.dropItem(broken, false);
            }
            stack.set(ModComponents.LOBSTER_TRANSFORMED, true);
        }
    }
}
