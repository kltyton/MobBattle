package com.kltyton.mob_battle.entity.littleperson.skillentity.requestedprojectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.TippableArrowRenderState;

@Environment(EnvType.CLIENT)
public class SevenHarvestBulletModel extends EntityModel<TippableArrowRenderState> {
    public SevenHarvestBulletModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("bb_main",
                CubeListBuilder.create().texOffs(3, 2)
                        .addBox(-0.25F, -0.2F, -0.28F, 0.5F, 0.4F, 0.56F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 16, 16);
    }
}
