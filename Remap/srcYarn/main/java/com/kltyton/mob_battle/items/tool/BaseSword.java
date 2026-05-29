package com.kltyton.mob_battle.items.tool;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BaseSword extends Item {
    public float attackCooldown = 0.0F;
    public BaseSword(Settings settings) {
        super(settings);
    }
    @Override
    public DamageSource getDamageSource(LivingEntity user) {
        if (user instanceof PlayerEntity playerEntity) {
            this.attackCooldown = playerEntity.getAttackCooldownProgress(0.0F);
        }
        return super.getDamageSource(user);
    }
    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHit(stack, target, attacker);
        if (this.attackCooldown >= 1.0F) addStatusEffect(target, attacker);
    }
    public void addStatusEffect(LivingEntity target, LivingEntity attacker) {}
}
