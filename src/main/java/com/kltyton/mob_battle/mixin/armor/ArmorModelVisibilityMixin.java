package com.kltyton.mob_battle.mixin.armor;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zigythebird.playeranim.accessors.IAvatarAnimationState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * PAL 1.2.4 第一人称提交会隐藏共享护甲模型的根节点，而原版 resetPose 不恢复可见性。
 * 在提交边界还原该临时标记，避免污染延迟绘制和后续玩家；肢体显示仍由 PAL 的姿势配置控制。
 */
@Mixin(HumanoidArmorLayer.class)
public abstract class ArmorModelVisibilityMixin {
    @Invoker("getArmorModel")
    protected abstract HumanoidModel<?> mobBattle$getArmorModel(HumanoidRenderState state, EquipmentSlot slot);

    @WrapMethod(method = "renderArmorPiece")
    private void mobBattle$preserveModelVisibility(PoseStack poses, SubmitNodeCollector collector, ItemStack stack,
                                                  EquipmentSlot slot, int light, HumanoidRenderState state, Operation<Void> original) {
        if (!(state instanceof IAvatarAnimationState avatar) || !avatar.playerAnimLib$isFirstPersonPass()) {
            original.call(poses, collector, stack, slot, light, state);
            return;
        }
        HumanoidModel<?> model = mobBattle$getArmorModel(state, slot);
        boolean visible = model.root().visible;
        try {
            original.call(poses, collector, stack, slot, light, state);
        } finally {
            model.root().visible = visible;
        }
    }
}
