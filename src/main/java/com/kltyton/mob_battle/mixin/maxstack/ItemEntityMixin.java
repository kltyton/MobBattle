package com.kltyton.mob_battle.mixin.maxstack;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.items.ModFabricItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow
    public abstract ItemStack getStack();

    @ModifyConstant(method = "merge(Lnet/minecraft/entity/ItemEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V", constant = @Constant(intValue = 64))
    private static int merge(int val) {
        return Mob_battle.MAX_STACK_SIZE;
    }
    //TODO 潜在地删除这个ModifyConstant，我只是想不出任何方式来优雅地删除它。

    @Inject(method = "tick", at = @At("TAIL"))
    private void transformMagmaLobsterInWater(CallbackInfo ci) {
        if (this.getStack().getItem() instanceof ModFabricItem modFabricItem) {
            modFabricItem.itemEntityHook(this.getStack(), (ItemEntity) (Object) this);
        }
    }
}
