package com.kltyton.mob_battle.entity.villager.militia;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

@Environment(EnvType.CLIENT)
public class MilitiaArcherVillagerRenderer extends AgeableMobRenderer<MilitiaArcherVillager, VillagerRenderState, VillagerModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/villager/villager.png");
    public static final CustomHeadLayer.Transforms HEAD_TRANSFORMATION = new CustomHeadLayer.Transforms(-0.1171875F, -0.07421875F, 1.0F);

    public MilitiaArcherVillagerRenderer(EntityRendererProvider.Context context) {
        super(
                context,
                new VillagerModel(context.bakeLayer(ModelLayers.VILLAGER)),
                new VillagerModel(context.bakeLayer(ModelLayers.VILLAGER_BABY)),
                0.5F
        );
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), HEAD_TRANSFORMATION));
        this.addLayer(new VillagerProfessionLayer<>(this, context.getResourceManager(), "villager"));
        this.addLayer(new CrossedArmsItemLayer<>(this));
    }

    public ResourceLocation getTextureLocation(VillagerRenderState villagerEntityRenderState) {
        return TEXTURE;
    }

    protected float getShadowRadius(VillagerRenderState villagerEntityRenderState) {
        float f = super.getShadowRadius(villagerEntityRenderState);
        return villagerEntityRenderState.isBaby ? f * 0.5F : f;
    }

    public VillagerRenderState createRenderState() {
        return new VillagerRenderState();
    }
    @Override
    public void extractRenderState(MilitiaArcherVillager villagerEntity, VillagerRenderState villagerEntityRenderState, float f) {
        super.extractRenderState(villagerEntity, villagerEntityRenderState, f);
        HoldingEntityRenderState.extractHoldingEntityRenderState(villagerEntity, villagerEntityRenderState, this.itemModelResolver);
        Holder<VillagerType> villagerTypeEntry = BuiltInRegistries.VILLAGER_TYPE.getOrThrow(VillagerType.SAVANNA);
        Holder<VillagerProfession> villagerProfessionEntry = BuiltInRegistries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.FLETCHER);
        // 设置村民为制箭师（Fletcher）和热带草原（Savanna）
        villagerEntityRenderState.villagerData = new VillagerData(
                villagerTypeEntry, // 热带草原类型
                villagerProfessionEntry,
                1
        );
    }
}
