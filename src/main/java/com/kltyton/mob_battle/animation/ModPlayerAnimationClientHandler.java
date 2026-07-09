package com.kltyton.mob_battle.animation;

import com.kltyton.mob_battle.config.MobBattleClientConfig;
import com.zigythebird.playeranim.PlayerAnimLibMod;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;

/**
 * PlayerAnimationLib 的客户端调用入口。
 * <p>
 * 这里只操作客户端上的玩家动画控制器；服务器逻辑请使用 ModPlayerAnimationServerHandler 发送同步包。
 */
public final class ModPlayerAnimationClientHandler {
    /**
     * 在指定客户端玩家实体上播放一次已加载的动画。
     *
     * @return 找到控制器且成功触发动画时返回 true
     */
    public static boolean play(Avatar avatar, Identifier animationId) {
        PlayerAnimationController controller = getController(avatar);

        if (controller != null) {
            if (MobBattleClientConfig.showFirstPerson()) {
                controller.setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL);
                controller.setFirstPersonConfiguration(
                        new FirstPersonConfiguration()
                                .setShowArmor(MobBattleClientConfig.showChestArmor())
                                .setShowRightArm(MobBattleClientConfig.showRightArmor())
                                .setShowLeftArm(MobBattleClientConfig.showLeftArmor())
                                .setShowRightItem(MobBattleClientConfig.showRightHandItem())
                                .setShowLeftItem(MobBattleClientConfig.showLeftHandItem())
                );
            }

            return controller.triggerAnimation(animationId);
        }
        return false;
    }
    /**
     * 停止指定客户端玩家实体当前由控制器触发的动画。
     */
    public static boolean stop(Avatar avatar) {
        PlayerAnimationController controller = getController(avatar);
        if (controller == null) {
            return false;
        }

        boolean stoppedTriggeredAnimation = controller.stopTriggeredAnimation();
        controller.stop();
        return stoppedTriggeredAnimation;
    }

    /**
     * 读取 PlayerAnimationLib 自动挂载到玩家身上的默认动画控制器。
     */
    private static PlayerAnimationController getController(Avatar avatar) {
        IAnimation animationLayer = PlayerAnimationAccess.getPlayerAnimationLayer(
                avatar,
                PlayerAnimLibMod.ANIMATION_LAYER_ID
        );
        return animationLayer instanceof PlayerAnimationController controller ? controller : null;
    }
}
