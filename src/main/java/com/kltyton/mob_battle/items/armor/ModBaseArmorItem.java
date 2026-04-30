package com.kltyton.mob_battle.items.armor;

import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.utils.ArmorUtil;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ModBaseArmorItem extends Item {
    public ArmorMaterial material;
    public boolean shouldRender;
    public ModBaseArmorItem(Settings settings, ArmorMaterial material) {
        super(settings);
        this.material = material;
        this.shouldRender = true;
    }
    public ModBaseArmorItem(Settings settings, ArmorMaterial material, boolean shouldRender) {
        super(settings);
        this.material = material;
        this.shouldRender = shouldRender;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        if (entity instanceof LivingEntity living
                && ArmorUtil.hasFullArmor(living, ModMaterial.COMPRESSED_IRON_ARMOR_INSTANCE)) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 40, 0, false, false, true));
        }
    }

    public void renderCustomArmor(Model model, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, @Nullable Identifier texture, int j) {
        if (texture == null || !shouldRender) return;
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
