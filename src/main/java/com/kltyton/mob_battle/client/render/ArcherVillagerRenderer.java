package com.kltyton.mob_battle.client.render;

import com.kltyton.mob_battle.entity.WarriorVillager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

@Environment(EnvType.CLIENT)
public class WarriorVillagerRenderer extends AgeableMobEntityRenderer<WarriorVillager, VillagerEntityRenderState, VillagerResemblingModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/villager/villager.png");
    public static final HeadFeatureRenderer.HeadTransformation HEAD_TRANSFORMATION = new HeadFeatureRenderer.HeadTransformation(-0.1171875F, -0.07421875F, 1.0F);

    public WarriorVillagerRenderer(EntityRendererFactory.Context context) {
        super(
                context,
                new VillagerResemblingModel(context.getPart(EntityModelLayers.VILLAGER)),
                new VillagerResemblingModel(context.getPart(EntityModelLayers.VILLAGER_BABY)),
                0.5F
        );
        this.addFeature(new HeadFeatureRenderer<>(this, context.getEntityModels(), HEAD_TRANSFORMATION));
        this.addFeature(new VillagerClothingFeatureRenderer<>(this, context.getResourceManager(), "villager"));
        this.addFeature(new VillagerHeldItemFeatureRenderer<>(this));
    }

    public Identifier getTexture(VillagerEntityRenderState villagerEntityRenderState) {
        return TEXTURE;
    }

    protected float getShadowRadius(VillagerEntityRenderState villagerEntityRenderState) {
        float f = super.getShadowRadius(villagerEntityRenderState);
        return villagerEntityRenderState.baby ? f * 0.5F : f;
    }

    public VillagerEntityRenderState createRenderState() {
        return new VillagerEntityRenderState();
    }

    public void updateRenderState(WarriorVillager villagerEntity, VillagerEntityRenderState villagerEntityRenderState, float f) {
        super.updateRenderState(villagerEntity, villagerEntityRenderState, f);
        ItemHolderEntityRenderState.update(villagerEntity, villagerEntityRenderState, this.itemModelResolver);
/*        RegistryEntry<VillagerType> villagerTypeEntry = Registries.VILLAGER_TYPE.getOrThrow(VillagerType.SAVANNA);
        RegistryEntry<VillagerProfession> villagerProfessionEntry = Registries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.FLETCHER);
        // 设置村民为制箭师（Fletcher）和热带草原（Savanna）
        VillagerData villagerData = new VillagerData(
                villagerTypeEntry, // 热带草原类型
                villagerProfessionEntry,
                1
        );
        villagerEntityRenderState.villagerData = villagerData;*/
    }
}
