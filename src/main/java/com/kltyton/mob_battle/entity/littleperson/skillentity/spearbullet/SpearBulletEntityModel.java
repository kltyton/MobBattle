package com.kltyton.mob_battle.entity.littleperson.skillentity.spearbullet;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.util.math.MathHelper;

public class SpearBulletEntityModel extends EntityModel<ProjectileEntityRenderState> {
    private final ModelPart bb_main;
    public SpearBulletEntityModel(ModelPart root) {
        super(root);
        this.bb_main = root.getChild("bb_main");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(2, 19).cuboid(-0.5F, 0.0F, -4.0F, 0.5F, 0.5F, 9.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));
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
