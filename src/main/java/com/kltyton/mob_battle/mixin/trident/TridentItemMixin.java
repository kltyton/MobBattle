package com.kltyton.mob_battle.mixin.trident;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin {
    @ModifyArgs(
            method = "onStoppedUsing",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;useRiptide(IFLnet/minecraft/item/ItemStack;)V")
    )
    private void useMeleeDamageForRiptide(Args args, ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        float originalDamage = args.get(1);
        float meleeDamage = (float) user.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
        args.set(1, Math.max(originalDamage, meleeDamage));
    }
}
