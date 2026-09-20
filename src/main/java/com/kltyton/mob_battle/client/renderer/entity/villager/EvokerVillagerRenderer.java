package com.kltyton.mob_battle.client.renderer.entity.villager;

import com.kltyton.mob_battle.entity.villager.trading.EvokerVillagerEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.illager.IllagerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.illager.AbstractIllager;

/** 使用原版唤魔者模型和纹理渲染可交易村民。 */
public final class EvokerVillagerRenderer extends MobRenderer<
        EvokerVillagerEntity,
        IllagerRenderState,
        IllagerModel<IllagerRenderState>> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/illager/evoker.png");

    public EvokerVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new IllagerModel<>(context.bakeLayer(ModelLayers.EVOKER)), 0.5F);
    }

    @Override
    public IllagerRenderState createRenderState() {
        return new IllagerRenderState();
    }

    @Override
    public void extractRenderState(EvokerVillagerEntity entity, IllagerRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.armPose = AbstractIllager.IllagerArmPose.CROSSED;
    }

    @Override
    public Identifier getTextureLocation(IllagerRenderState state) {
        return TEXTURE;
    }
}
