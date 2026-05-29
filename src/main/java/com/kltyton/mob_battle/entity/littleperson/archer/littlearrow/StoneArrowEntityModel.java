package com.kltyton.mob_battle.entity.littleperson.archer.littlearrow;

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

public class StoneArrowEntityModel extends EntityModel<ArrowRenderState> {
    private final ModelPart bone;

    public StoneArrowEntityModel(ModelPart root) {
        super(root);
        this.bone = root.getChild("bone");
    }
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition bone = modelPartData.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -4.0F, 7.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 24.0F, -8.0F));
        return LayerDefinition.create(modelData, 32, 32);
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
