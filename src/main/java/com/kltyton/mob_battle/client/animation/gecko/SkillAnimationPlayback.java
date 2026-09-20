package com.kltyton.mob_battle.client.animation.gecko;

import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationPoint;
import com.geckolib.animation.state.AnimationTest;

/**
 * 将 GeckoLib 原生播放状态接入本模组的技能收尾回调。
 *
 * <p>不保存播放状态、不加载资源、不构造时间轴；控制器仍由 GeckoLib 独占管理。
 * 此处只负责让实体在一个触发动作结束时回传一次既有的 {@code stop}/{@code die} 指令。
 */
public final class SkillAnimationPlayback {
    private SkillAnimationPlayback() {
    }

    /**
     * 消费一个已结束的触发动作；原生停止接口的返回值保证同一次触发只消费一次。
     *
     * <p>零过渡动作走完后，控制器已不再操作骨骼，此时通过原生末帧判断收尾；
     * 有退场过渡时则使用控制器的完成判断，让 GeckoLib 继续执行自己的复位阶段。
     * 停止接口保留当前 RawAnimation，调用方可用 {@link AnimationTest#isCurrentAnimation}
     * 区分死亡动作，无需另一张“上次完成”缓存。
     */
    public static boolean consumeFinishedTriggeredAnimation(AnimationTest<?> test) {
        AnimationController<?> controller = test.controller();
        AnimationPoint point = controller.getCurrentAnimationPoint();
        boolean finished = controller.hasAnimationFinished()
                || (point != null && point.hasFinished() && !controller.isAnimatingBones());
        return finished && controller.stopTriggeredAnimation();
    }

    /** 保留首次触发尚未创建播放点的阶段，其余播放和退场均交给原生控制器。 */
    public static PlayState playTriggeredAnimationOrStop(AnimationTest<?> test) {
        if (consumeFinishedTriggeredAnimation(test)) {
            return PlayState.STOP;
        }
        return isPlayingOrPending(test.controller()) ? PlayState.CONTINUE : PlayState.STOP;
    }

    /**
     * 用本实体原生技能控制器判断主动作是否需要等待技能，不依赖服务器回包延时或墙钟窗口。
     * {@code skill_controller} 是现有实体控制器名称，不改变触发与网络协议。
     */
    public static boolean hasActiveSkill(AnimationTest<?> test) {
        AnimationController<?> controller = test.manager().getAnimationControllers().get("skill_controller");
        return controller != null && isPlayingOrPending(controller) && !controller.hasAnimationFinished();
    }

    private static boolean isPlayingOrPending(AnimationController<?> controller) {
        return controller.isPlayingTriggeredAnimation()
                || (controller.getCurrentRawAnimation() != null
                && controller.getPlayState() == PlayState.CONTINUE
                && controller.getCurrentAnimationPoint() == null);
    }
}
