package com.kltyton.mob_battle.mixin.trident;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin {
    @ModifyArgs(
            method = "releaseUsing",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;startAutoSpinAttack(IFLnet/minecraft/world/item/ItemStack;)V")
    )
    private void useMeleeDamageForRiptide(Args args, ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        float originalDamage = args.get(1);
        float meleeDamage = (float) user.getAttributeValue(Attributes.ATTACK_DAMAGE);
        args.set(1, Math.max(originalDamage, meleeDamage));
    }
}
