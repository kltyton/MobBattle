package com.kltyton.mob_battle.mixin.spear;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntitySpearDismountMixin {
    @Redirect(
            method = "stabAttack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;stopRiding()V")
    )
    private void mobBattle$keepSpearTargetRiding(
            Entity target,
            EquipmentSlot weaponSlot,
            Entity attackedTarget,
            float baseDamage,
            boolean dealsDamage,
            boolean dealsKnockback,
            boolean dismounts
    ) {
        ItemStack weapon = ((LivingEntity) (Object) this).getItemBySlot(weaponSlot);
        if (!weapon.is(ItemTags.SPEARS)) {
            target.stopRiding();
        }
    }
}
