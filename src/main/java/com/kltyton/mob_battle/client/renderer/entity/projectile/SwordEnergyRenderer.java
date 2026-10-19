package com.kltyton.mob_battle.client.renderer.entity.projectile;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.kltyton.mob_battle.entity.general.GeneralEntityModel;
import com.kltyton.mob_battle.entity.general.GeneralEntityRenderer;
import com.kltyton.mob_battle.entity.littleperson.skillentity.SkillProjectileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

/** 将血剑气、冰剑气模型的 -Z 前向对齐飞行向量，同时适配水平朝向和俯仰。 */
@Environment(EnvType.CLIENT)
public final class SwordEnergyRenderer<R extends EntityRenderState & GeoRenderState>
        extends GeneralEntityRenderer<SkillProjectileEntity, R> {
    public SwordEnergyRenderer(EntityRendererProvider.Context context, String name) {
        super(context, name, false, GeneralEntityModel.RenderTypes.TRANSLUCENT);
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<R> renderPassInfo) {
        Vec3 velocity = renderPassInfo.renderState().getOrDefaultGeckolibData(DataTickets.VELOCITY, Vec3.ZERO);
        if (velocity.lengthSqr() < 1.0E-8D) {
            super.adjustRenderPose(renderPassInfo);
            return;
        }
        Vec3 direction = velocity.normalize();
        renderPassInfo.poseStack().mulPose(new Quaternionf().rotationTo(
                0.0F, 0.0F, -1.0F, (float) direction.x, (float) direction.y, (float) direction.z));
        renderPassInfo.poseStack().translate(0.0F, 0.01F, 0.0F);
    }
}
