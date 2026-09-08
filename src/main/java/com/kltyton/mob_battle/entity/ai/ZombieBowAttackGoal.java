package com.kltyton.mob_battle.entity.ai;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * 僵尸弓箭 AI：沿用原版拉弓状态机，但把发射阶段委托给统一的多发射逻辑。
 * 该入口必须只使用实体当前手持的武器，避免装备被命令替换后又回退到默认弓。
 */
public class ZombieBowAttackGoal<T extends Monster & RangedAttackMob> extends RangedBowAttackGoal<T> {
    private final T mob;

    public ZombieBowAttackGoal(T mob, double speedModifier, int attackIntervalMin, float attackRadius) {
        super(mob, speedModifier, attackIntervalMin, attackRadius);
        this.mob = mob;
    }

    @Override
    protected boolean isHoldingBow() {
        return this.mob.getMainHandItem().getItem() instanceof BowItem
                && this.mob.canUseNonMeleeWeapon(this.mob.getMainHandItem());
    }

    /**
     * 使用 Mojang 的投射物计数与散布处理，逐发创建箭实体；不按附魔等级预分配固定数组。
     */
    public static <T extends Monster> void shootProjectiles(T mob, LivingEntity target, float power) {
        if (!(mob.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        InteractionHand hand = mob.getMainHandItem().getItem() instanceof BowItem
                ? InteractionHand.MAIN_HAND
                : InteractionHand.OFF_HAND;
        ItemStack bow = mob.getItemInHand(hand);
        if (!(bow.getItem() instanceof BowItem)) {
            return;
        }

        ItemStack projectile = mob.getProjectile(bow);
        int shotCount = Math.min(
                Enchantment.MAX_LEVEL,
                EnchantmentHelper.processProjectileCount(serverLevel, bow, mob, 1)
        );
        if (shotCount <= 0) {
            return;
        }

        float maxAngle = EnchantmentHelper.processProjectileSpread(serverLevel, bow, mob, 0.0F);
        float angleStep = shotCount == 1 ? 0.0F : 2.0F * maxAngle / (shotCount - 1);
        float angleOffset = (shotCount - 1) % 2 * angleStep / 2.0F;
        float direction = 1.0F;
        double x = target.getX() - mob.getX();
        double z = target.getZ() - mob.getZ();

        for (int index = 0; index < shotCount; index++) {
            AbstractArrow arrow = ProjectileUtil.getMobArrow(mob, projectile, power, bow);
            double y = target.getY(0.3333333333333333D) - arrow.getY();
            int halfIndex = (index + 1) / 2;
            float angle = angleOffset + direction * halfIndex * angleStep;
            direction = -direction;
            double radians = Math.toRadians(angle);
            double cosine = Math.cos(radians);
            double sine = Math.sin(radians);
            double rotatedX = x * cosine - z * sine;
            double rotatedZ = x * sine + z * cosine;
            double horizontalDistance = Math.sqrt(rotatedX * rotatedX + rotatedZ * rotatedZ);
            Projectile.spawnProjectileUsingShoot(
                    arrow,
                    serverLevel,
                    projectile,
                    rotatedX,
                    y + horizontalDistance * 0.2D,
                    rotatedZ,
                    1.6F,
                    14 - serverLevel.getDifficulty().getId() * 4
            );
        }

        mob.playSound(
                SoundEvents.SKELETON_SHOOT,
                1.0F,
                1.0F / (mob.getRandom().nextFloat() * 0.4F + 0.8F)
        );
    }
}
