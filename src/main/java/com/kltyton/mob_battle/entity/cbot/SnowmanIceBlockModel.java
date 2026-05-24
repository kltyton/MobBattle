package com.kltyton.mob_battle.entity.cbot;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;

public class SnowmanIceBlockModel extends EntityModel<ProjectileEntityRenderState> {
    public SnowmanIceBlockModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("bone7",
                ModelPartBuilder.create().uv(160, 198).cuboid(-9.0F, -18.0F, -9.0F, 18.0F, 18.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.origin(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }
}
