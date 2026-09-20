package com.kltyton.mob_battle.client.camera.imbalance;

/**
 * 失衡镜头的无副作用数值策略。
 *
 * <p>该类不引用 Minecraft 客户端类型，因此可由服务端 GameTest 验证；真正的随机数仍只在
 * {@code ImbalanceCameraEffect} 的客户端 tick 中产生。</p>
 */
public final class ImbalanceCameraPolicy {
    /** 每个客户端 tick 向垂直俯视方向推进的角速度。 */
    public static final float DOWNWARD_PITCH_SPEED_DEGREES_PER_TICK = 30.0F;

    /** 每个客户端 tick 的左右随机偏移绝对上限。 */
    public static final float MAX_HORIZONTAL_OFFSET_DEGREES = 45.0F;

    private ImbalanceCameraPolicy() {
    }

    /**
     * 客户端镜头扰动的生命周期状态；不持有 Minecraft 对象，便于 GameTest 验证状态转换。
     *
     * <p>原始 pitch 只在一次有效激活的首个 tick 捕获，失效时由调用方恢复后清空；yaw 偏移
     * 始终保存为有限值，避免异常输入污染下一次生命周期。</p>
     */
    public static final class State {
        private boolean originalPitchCaptured;
        private float originalPitch;
        private float appliedYawOffset;

        /** 返回当前生命周期是否已经捕获原始 pitch。 */
        public boolean hasOriginalPitch() {
            return originalPitchCaptured;
        }

        /** 首次调用捕获进入失衡滑翔前的 pitch，后续调用保持同一快照。 */
        public float captureOriginalPitch(float currentPitch) {
            if (!originalPitchCaptured) {
                originalPitch = finiteOrZero(currentPitch);
                originalPitchCaptured = true;
            }
            return originalPitch;
        }

        /** 返回需要在生命周期结束时恢复的有限 pitch。 */
        public float originalPitch() {
            return originalPitch;
        }

        /** 返回当前生命周期最后一次应用的 yaw 偏移。 */
        public float appliedYawOffset() {
            return appliedYawOffset;
        }

        /** 保存有限的 yaw 偏移，异常值按零处理。 */
        public void setAppliedYawOffset(float appliedYawOffset) {
            this.appliedYawOffset = finiteOrZero(appliedYawOffset);
        }

        /** 在调用方恢复 pitch 和撤销 yaw 后清空本次生命周期快照。 */
        public void clear() {
            originalPitchCaptured = false;
            originalPitch = 0.0F;
            appliedYawOffset = 0.0F;
        }
    }

    /** 只有同时满足失衡效果和鞘翅滑翔状态时才允许产生镜头扰动。 */
    public static boolean shouldDisturb(boolean hasImbalanceEffect, boolean isFallFlying) {
        return hasImbalanceEffect && isFallFlying;
    }

    /** 将 pitch 以固定高速推向 +90 度，并把异常输入收敛到安全值。 */
    public static float nextPitch(float currentPitch) {
        if (!Float.isFinite(currentPitch)) {
            return 90.0F;
        }
        return Math.max(-90.0F, Math.min(90.0F,
                currentPitch + DOWNWARD_PITCH_SPEED_DEGREES_PER_TICK));
    }

    /** 将客户端随机数映射为有界的左右偏移；NaN/无穷输入退化为零偏移。 */
    public static float randomHorizontalOffset(float randomUnit) {
        if (!Float.isFinite(randomUnit)) {
            return 0.0F;
        }
        float boundedUnit = Math.max(0.0F, Math.min(1.0F, randomUnit));
        return (boundedUnit * 2.0F - 1.0F) * MAX_HORIZONTAL_OFFSET_DEGREES;
    }

    private static float finiteOrZero(float value) {
        return Float.isFinite(value) ? value : 0.0F;
    }
}
