package com.kltyton.mob_battle.client.renderer.entitydomain;

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

/** 将甲方提供的 16x16 小/大传仁工子弹模型转换为当前 EntityModel 坐标。 */
@Environment(EnvType.CLIENT)
public final class ChuanRenGongProjectileModel extends EntityModel<TippableArrowRenderState> {
    public ChuanRenGongProjectileModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createSmallLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(3, 2)
                        .addBox(0.0F, -0.09F, -1.0F, 0.06F, 0.09F, 0.95F,
                                new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 16, 16);
    }

    public static LayerDefinition createLargeLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, -1)
                        .addBox(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 3.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 16, 16);
    }
}
