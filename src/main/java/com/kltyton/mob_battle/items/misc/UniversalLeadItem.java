package com.kltyton.mob_battle.items.misc;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LeadItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class UniversalLeadItem extends LeadItem {
    public UniversalLeadItem(Settings settings) {
        super(settings);
    }
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        world.playSound(null, user.getX(), user.getY(), user.getZ(), user.getEatingSound(), user.getSoundCategory(), 1.0F, 1.0F);
        ItemStack itemStack = user.getStackInHand(hand);
        ConsumableComponent consumableComponent = itemStack.get(DataComponentTypes.CONSUMABLE);
        if (consumableComponent != null) {
            return consumableComponent.consume(user, itemStack, hand);
        } else {
            EquippableComponent equippableComponent = itemStack.get(DataComponentTypes.EQUIPPABLE);
            if (equippableComponent != null && equippableComponent.swappable()) {
                return equippableComponent.equip(itemStack, user);
            } else {
                BlocksAttacksComponent blocksAttacksComponent = itemStack.get(DataComponentTypes.BLOCKS_ATTACKS);
                if (blocksAttacksComponent != null) {
                    user.setCurrentHand(hand);
                    return ActionResult.CONSUME;
                } else {
                    return ActionResult.PASS;
                }
            }
        }
    }
}
