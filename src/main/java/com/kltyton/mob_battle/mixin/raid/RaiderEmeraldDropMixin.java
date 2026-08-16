package com.kltyton.mob_battle.mixin.raid;

import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Replaces emerald stacks emitted by any raid-capable illager with the mod currency. */
@Mixin(Entity.class)
public abstract class RaiderEmeraldDropMixin {
    @ModifyVariable(
            method = "spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private ItemStack mobBattle$replaceEmeraldDrop(ItemStack stack) {
        if ((Object) this instanceof Raider && stack.is(Items.EMERALD)) {
            return stack.transmuteCopy(ModItems.ILLAGER_CURRENCY);
        }
        return stack;
    }
}
