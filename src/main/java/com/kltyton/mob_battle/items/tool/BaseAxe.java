package com.kltyton.mob_battle.items.tool;

import com.kltyton.mob_battle.items.ModMaterial;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;

public class BaseAxe extends AxeItem {
    public float attackCooldown = 0.0F;
    public BaseAxe(Properties settings) {
        super(ModMaterial.KLTYTON_TOOL_MATERIAL, 46, -3.3f, settings.axe(ModMaterial.KLTYTON_TOOL_MATERIAL,50,0.7f));
    }    public DamageSource getDamageSource(LivingEntity user) {
        if (user instanceof Player playerEntity) {
            this.attackCooldown = playerEntity.getAttackStrengthScale(0.0F);
        }
        return super.getDamageSource(user);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.hurtEnemy(stack, target, attacker);
        if (this.attackCooldown >= 1.0F)
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 2, false, true, true), attacker);
    }
}
