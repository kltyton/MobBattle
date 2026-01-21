package com.kltyton.mob_battle.entity.littleperson.archer.littlearrow;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.util.math.MathHelper;

public class StoneArrowEntityModel extends EntityModel<ProjectileEntityRenderState> {
    private final ModelPart bone;

    public StoneArrowEntityModel(ModelPart root) {
        super(root);
        this.bone = root.getChild("bone");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 0).cuboid(-11.0F, -4.0F, 7.0F, 6.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(8.0F, 24.0F, -8.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(ProjectileEntityRenderState projectileEntityRenderState) {
        super.setAngles(projectileEntityRenderState);
        if (projectileEntityRenderState.shake > 0.0F) {
            float f = -MathHelper.sin(projectileEntityRenderState.shake * 3.0F) * projectileEntityRenderState.shake;
            this.root.roll += f * (float) (Math.PI / 180.0);
        }
    }
}
