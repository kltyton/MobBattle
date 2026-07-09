package com.kltyton.mob_battle.items.armor;

import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.items.armor.compressarmor.CompressArmorSkillManager;
import com.kltyton.mob_battle.utils.ArmorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import org.jetbrains.annotations.Nullable;

public class ModBaseArmorItem extends Item {
    public ArmorMaterial material;
    public boolean shouldRender;
    public ModBaseArmorItem(Properties settings, ArmorMaterial material) {
        super(settings);
        this.material = material;
        this.shouldRender = true;
    }
    public ModBaseArmorItem(Properties settings, ArmorMaterial material, boolean shouldRender) {
        super(settings);
        this.material = material;
        this.shouldRender = shouldRender;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        if (entity instanceof LivingEntity living
                && ArmorUtil.hasFullArmor(living, ModMaterial.COMPRESSED_IRON_ARMOR_INSTANCE)) {
            living.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 40, 0, false, false, true));
        }
        if (entity instanceof ServerPlayer player
                && ArmorUtil.hasFullArmor(player, ModMaterial.ECREDCULTIST_INSTANCE)) {
            CompressArmorSkillManager.tickEcredcultistArmor(player);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void renderCustomArmor(Model model, Object state, ItemStack stack, PoseStack matrices, SubmitNodeCollector renderTasks, int light, @Nullable Identifier texture, int outlineColor) {
        if (texture == null || !shouldRender) return;
        String path = texture.getPath();
        String newPath;
        if (path.endsWith(".png")) {
            newPath = path.substring(0, path.length() - 4) + "_layer.png";
        } else {
            newPath = path + "_layer";
        }
        Identifier glowTexture = Identifier.fromNamespaceAndPath(texture.getNamespace(), newPath);
        matrices.pushPose();
        float scale = 1.01f;
        matrices.scale(scale, scale, scale);
        renderTasks.submitModel(model, state, matrices, RenderTypes.eyes(glowTexture), light, OverlayTexture.NO_OVERLAY, -1, null, outlineColor, null);
        matrices.popPose();
    }
}
