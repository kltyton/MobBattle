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
public class KnifeProjectileModel extends EntityModel<TippableArrowRenderState> {
    public KnifeProjectileModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition handle = root.addOrReplaceChild("bone11", CubeListBuilder.create(), PartPose.offset(0.06F, -0.29F, -0.3F));
        handle.addOrReplaceChild("bone12",
                CubeListBuilder.create().texOffs(14, 13)
                        .addBox(-1.82F, -0.36F, -2.87F, 0.01F, 0.3F, 1.89F, new CubeDeformation(0.0F)),
                PartPose.offset(1.81F, 0.22F, 0.15F));
        handle.addOrReplaceChild("bone13",
                CubeListBuilder.create().texOffs(0, 14)
                        .addBox(-4.0F, -0.44F, -1.0F, 0.4F, 0.44F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(3.81F, 0.22F, 0.15F));
        return LayerDefinition.create(mesh, 16, 16);
    }
}
