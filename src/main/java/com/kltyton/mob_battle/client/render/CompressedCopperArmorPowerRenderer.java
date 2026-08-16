package com.kltyton.mob_battle.client.render;

import com.kltyton.mob_battle.entity.player.IPlayerStateAccessor;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.items.armor.ModBaseArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public final class CompressedCopperArmorPowerRenderer {
    private static final Identifier POWER_LOCATION = Identifier.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");
    private static final int ENERGY_COLOR = -8355712;
    private static final int POWER_ORDER_OFFSET = 100;
    private static final float ARMOR_SCALE = 1.0125F;
    private static final float BOOTS_SCALE = 1.04F;

    private CompressedCopperArmorPowerRenderer() {
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void render(Model model, Object state, ItemStack stack, PoseStack matrices, SubmitNodeCollector renderTasks, int light, int outlineColor, int order) {
        if (!(state instanceof AvatarRenderState avatarState)
                || !((IPlayerStateAccessor) avatarState).hasCompressedCopperPower()
                || !(stack.getItem() instanceof ModBaseArmorItem armorItem)
                || armorItem.material != ModMaterial.COMPRESSED_COPPER_ARMOR_INSTANCE) {
            return;
        }

        float age = avatarState.ageInTicks;
        float scale = getSlot(stack) == EquipmentSlot.FEET ? BOOTS_SCALE : ARMOR_SCALE;
        matrices.pushPose();
        matrices.scale(scale, scale, scale);
        renderTasks.order(order + POWER_ORDER_OFFSET).submitModel(
                model,
                state,
                matrices,
                RenderTypes.energySwirl(POWER_LOCATION, (age * 0.01F) % 1.0F, (age * 0.01F) % 1.0F),
                light,
                OverlayTexture.NO_OVERLAY,
                ENERGY_COLOR,
                null,
                outlineColor,
                null
        );
        matrices.popPose();
    }

    private static EquipmentSlot getSlot(ItemStack stack) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        return equippable != null ? equippable.slot() : null;
    }
}
