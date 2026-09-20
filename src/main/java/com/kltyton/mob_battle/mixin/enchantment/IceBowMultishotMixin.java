package com.kltyton.mob_battle.mixin.enchantment;

import com.kltyton.mob_battle.items.tool.bow.IceBowItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * 将 IceBowItem 的自定义固定散射次数接入统一 Multishot 计数；未附魔时保留其原有十发行为。
 */
@Mixin(IceBowItem.class)
public abstract class IceBowMultishotMixin {
    @ModifyConstant(method = "shootShotgun", constant = @Constant(intValue = 10))
    private int mob_battle$applyMultishotToIceBow(
            int original,
            ServerLevel level,
            Player shooter,
            ItemStack bow,
            float pull,
            boolean fullyCharged
    ) {
        if (!EnchantmentHelper.has(bow, EnchantmentEffectComponents.PROJECTILE_COUNT)) {
            return original;
        }
        return EnchantmentHelper.processProjectileCount(level, bow, shooter, 1);
    }
}
