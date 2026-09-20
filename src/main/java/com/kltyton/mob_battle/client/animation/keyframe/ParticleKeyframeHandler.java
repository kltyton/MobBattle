package com.kltyton.mob_battle.client.animation.keyframe;

import com.geckolib.animation.state.KeyFrameEvent;
import com.geckolib.cache.animation.keyframeevent.ParticleKeyframeData;
import net.minecraft.world.entity.LivingEntity;

/**
 * GeckoLib 粒子关键帧回调的兼容入口。
 *
 * <p>ParticleStorm 1.4.0 的 {@code AnimationTimelineMixin} 会在 GeckoLib 粒子关键帧
 * 到达时间线时自动调用 {@code GeckoLibHelper.processParticleEffect}。其
 * {@code RenderPassInfoMixin} 与 {@code GeoLocatorMixin} 会在同一渲染流程中绑定 locator，
 * 并在 {@code captureLocatorTransform} 中保存包含实体朝向的完整 locator 矩阵。因此这里
 * 不得再次生成粒子：额外发射不仅重复显示，还会绕过 locator 矩阵而丢失旋转。
 *
 * <p>保留此入口是为了兼容现有 AnimationController 的回调注册；方法本身必须保持无副作用，
 * 这样 common 实体代码可以继续注册回调，同时不会在 ParticleStorm 已经自动分发后双重发射。
 */
public final class ParticleKeyframeHandler {
    private ParticleKeyframeHandler() {
    }

    /**
     * 保留给现有 AnimationController 注册点的兼容方法；ParticleStorm 1.4.0 已负责实际分发。
     *
     * @param entity 触发动画的实体，仅为保持既有公开签名
     * @param event 粒子关键帧事件，仅为保持既有公开签名
     */
    public static void handle(LivingEntity entity, KeyFrameEvent<?, ParticleKeyframeData> event) {
        // ParticleStorm 1.4.0 的 mixin 已完成粒子分发；此处禁止重复发射。
    }
}
