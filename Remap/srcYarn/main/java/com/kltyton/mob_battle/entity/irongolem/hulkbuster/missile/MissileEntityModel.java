package com.kltyton.mob_battle.entity.irongolem.hulkbuster.missile;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.util.math.MathHelper;

public class MissileEntityModel extends EntityModel<ProjectileEntityRenderState> {
    private final ModelPart bone;
    public MissileEntityModel(ModelPart root) {
        super(root);
        this.bone = root.getChild("bone");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -14.0F, -4.0F, 6.0F, 6.0F, 9.0F, new Dilation(0.0F))
                .uv(0, 15).cuboid(-2.0F, -13.0F, -6.0F, 4.0F, 4.0F, 2.0F, new Dilation(0.0F))
                .uv(12, 15).cuboid(-1.0F, -8.0F, 3.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(12, 19).cuboid(-1.0F, -15.0F, 3.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(21, 15).cuboid(3.0F, -12.0F, 3.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(21, 19).cuboid(-4.0F, -12.0F, 3.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 21).cuboid(-1.0F, -7.0F, 4.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(16, 23).cuboid(-5.0F, -12.0F, 4.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(8, 23).cuboid(-1.0F, -16.0F, 4.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(22, 23).cuboid(4.0F, -12.0F, 4.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 22).cuboid(0.0F, -13.0F, 4.0F, 0.0F, 6.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 15.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r1 = bone.addChild("cube_r1", ModelPartBuilder.create().uv(0, 21).cuboid(1.0F, -6.0F, -1.0F, 0.0F, 6.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, -12.0F, 5.0F, 0.0F, 0.0F, 1.5708F));
        return TexturedModelData.of(modelData, 32, 32);
    }
    @Override
    public void setAngles(ProjectileEntityRenderState state) {
        this.root.roll = 0.0F;
        this.root.yaw = 0.0F;
        this.root.pitch = 0.0F;
        if (state.shake > 0.0F) {
            float f = -MathHelper.sin(state.shake * 3.0F) * state.shake;
            this.root.roll = f * (float)(Math.PI / 180.0);
        }
    }
}
