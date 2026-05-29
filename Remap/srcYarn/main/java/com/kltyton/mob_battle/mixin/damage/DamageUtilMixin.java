package com.kltyton.mob_battle.mixin.damage;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(DamageUtil.class)
public class DamageUtilMixin {

    /**
     * 修改护甲伤害减免上限，从 80% 提高到 96%
     * @author KLTYTON
     * @reason 修改护甲伤害减免上限
     */
    @Overwrite
    public static float getDamageLeft(LivingEntity armorWearer, float damageAmount, DamageSource damageSource, float armor, float armorToughness) {
        float f = 2.0F + armorToughness / 4.0F;
        float g = MathHelper.clamp(armor - damageAmount / f, armor * 0.2F, 24.0F);
        float h = g / 25.0F;

        ItemStack itemStack = damageSource.getWeaponStack();
        float i;
        if (itemStack != null && armorWearer.getWorld() instanceof ServerWorld serverWorld) {
            i = MathHelper.clamp(EnchantmentHelper.getArmorEffectiveness(serverWorld, itemStack, armorWearer, damageSource, h), 0.0F, 1.0F);
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
    public static float getInflictedDamage(float damageDealt, float protection) {
        float f = MathHelper.clamp(protection, 0.0F, 24.0F);
        return damageDealt * (1.0F - f / 25.0F);
    }
}

