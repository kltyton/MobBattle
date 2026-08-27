package com.kltyton.mob_battle.mixin.damage;

import com.kltyton.mob_battle.entity.littleperson.skillentity.ironmanbullet.IronManBulletEntity;
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
     * 覆盖 Minecraft 26.1.2 的完整护甲减伤公式，以保留本项目“护甲穿透”附魔对
     * 护甲有效率的二次修正及最高 96% 减伤上限。该修改横跨原方法的夹取、附魔修正
     * 和最终乘算，拆成多个注入点会依赖局部变量与执行顺序，无法形成稳定的等价边界。
     * 上游若调整 {@link CombatRules} 公式或参数顺序，必须逐行重新核对本覆写。
     *
     * @author KLTYTON
     * @reason 保留项目级护甲穿透公式与 96% 上限，并明确锁定 Minecraft 26.1.2 实现。
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
        if (damageSource.getDirectEntity() instanceof IronManBulletEntity) {
            i = Math.max(0.0F, i - 0.15F);
        }

        float j = 1.0F - i;
        return damageAmount * j;
    }
    /**
     * 覆盖 Minecraft 26.1.2 的魔法保护减伤公式，将保护值上限与本项目的 96% 减伤
     * 规则保持一致。该方法本身就是不可分割的纯公式，局部常量注入会比完整公式更依赖
     * 字节码形状；上游修改保护值夹取或除数时必须重新核对。
     *
     * @author KLTYTON
     * @reason 保留项目级魔法保护上限，并明确锁定 Minecraft 26.1.2 公式。
     */
    @Overwrite
    public static float getDamageAfterMagicAbsorb(float damageDealt, float protection) {
        float f = Mth.clamp(protection, 0.0F, 24.0F);
        return damageDealt * (1.0F - f / 25.0F);
    }
}

