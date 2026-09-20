package com.kltyton.mob_battle.client.model.littleperson;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.littleperson.FufuAppearance;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

/** 统一切换小人的模型、纹理和动画；名称来自实体同步数据，不依赖名牌是否可见。 */
public class LittlePersonAppearanceModel<T extends LivingEntity & GeoAnimatable> extends GeoModel<T> {
    private static final DataTicket<Boolean> FUFU = DataTicket.create("mob_battle_fufu_appearance", Boolean.class);
    public String name;

    public LittlePersonAppearanceModel(String name) {
        this.name = name;
    }

    @Override
    public void addAdditionalStateData(T animatable, @Nullable Object relatedObject, GeoRenderState renderState) {
        var manager = animatable.getAnimatableInstanceCache().getManagerForId(animatable.getId());
        boolean desired = !FufuAppearance.resourcePath(name, FufuAppearance.isNamed(animatable.getCustomName())).equals(name);
        Boolean previous = manager.getAnimatableData(FUFU);
        boolean active = desired;
        if (previous != null && previous != desired) {
            // 技能关键帧可能驱动服务端结算；让当前触发动作结束后再换骨架，禁止重播关键帧。
            boolean playingAction = manager.getAnimationControllers().values().stream()
                    .anyMatch(controller -> controller.isPlayingTriggeredAnimation() && !controller.hasAnimationFinished());
            if (playingAction) {
                active = previous;
            } else {
                // 两套骨架名称不同，清除旧的循环动画缓存，下一帧从对应动画资源重新构建。
                manager.getAnimationControllers().values().forEach(controller -> controller.reset());
            }
        }
        manager.setAnimatableData(FUFU, active);
        renderState.addGeckolibData(FUFU, active);
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return resource(FufuAppearance.resourcePath(name, renderState.getOrDefaultGeckolibData(FUFU, false)));
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return resource("textures/entity/little_person/"
                + FufuAppearance.resourcePath(name, renderState.getOrDefaultGeckolibData(FUFU, false)) + ".png");
    }

    @Override
    public Identifier getAnimationResource(T animatable) {
        Boolean active = animatable.getAnimatableInstanceCache().getDataPoint(animatable.getId(), FUFU);
        return resource(FufuAppearance.resourcePath(name,
                active != null ? active : FufuAppearance.isNamed(animatable.getCustomName())));
    }

    private static Identifier resource(String path) {
        return Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, path);
    }
}
