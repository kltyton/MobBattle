package com.kltyton.mob_battle.mixin.enchantment;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 扩展原版 Multishot 的适用范围和等级上限，使所有 {@link BowItem} 子类沿用原版弓的发射管线。
 * 附魔组件仍由 Minecraft 数据驱动；这里只修正弓的适用性、255 存储上限和弓的发射数量。
 */
@Mixin(Enchantment.class)
public abstract class MultishotEnchantmentMixin {
    @Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
    private void mob_battle$allowMultishotOnBows(ItemStack item, CallbackInfoReturnable<Boolean> cir) {
        if (this.mob_battle$isMultishot() && item.getItem() instanceof BowItem) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isSupportedItem", at = @At("HEAD"), cancellable = true)
    private void mob_battle$markBowsAsSupported(ItemStack item, CallbackInfoReturnable<Boolean> cir) {
        if (this.mob_battle$isMultishot() && item.getItem() instanceof BowItem) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isPrimaryItem", at = @At("HEAD"), cancellable = true)
    private void mob_battle$markBowsAsPrimary(ItemStack item, CallbackInfoReturnable<Boolean> cir) {
        if (this.mob_battle$isMultishot() && item.getItem() instanceof BowItem) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    private void mob_battle$allowStoredMaximumLevel(CallbackInfoReturnable<Integer> cir) {
        if (this.mob_battle$isMultishot()) {
            cir.setReturnValue(Enchantment.MAX_LEVEL);
        }
    }

    @Inject(method = "modifyProjectileCount", at = @At("HEAD"), cancellable = true)
    private void mob_battle$useBowLevelAsProjectileCount(
            ServerLevel serverLevel,
            int enchantmentLevel,
            ItemStack weapon,
            Entity shooter,
            MutableFloat count,
            CallbackInfo ci
    ) {
        if (this.mob_battle$isMultishot() && weapon.getItem() instanceof BowItem) {
            count.setValue(Math.clamp(enchantmentLevel, 1, Enchantment.MAX_LEVEL));
            ci.cancel();
        }
    }

    @Unique
    private boolean mob_battle$isMultishot() {
        Enchantment enchantment = (Enchantment) (Object) this;
        return enchantment.effects().has(net.minecraft.world.item.enchantment.EnchantmentEffectComponents.PROJECTILE_COUNT);
    }
}
