package com.kltyton.mob_battle.entity.highbird.egg;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class HighbirdEggEntityModel extends GeoModel<HighbirdEggEntity> {
    // 不同状态的模型资源
    private static final Identifier MODEL_NORMAL = Identifier.of(Mob_battle.MOD_ID, "highbird_egg");
    private static final Identifier MODEL_HOT = Identifier.of(Mob_battle.MOD_ID, "highbird_egg_hot");
    private static final Identifier MODEL_COLD = Identifier.of(Mob_battle.MOD_ID, "highbird_egg_cold");

    // 不同状态的纹理资源
    private static final Identifier TEXTURE_NORMAL = Identifier.of(Mob_battle.MOD_ID, "textures/entity/highbird_egg.png");
    private static final Identifier TEXTURE_HOT = Identifier.of(Mob_battle.MOD_ID, "textures/entity/highbird_egg_hot.png");
    private static final Identifier TEXTURE_COLD = Identifier.of(Mob_battle.MOD_ID, "textures/entity/highbird_egg_cold.png");

    // 不同状态的动画资源
    private static final Identifier ANIMATION_NORMAL = Identifier.of(Mob_battle.MOD_ID, "highbird_egg");
    private static final Identifier ANIMATION_HOT = Identifier.of(Mob_battle.MOD_ID, "highbird_egg_hot");
    private static final Identifier ANIMATION_COLD = Identifier.of(Mob_battle.MOD_ID, "highbird_egg_cold");
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return getResourceForStatus(renderState,
                MODEL_NORMAL, MODEL_HOT, MODEL_COLD);
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return getResourceForStatus(renderState,
                TEXTURE_NORMAL, TEXTURE_HOT, TEXTURE_COLD);
    }

    @Override
    public Identifier getAnimationResource(HighbirdEggEntity entity) {
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
    private Identifier getResourceForStatus(GeoRenderState renderState,
                                            Identifier normal,
                                            Identifier hot,
                                            Identifier cold) {
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
