package com.kltyton.mob_battle.entity.littleperson.skillentity.spearbullet;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.util.Mth;

public class SpearBulletEntityModel extends EntityModel<ArrowRenderState> {
    private final ModelPart bb_main;
    public SpearBulletEntityModel(ModelPart root) {
        super(root);
        this.bb_main = root.getChild("bb_main");
    }
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild(
                "bb_main",
                CubeListBuilder.create().texOffs(4, 2)
                        .addBox(-0.12F, -0.12F, -0.85F, 0.24F, 0.24F, 1.7F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );
        return LayerDefinition.create(modelData, 16, 16);
    }
    @Override
    public void setupAnim(ArrowRenderState projectileEntityRenderState) {
        super.setupAnim(projectileEntityRenderState);
        if (projectileEntityRenderState.shake > 0.0F) {
            float f = -Mth.sin(projectileEntityRenderState.shake * 3.0F) * projectileEntityRenderState.shake;
            this.root.zRot += f * (float) (Math.PI / 180.0);
        }
    }
}
