package com.kltyton.mob_battle.entity.player;

import net.minecraft.world.item.ItemStack;

public interface IPlayerEntityAccessor {
    boolean isUsingGeckoLib();
    void setUseGeckoLib(boolean use);

    /** 返回玩家当前狙击枪左键瞄准状态；状态只属于该玩家实体。 */
    boolean isVsSnipeLeftClicking();

    /** 设置玩家当前实际狙击枪栈的左键瞄准状态。 */
    void setVsSnipeLeftClicking(ItemStack stack, boolean active);

    /** 清除狙击枪的全部瞬态状态。 */
    void clearVsSnipeState();

    /** 重置指定狙击枪栈的装填音效阶段。 */
    void resetVsSnipeLoading(ItemStack stack);

    /** 返回指定狙击枪栈是否已经播放过开始装填音效。 */
    boolean isVsSnipeCharging(ItemStack stack);

    /** 返回指定狙击枪栈是否已经完成装填阶段。 */
    boolean isVsSnipeLoaded(ItemStack stack);

    /** 更新指定狙击枪栈的开始装填音效阶段。 */
    void setVsSnipeCharging(ItemStack stack, boolean charging);

    /** 更新指定狙击枪栈的完成装填阶段。 */
    void setVsSnipeLoaded(ItemStack stack, boolean loaded);
}
