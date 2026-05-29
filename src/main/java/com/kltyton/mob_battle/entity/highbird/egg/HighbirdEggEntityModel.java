package com.kltyton.mob_battle.entity.highbird.egg;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class HighbirdEggEntityModel extends GeoModel<HighbirdEggEntity> {
    // 不同状态的模型资源
    private static final ResourceLocation MODEL_NORMAL = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "highbird_egg");
    private static final ResourceLocation MODEL_HOT = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "highbird_egg_hot");
    private static final ResourceLocation MODEL_COLD = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "highbird_egg_cold");

    // 不同状态的纹理资源
    private static final ResourceLocation TEXTURE_NORMAL = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/highbird_egg.png");
    private static final ResourceLocation TEXTURE_HOT = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/highbird_egg_hot.png");
    private static final ResourceLocation TEXTURE_COLD = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/highbird_egg_cold.png");

    // 不同状态的动画资源
    private static final ResourceLocation ANIMATION_NORMAL = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "highbird_egg");
    private static final ResourceLocation ANIMATION_HOT = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "highbird_egg_hot");
    private static final ResourceLocation ANIMATION_COLD = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "highbird_egg_cold");
    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return getResourceForStatus(renderState,
                MODEL_NORMAL, MODEL_HOT, MODEL_COLD);
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return getResourceForStatus(renderState,
                TEXTURE_NORMAL, TEXTURE_HOT, TEXTURE_COLD);
    }

    @Override
    public ResourceLocation getAnimationResource(HighbirdEggEntity entity) {
        // 根据实体状态返回对应的动画
        return switch (entity.getStatus()) {
            case HighbirdEggEntity.HOT_STATUS -> ANIMATION_HOT;
            case HighbirdEggEntity.COLD_STATUS -> ANIMATION_COLD;
            default -> ANIMATION_NORMAL; // 默认为NORMAL
        };
    }
    /**
     * 根据状态获取对应资源
     */
    private ResourceLocation getResourceForStatus(GeoRenderState renderState,
                                            ResourceLocation normal,
                                            ResourceLocation hot,
                                            ResourceLocation cold) {
        // 从渲染状态获取状态数据
        String status = renderState.getOrDefaultGeckolibData(
                HighbirdEggEntity.STATUS_TICKET,
                HighbirdEggEntity.NORMAL_STATUS
        );

        // 返回对应状态的资源
        if (HighbirdEggEntity.HOT_STATUS.equals(status)) {
            return hot;
        } else if (HighbirdEggEntity.COLD_STATUS.equals(status)) {
            return cold;
        }
        return normal;
    }
}
