package com.kltyton.mob_battle.mixin.armor;

import com.kltyton.mob_battle.items.armor.ModBaseArmorItem;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EquipmentLayerRenderer.class)
public abstract class EcredcultistGlowArmorRenderer {

    @Inject(
            method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V", shift = At.Shift.AFTER)
    )
    private void render(EquipmentClientInfo.LayerType layerType, ResourceKey<EquipmentAsset> assetKey, Model model, ItemStack stack, PoseStack matrices, MultiBufferSource vertexConsumers, int light, @Nullable ResourceLocation texture, CallbackInfo ci, @Local(ordinal = 2) int j, @Local(ordinal = 1) ResourceLocation identifier) {
        if (stack.getItem() instanceof ModBaseArmorItem modBaseArmorItem) modBaseArmorItem.renderCustomArmor(model, stack, matrices, vertexConsumers, light, identifier, j);
    }
}
