package com.kltyton.mob_battle.client.renderer.entity.villager;

import com.kltyton.mob_battle.entity.villager.trading.PiglinVillagerEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.piglin.AdultPiglinModel;
import net.minecraft.client.model.monster.piglin.PiglinModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import net.minecraft.resources.Identifier;

/** 使用原版猪灵模型和纹理渲染可交易村民。 */
public final class PiglinVillagerRenderer extends MobRenderer<
        PiglinVillagerEntity,
        PiglinRenderState,
        PiglinModel> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/piglin/piglin.png");

    public PiglinVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new AdultPiglinModel(context.bakeLayer(ModelLayers.PIGLIN)), 0.5F);
    }

    @Override
    public PiglinRenderState createRenderState() {
        return new PiglinRenderState();
    }

    @Override
    public Identifier getTextureLocation(PiglinRenderState state) {
        return TEXTURE;
    }
}
