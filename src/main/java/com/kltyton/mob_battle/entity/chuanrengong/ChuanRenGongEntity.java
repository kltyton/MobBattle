package com.kltyton.mob_battle.entity.chuanrengong;

import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.RequestedLittlePersonEntity;
import com.kltyton.mob_battle.entity.registry.ChuanRenGongEntityTypes;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 传仁工实体领域：使用原版骷髅弓手的跟随、瞄准和横向走位模型，
 * 技能伤害只由动画关键帧回传的稳定指令触发。
 */
public final class ChuanRenGongEntity extends RequestedLittlePersonEntity implements RangedAttackMob {
    public static final float NORMAL_ATTACK_DAMAGE = 35.0F;
    public static final float ATTACK_2_DAMAGE = 40.0F;
    public static final float ATTACK_3_DAMAGE = 80.0F;
    public static final float ATTACK_4_DAMAGE = 40.0F;
    public static final double ATTACK_2_KNOCKBACK = 5.0D;
    public static final double RAIN_HEIGHT_ABOVE_TARGET = 4.0D;

    private static final double ATTACK_2_RANGE = 1.0D;
    private static final double ATTACK_3_RANGE = 24.0D;
    private static final double ATTACK_4_RANGE = 40.0D;

    public ChuanRenGongEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 3);
        this.healPerSecond = 1.0F;
        this.autoSkillRange = ATTACK_4_RANGE;
        setCooldownSeconds(3, 10, 20);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createRequestedAttributes(1800.0D, NORMAL_ATTACK_DAMAGE, 0.35D, 40.0D, 0.0D);
    }

    @Override
    protected void registerGoals() {
        // RangedBowAttackGoal 自带骷髅式保持距离、横向走位与随机左右换向。
        this.goalSelector.addGoal(2, new RangedBowAttackGoal<>(this, 1.0D, 30, 32.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this, Player.class, 10, true, false, this::canTarget));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this, com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillager.class,
                10, true, false, this::canTarget));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
                this, AbstractGolem.class, 10, true, false, this::canTarget));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
                this, Mob.class, 5, false, false,
                (entity, world) -> entity instanceof Enemy
                        && !(entity instanceof LittlePersonEntity)
                        && canTarget(entity, world)));
    }

    private boolean canTarget(LivingEntity target, ServerLevel world) {
        return EntityQueries.isValidSummonCombatTarget(this, this.getSummonOwner(), target);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        if (target != this.getTarget() || !canSkill() || !isValidSummonTarget(target)) {
            return;
        }
        performNormalAttack();
    }

    @Override
    protected boolean tryUseSpecialSkill(LivingEntity target) {
        if (!isValidSummonTarget(target) || this.hasSkill()) {
            return false;
        }
        double distance = this.distanceTo(target);
        String skill = distance <= ATTACK_2_RANGE ? "attack2"
                : distance <= ATTACK_3_RANGE ? "attack3"
                : distance <= ATTACK_4_RANGE ? "attack4" : null;
        if (skill != null && canUseSkill(skill, target)) {
            performSkill(skill);
            return true;
        }
        return false;
    }

    @Override
    protected void runAttack() {
        shootSmallProjectile(NORMAL_ATTACK_DAMAGE, 1.8D, 0.0D, 0.0D);
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> shootSmallProjectile(ATTACK_2_DAMAGE, 1.8D, 0.0D, ATTACK_2_KNOCKBACK);
            case 3 -> shootLargeProjectile();
            case 4 -> shootRainProjectile();
            default -> {
            }
        }
    }

    /**
     * 只接受本动画实际存在的协议字符串，避免基类的通用 attackN 解析接受不存在的技能。
     */
    @Override
    public boolean handleSkillPayload(String skillName) {
        if (skillName == null) {
            return false;
        }
        return switch (skillName) {
            case "attack", "attack2", "attack3", "attack4", "stop", "stop_ai", "start_ai", "die"
                    -> super.handleSkillPayload(skillName);
            default -> false;
        };
    }

    private void shootSmallProjectile(float damage, double speed, double yOffset, double knockback) {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (target == null || !isValidSummonTarget(target)) {
            return;
        }
        Vec3 direction = target.getEyePosition().subtract(this.getEyePosition()).normalize();
        ChuanRenGongSmallProjectileEntity projectile = ChuanRenGongEntityTypes.SMALL_PROJECTILE.create(
                world, EntitySpawnReason.MOB_SUMMONED);
        if (projectile == null) {
            return;
        }
        projectile.configure(this, this.getEyePosition().add(direction.scale(0.8D)).add(0.0D, yOffset, 0.0D),
                direction.scale(speed), damage, 0.0F, false, false, false, 80);
        projectile.setKnockbackStrength(knockback);
        world.addFreshEntity(projectile);
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F,
                0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    private void shootLargeProjectile() {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (target == null || !isValidSummonTarget(target)) {
            return;
        }
        Vec3 direction = target.getEyePosition().subtract(this.getEyePosition()).normalize();
        ChuanRenGongLargeProjectileEntity projectile = ChuanRenGongEntityTypes.LARGE_PROJECTILE.create(
                world, EntitySpawnReason.MOB_SUMMONED);
        if (projectile == null) {
            return;
        }
        projectile.configure(this, this.getEyePosition().add(direction.scale(0.8D)), direction.scale(1.45D),
                ATTACK_3_DAMAGE, 0.0F, false, false, false, 100);
        world.addFreshEntity(projectile);
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F,
                0.35F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    private void shootRainProjectile() {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (target == null || !isValidSummonTarget(target)) {
            return;
        }
        ChuanRenGongSmallProjectileEntity projectile = ChuanRenGongEntityTypes.SMALL_PROJECTILE.create(
                world, EntitySpawnReason.MOB_SUMMONED);
        if (projectile == null) {
            return;
        }
        Vec3 position = new Vec3(target.getX(), target.getY() + RAIN_HEIGHT_ABOVE_TARGET,
                target.getZ());
        projectile.configure(this, position, new Vec3(0.0D, -1.2D, 0.0D), ATTACK_4_DAMAGE, 0.0F,
                false, false, false, 80);
        projectile.setNoGravity(true);
        world.addFreshEntity(projectile);
    }
}
