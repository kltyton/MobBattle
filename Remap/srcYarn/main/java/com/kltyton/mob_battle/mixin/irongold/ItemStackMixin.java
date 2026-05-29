package com.kltyton.mob_battle.mixin.irongold;

import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.tool.irongold.IronGoldSword;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract Item getItem();

    @Inject(method = "setDamage", at = @At("HEAD"), cancellable = true)
    public void setDamage(int damage, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.getItem() instanceof IronGoldSword && damage >= stack.getMaxDamage() - 1) {
            stack.set(DataComponentTypes.DAMAGE, MathHelper.clamp(damage, 0, stack.getMaxDamage() - 1));
            ci.cancel();
        }
    }
    @Inject(method = "getName", at = @At("RETURN"), cancellable = true)
    public void getName(CallbackInfoReturnable<Text> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        Text originalName = cir.getReturnValue();
        if (stack.isOf(ModItems.IRON_GOLD_SWORD) && stack.getDamage() >= stack.getMaxDamage() - 1) {
            MutableText damagedName = originalName.copy();
            damagedName.append(Text.literal("（已损坏）").styled(style -> style.withColor(0xFF5555)));
            cir.setReturnValue(damagedName);
        }
    }
    @Inject(method = "inventoryTick", at = @At("HEAD"))
    public void inventoryTick(World world, Entity entity, EquipmentSlot slot, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (this.getItem() instanceof ModFabricItem modfabricItem) modfabricItem.inventoryTick(stack, world, entity, slot);
    }
    @Inject(method = "onDurabilityChange", at = @At("HEAD"))
    public void onDurabilityChange(int damage, @Nullable ServerPlayerEntity player, Consumer<Item> breakCallback, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (this.getItem() instanceof ModFabricItem modfabricItem) modfabricItem.onDurabilityChange(stack, damage, player);
    }
}
