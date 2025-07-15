package com.kltyton.mob_battle.mixin;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.accessor.LeadAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Unique
    private boolean isUniversalLeadEnyity = false;
    @Unique
    public boolean getIsUniversalLeadEnyity() {
        return isUniversalLeadEnyity;
    }
    @Redirect(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 2))
    private boolean isUniversalLead(ItemStack instance, Item item) {
        if (instance.isOf(Mob_battle.UNIVERSAL_LEAD)) {
            this.isUniversalLeadEnyity = true;
            LeadAccessor.isUniversalLead = true;
        } else if (instance.isOf(Items.LEAD)) {
            this.isUniversalLeadEnyity = false;
            LeadAccessor.isUniversalLead = false;
        }
        return (instance.isOf(Items.LEAD) || instance.isOf(Mob_battle.UNIVERSAL_LEAD));
    }

/*    @ModifyArg(method = "dropItem(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/entity/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;dropItem(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemConvertible;I)Lnet/minecraft/entity/ItemEntity;"), index = 1)
    public ItemConvertible dropItem(ItemConvertible item) {

        if (item == Items.LEAD && isUniversalLeadEnyity) {
            isUniversalLeadEnyity = false;
            return Mob_battle.UNIVERSAL_LEAD;
        }
        return item;
    }*/

    @Inject(method = "writeData", at = @At("TAIL"))
    private void saveCustomData(WriteView view, CallbackInfo ci) {
        view.putBoolean("IsUniversalLeadEnyity", isUniversalLeadEnyity);
    }
    @Inject(method = "readData", at = @At("TAIL"))
    private void loadCustomData(ReadView view, CallbackInfo ci) {
        isUniversalLeadEnyity = view.getBoolean("IsUniversalLeadEnyity", false);
    }
}
