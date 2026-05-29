package com.kltyton.mob_battle.entity.irongolem.hulkbuster.missile;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.util.Mth;

public class MissileEntityModel extends EntityModel<ArrowRenderState> {
    private final ModelPart bone;
    public MissileEntityModel(ModelPart root) {
        super(root);
        this.bone = root.getChild("bone");
    }
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition bone = modelPartData.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -14.0F, -4.0F, 6.0F, 6.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(0, 15).addBox(-2.0F, -13.0F, -6.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(12, 15).addBox(-1.0F, -8.0F, 3.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(12, 19).addBox(-1.0F, -15.0F, 3.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(21, 15).addBox(3.0F, -12.0F, 3.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(21, 19).addBox(-4.0F, -12.0F, 3.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 21).addBox(-1.0F, -7.0F, 4.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 23).addBox(-5.0F, -12.0F, 4.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(8, 23).addBox(-1.0F, -16.0F, 4.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(22, 23).addBox(4.0F, -12.0F, 4.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 22).addBox(0.0F, -13.0F, 4.0F, 0.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 15.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_r1 = bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 21).addBox(1.0F, -6.0F, -1.0F, 0.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -12.0F, 5.0F, 0.0F, 0.0F, 1.5708F));
        return LayerDefinition.create(modelData, 32, 32);
    }
    @Override
    public void setupAnim(ArrowRenderState state) {
        this.root.zRot = 0.0F;
        this.root.yRot = 0.0F;
        this.root.xRot = 0.0F;
        if (state.shake > 0.0F) {
            float f = -Mth.sin(state.shake * 3.0F) * state.shake;
            this.root.zRot = f * (float)(Math.PI / 180.0);
        }
    }
}
