package com.kltyton.mob_battle.items.tool;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BaseSword extends Item {
    public float attackCooldown = 0.0F;
    public BaseSword(Properties settings) {
        super(settings);
    }
    @Override
    public DamageSource getDamageSource(LivingEntity user) {
        if (user instanceof Player playerEntity) {
            this.attackCooldown = playerEntity.getAttackStrengthScale(0.0F);
        }
        return super.getDamageSource(user);
    }
    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.hurtEnemy(stack, target, attacker);
        if (this.attackCooldown >= 1.0F) addStatusEffect(target, attacker);
    }
    public void addStatusEffect(LivingEntity target, LivingEntity attacker) {}
}
