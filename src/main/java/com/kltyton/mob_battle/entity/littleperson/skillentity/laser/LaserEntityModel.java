package com.kltyton.mob_battle.entity.littleperson.skillentity.laser;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.util.math.MathHelper;

public class LaserEntityModel extends EntityModel<ProjectileEntityRenderState> {
    public LaserEntityModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(
                "bb_main",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.origin(0.0F, 24.0F, 0.0F)
        );
        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public void setAngles(ProjectileEntityRenderState projectileEntityRenderState) {
        super.setAngles(projectileEntityRenderState);
        if (projectileEntityRenderState.shake > 0.0F) {
            float roll = -MathHelper.sin(projectileEntityRenderState.shake * 3.0F) * projectileEntityRenderState.shake;
            this.root.roll += roll * (float) (Math.PI / 180.0);
        }
    }
}
