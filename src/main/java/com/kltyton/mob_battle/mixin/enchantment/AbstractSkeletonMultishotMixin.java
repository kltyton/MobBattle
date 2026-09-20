package com.kltyton.mob_battle.mixin.enchantment;

import com.kltyton.mob_battle.entity.ai.ZombieBowAttackGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 让原版骷髅的原生单箭发射入口复用 Multishot；没有该附魔时保留 Mojang 原始实现。
 */
@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonMultishotMixin {
    @Inject(method = "performRangedAttack", at = @At("HEAD"), cancellable = true)
    private void mob_battle$shootMultishot(LivingEntity target, float power, CallbackInfo ci) {
        AbstractSkeleton skeleton = (AbstractSkeleton) (Object) this;
        ItemStack bow = skeleton.getMainHandItem();
        if (!(bow.getItem() instanceof BowItem)
                || !EnchantmentHelper.has(bow, EnchantmentEffectComponents.PROJECTILE_COUNT)) {
            return;
        }

        ZombieBowAttackGoal.shootProjectiles(skeleton, target, power);
        ci.cancel();
    }
}
