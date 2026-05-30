package com.kltyton.mob_battle.mixin.irongold;

import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.tool.irongold.IronGoldSword;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract Item getItem();

    @Inject(method = "setDamageValue", at = @At("HEAD"), cancellable = true)
    public void setDamage(int damage, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.getItem() instanceof IronGoldSword && damage >= stack.getMaxDamage() - 1) {
            stack.set(DataComponents.DAMAGE, Mth.clamp(damage, 0, stack.getMaxDamage() - 1));
            ci.cancel();
        }
    }
    @Inject(method = "getHoverName", at = @At("RETURN"), cancellable = true)
    public void getName(CallbackInfoReturnable<Component> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        Component originalName = cir.getReturnValue();
        if (stack.is(ModItems.IRON_GOLD_SWORD) && stack.getDamageValue() >= stack.getMaxDamage() - 1) {
            MutableComponent damagedName = originalName.copy();
            damagedName.append(Component.literal("（已损坏）").withStyle(style -> style.withColor(0xFF5555)));
            cir.setReturnValue(damagedName);
        }
    }
    @Inject(method = "inventoryTick", at = @At("HEAD"))
    public void inventoryTick(Level world, Entity entity, EquipmentSlot slot, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (this.getItem() instanceof ModFabricItem modfabricItem) modfabricItem.inventoryTick(stack, world, entity, slot);
    }
    @Inject(method = "applyDamage", at = @At("HEAD"))
    public void onDurabilityChange(int damage, @Nullable ServerPlayer player, Consumer<Item> breakCallback, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (this.getItem() instanceof ModFabricItem modfabricItem) modfabricItem.onDurabilityChange(stack, damage, player);
    }
}
