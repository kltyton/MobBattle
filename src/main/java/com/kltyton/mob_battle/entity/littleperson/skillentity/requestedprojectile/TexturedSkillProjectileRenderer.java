package com.kltyton.mob_battle.entity.littleperson.skillentity.requestedprojectile;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.client.render.SubmitRenderUtil;
import com.kltyton.mob_battle.entity.littleperson.skillentity.SkillProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.TippableArrowRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;

public class TexturedSkillProjectileRenderer extends EntityRenderer<SkillProjectileEntity, TippableArrowRenderState> {
    private final EntityModel<TippableArrowRenderState> model;
    private final Identifier texture;
    private final float scale;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public TexturedSkillProjectileRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer, Identifier texture, float scale, Class<? extends EntityModel> modelClass) {
        super(context);
        this.model = createModel(modelClass, context.bakeLayer(layer));
        this.texture = texture;
        this.scale = scale;
    }

    @SuppressWarnings("unchecked")
    private static EntityModel<TippableArrowRenderState> createModel(Class<? extends EntityModel> modelClass, net.minecraft.client.model.geom.ModelPart root) {
        if (modelClass == KnifeProjectileModel.class) {
            return (EntityModel<TippableArrowRenderState>) new KnifeProjectileModel(root);
        }
        if (modelClass == SkeletonHeadProjectileModel.class) {
            return (EntityModel<TippableArrowRenderState>) new SkeletonHeadProjectileModel(root);
        }
        return (EntityModel<TippableArrowRenderState>) new SevenHarvestBulletModel(root);
    }

    public static Identifier texture(String name) {
        return Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/projectiles/" + name + ".png");
    }

    @Override
    public void submit(TippableArrowRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        matrices.pushPose();
        matrices.scale(this.scale, this.scale, this.scale);
        matrices.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        matrices.mulPose(Axis.ZP.rotationDegrees(state.xRot));
        SubmitRenderUtil.submitModel(this.model, state, matrices, renderTasks, this.texture);
        matrices.popPose();
        super.submit(state, matrices, renderTasks, cameraState);
    }

    @Override
    public void extractRenderState(SkillProjectileEntity entity, TippableArrowRenderState state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.xRot = entity.getXRot(tickDelta);
        state.yRot = entity.getYRot(tickDelta);
    }

    @Override
    public TippableArrowRenderState createRenderState() {
        return new TippableArrowRenderState();
    }
}
