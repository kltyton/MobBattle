package com.kltyton.mob_battle.items.tool;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.items.ModFabricItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BaseSword extends Item implements ModFabricItem {
    public float attackCooldown = 0.0F;
    public BaseSword(Properties settings) {
        super(settings);
    }
/*    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player playerEntity) {
            this.attackCooldown = playerEntity.getAttackStrengthScale(0.0F);
        }
        super.hurtEnemy(stack, target, attacker);
        if (this.attackCooldown >= 1.0F) addStatusEffect(target, attacker);
    }*/
}
