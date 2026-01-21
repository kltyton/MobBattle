package com.kltyton.mob_battle.items.armor;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ModBaseArmorItem extends Item {
    public ArmorMaterial material;
    public ModBaseArmorItem(Settings settings, ArmorMaterial material) {
        super(settings);
        this.material = material;
    }
    public void renderCustomArmor(Model model, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, @Nullable Identifier texture, int j) {
        if (texture == null) return;
        String path = texture.getPath();
        String newPath;
        if (path.endsWith(".png")) {
            newPath = path.substring(0, path.length() - 4) + "_layer.png";
        } else {
            newPath = path + "_layer";
        }
        Identifier glowTexture = Identifier.of(texture.getNamespace(), newPath);
        matrices.push();
        float scale = 1.01f;
        matrices.scale(scale, scale, scale);
        VertexConsumer glowConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getEyes(glowTexture), stack.hasGlint());
        model.render(matrices, glowConsumer, light, OverlayTexture.DEFAULT_UV, j);
        matrices.pop();
    }
}
