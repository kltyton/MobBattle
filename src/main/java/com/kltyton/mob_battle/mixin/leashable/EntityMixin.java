package com.kltyton.mob_battle.mixin.leashable;

import com.kltyton.mob_battle.accessor.ILead;
import com.kltyton.mob_battle.event.alliance.AllianceUtils;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.network.packet.ILeadUpdatePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    public abstract Level level();

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
        if (!this.level().isClientSide()) {
            for (Player player : this.level().players()) {
                ServerPlayNetworking.send((ServerPlayer) player, new ILeadUpdatePayload(this.getId(), isUniversalLeadEnyity ? 1 : 0, 3));
            }
        }
    }
    @Unique
    public void custom$setIsInvisibleUniversalLeadEnyity(boolean isInvisibleUniversalLeadEnyity) {
        this.isInvisibleUniversalLeadEnyity = isInvisibleUniversalLeadEnyity;
        if (!this.level().isClientSide()) {
            for (Player player : this.level().players()) {
                ServerPlayNetworking.send((ServerPlayer) player, new ILeadUpdatePayload(this.getId(), 3, isInvisibleUniversalLeadEnyity ? 1 : 0));
            }
        }
    }
    @Redirect(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 2))
    private boolean isUniversalLead(ItemStack instance, Item item) {
        if (instance.is(ModItems.UNIVERSAL_LEAD)) {
            custom$setIsUniversalLeadEnyity(true);
            custom$setIsInvisibleUniversalLeadEnyity(false);
        } else if (instance.is(ModItems.INVISIBLE_UNIVERSAL_LEAD)){
            custom$setIsUniversalLeadEnyity(true);
            custom$setIsInvisibleUniversalLeadEnyity(true);
        } else if (instance.is(Items.LEAD)) {
            custom$setIsUniversalLeadEnyity(false);
            custom$setIsInvisibleUniversalLeadEnyity(false);
        }
        return (instance.is(Items.LEAD) || instance.is(ModItems.UNIVERSAL_LEAD) || instance.is(ModItems.INVISIBLE_UNIVERSAL_LEAD));
    }
    @Redirect(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;canBeLeashed()Z"))
    private static boolean canBeLeashed(Leashable leashable) {
        if (((ILead)leashable).getIsUniversalLeadEnyity()) {
            return true;
        }
        return leashable.canBeLeashed();
    }

    @ModifyArg(method = "spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/ItemLike;I)Lnet/minecraft/world/entity/item/ItemEntity;"), index = 1)
    public ItemLike dropItem(ItemLike item) {
        if (item == Items.LEAD && custom$getIsUniversalLeadEnyity()) {
            custom$setIsUniversalLeadEnyity(false);
            custom$setIsInvisibleUniversalLeadEnyity(false);
            return Items.AIR;
        }
        return item;
    }

    @Inject(method = "saveWithoutId", at = @At("RETURN"))
    private void saveCustomData(ValueOutput view, CallbackInfo ci) {
        view.putBoolean("IsUniversalLeadEnyity", custom$getIsUniversalLeadEnyity());
        view.putBoolean("IsInvisibleUniversalLeadEnyity", custom$getIsInvisibleUniversalLeadEnyity());
    }
    @Inject(method = "load", at = @At("RETURN"))
    private void loadCustomData(ValueInput view, CallbackInfo ci) {
        custom$setIsUniversalLeadEnyity(view.getBooleanOr("IsUniversalLeadEnyity", false));
        custom$setIsInvisibleUniversalLeadEnyity(view.getBooleanOr("IsInvisibleUniversalLeadEnyity", false));
    }
    @Inject(method = "isAlliedTo(Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"), cancellable = true)
    //同盟指令
    public final void isTeammate(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if (other != null && AllianceUtils.isSameAlliance((Entity) (Object) this, other)) cir.setReturnValue(true);
    }
    @Inject(method = "considersEntityAsAlly", at = @At("RETURN"), cancellable = true)
    protected void isInSameTeam(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if (other != null && AllianceUtils.isSameAlliance((Entity) (Object) this, other)) cir.setReturnValue(true);
    }
}
