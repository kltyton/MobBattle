package com.kltyton.mob_battle.mixin.damage;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CombatRules.class)
public class DamageUtilMixin {

    /**
     * 修改护甲伤害减免上限，从 80% 提高到 96%
     * @author KLTYTON
     * @reason 修改护甲伤害减免上限
     */
    @Overwrite
    public static float getDamageAfterAbsorb(LivingEntity armorWearer, float damageAmount, DamageSource damageSource, float armor, float armorToughness) {
        float f = 2.0F + armorToughness / 4.0F;
        float g = Mth.clamp(armor - damageAmount / f, armor * 0.2F, 24.0F);
        float h = g / 25.0F;

        ItemStack itemStack = damageSource.getWeaponItem();
        float i;
        if (itemStack != null && armorWearer.level() instanceof ServerLevel serverWorld) {
            i = Mth.clamp(EnchantmentHelper.modifyArmorEffectiveness(serverWorld, itemStack, armorWearer, damageSource, h), 0.0F, 1.0F);
        } else {
            i = h;
        }

        float j = 1.0F - i;
        return damageAmount * j;
    }
    /**
     * @author KLTYTON
     * @reason 修改护甲伤害减免上限
     */
    @Overwrite
    public static float getDamageAfterMagicAbsorb(float damageDealt, float protection) {
        float f = Mth.clamp(protection, 0.0F, 24.0F);
        return damageDealt * (1.0F - f / 25.0F);
    }
}

