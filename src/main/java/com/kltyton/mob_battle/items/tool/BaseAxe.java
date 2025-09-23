package com.kltyton.mob_battle.items.tool;

import com.kltyton.mob_battle.items.ModMaterial;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;

public class BaseAxe extends AxeItem {
    public float attackCooldown = 0.0F;
    public BaseAxe(Settings settings) {
        super(ModMaterial.KLTYTON_TOOL_MATERIAL, 46, -3.3f, settings.axe(ModMaterial.KLTYTON_TOOL_MATERIAL,50,0.7f));
    }    public DamageSource getDamageSource(LivingEntity user) {
        if (user instanceof PlayerEntity playerEntity) {
            this.attackCooldown = playerEntity.getAttackCooldownProgress(0.0F);
        }
        return super.getDamageSource(user);
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHit(stack, target, attacker);
        if (this.attackCooldown >= 1.0F)
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 2, false, true, true), attacker);
    }
}
