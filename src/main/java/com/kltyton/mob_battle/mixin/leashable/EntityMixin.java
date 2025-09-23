package com.kltyton.mob_battle.mixin.leashable;

import com.kltyton.mob_battle.accessor.ILead;
import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Leashable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
@Implements(@Interface(iface = ILead.class, prefix = "custom$"))
public abstract class EntityMixin implements ILead {
    @Unique
    private boolean isUniversalLeadEnyity = false;
    @Unique
    public boolean custom$getIsUniversalLeadEnyity() {
        return this.isUniversalLeadEnyity;
    }

    @Unique
    public void custom$setIsUniversalLeadEnyity(boolean isUniversalLeadEnyity) {
        this.isUniversalLeadEnyity = isUniversalLeadEnyity;
    }
    @Redirect(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 2))
    private boolean isUniversalLead(ItemStack instance, Item item) {
        if (instance.isOf(ModItems.UNIVERSAL_LEAD)) {
            custom$setIsUniversalLeadEnyity(true);
        } else if (instance.isOf(Items.LEAD)) {
            custom$setIsUniversalLeadEnyity(false);
        }
        return (instance.isOf(Items.LEAD) || instance.isOf(ModItems.UNIVERSAL_LEAD));
    }
    @Redirect(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Leashable;canBeLeashed()Z"))
    private static boolean canBeLeashed(Leashable leashable) {
        if (((ILead)leashable).getIsUniversalLeadEnyity()) {
            return true;
        }
        return leashable.canBeLeashed();
    }

    @ModifyArg(method = "dropItem(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/entity/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;dropItem(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemConvertible;I)Lnet/minecraft/entity/ItemEntity;"), index = 1)
    public ItemConvertible dropItem(ItemConvertible item) {
        if (item == Items.LEAD && isUniversalLeadEnyity) {
            custom$setIsUniversalLeadEnyity(false);
            return Items.AIR;
        }
        return item;
    }

    @Inject(method = "writeData", at = @At("TAIL"))
    private void saveCustomData(WriteView view, CallbackInfo ci) {
        view.putBoolean("IsUniversalLeadEnyity", custom$getIsUniversalLeadEnyity());
    }
    @Inject(method = "readData", at = @At("TAIL"))
    private void loadCustomData(ReadView view, CallbackInfo ci) {
        custom$setIsUniversalLeadEnyity(view.getBoolean("IsUniversalLeadEnyity", false));
    }
}
