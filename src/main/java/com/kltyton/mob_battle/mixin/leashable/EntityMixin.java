package com.kltyton.mob_battle.mixin.leashable;

import com.kltyton.mob_battle.accessor.ILead;
import com.kltyton.mob_battle.event.alliance.AllianceUtils;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.network.packet.ILeadUpdatePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
@Implements(@Interface(iface = ILead.class, prefix = "custom$"))
public abstract class EntityMixin implements ILead {
    @Shadow
    public abstract World getWorld();

    @Shadow
    public abstract int getId();

    @Unique
    private boolean isUniversalLeadEnyity = false;
    @Unique
    private boolean isInvisibleUniversalLeadEnyity = false;
    @Unique
    public boolean custom$getIsInvisibleUniversalLeadEnyity() {
        return this.isInvisibleUniversalLeadEnyity;
    }
    @Unique
    public boolean custom$getIsUniversalLeadEnyity() {
        return this.isUniversalLeadEnyity;
    }

    @Unique
    public void custom$setIsUniversalLeadEnyity(boolean isUniversalLeadEnyity) {
        this.isUniversalLeadEnyity = isUniversalLeadEnyity;
        if (!this.getWorld().isClient()) {
            for (PlayerEntity player : this.getWorld().getPlayers()) {
                ServerPlayNetworking.send((ServerPlayerEntity) player, new ILeadUpdatePayload(this.getId(), isUniversalLeadEnyity ? 1 : 0, 3));
            }
        }
    }
    @Unique
    public void custom$setIsInvisibleUniversalLeadEnyity(boolean isInvisibleUniversalLeadEnyity) {
        this.isInvisibleUniversalLeadEnyity = isInvisibleUniversalLeadEnyity;
        if (!this.getWorld().isClient()) {
            for (PlayerEntity player : this.getWorld().getPlayers()) {
                ServerPlayNetworking.send((ServerPlayerEntity) player, new ILeadUpdatePayload(this.getId(), 3, isInvisibleUniversalLeadEnyity ? 1 : 0));
            }
        }
    }
    @Redirect(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 2))
    private boolean isUniversalLead(ItemStack instance, Item item) {
        if (instance.isOf(ModItems.UNIVERSAL_LEAD)) {
            custom$setIsUniversalLeadEnyity(true);
            custom$setIsInvisibleUniversalLeadEnyity(false);
        } else if (instance.isOf(ModItems.INVISIBLE_UNIVERSAL_LEAD)){
            custom$setIsUniversalLeadEnyity(true);
            custom$setIsInvisibleUniversalLeadEnyity(true);
        } else if (instance.isOf(Items.LEAD)) {
            custom$setIsUniversalLeadEnyity(false);
            custom$setIsInvisibleUniversalLeadEnyity(false);
        }
        return (instance.isOf(Items.LEAD) || instance.isOf(ModItems.UNIVERSAL_LEAD) || instance.isOf(ModItems.INVISIBLE_UNIVERSAL_LEAD));
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
        if (item == Items.LEAD && custom$getIsUniversalLeadEnyity()) {
            custom$setIsUniversalLeadEnyity(false);
            custom$setIsInvisibleUniversalLeadEnyity(false);
            return Items.AIR;
        }
        return item;
    }

    @Inject(method = "writeData", at = @At("TAIL"))
    private void saveCustomData(WriteView view, CallbackInfo ci) {
        view.putBoolean("IsUniversalLeadEnyity", custom$getIsUniversalLeadEnyity());
        view.putBoolean("IsInvisibleUniversalLeadEnyity", custom$getIsInvisibleUniversalLeadEnyity());
    }
    @Inject(method = "readData", at = @At("TAIL"))
    private void loadCustomData(ReadView view, CallbackInfo ci) {
        custom$setIsUniversalLeadEnyity(view.getBoolean("IsUniversalLeadEnyity", false));
        custom$setIsInvisibleUniversalLeadEnyity(view.getBoolean("IsInvisibleUniversalLeadEnyity", false));
    }
    @Inject(method = "isTeammate", at = @At("RETURN"), cancellable = true)
    //同盟指令
    public final void isTeammate(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if (other != null && AllianceUtils.isSameAlliance((Entity) (Object) this, other)) cir.setReturnValue(true);
    }
    @Inject(method = "isInSameTeam", at = @At("RETURN"), cancellable = true)
    protected void isInSameTeam(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if (other != null && AllianceUtils.isSameAlliance((Entity) (Object) this, other)) cir.setReturnValue(true);
    }
}
