package com.kltyton.mob_battle.entity.littleperson.skillentity.requested;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.utils.TaskSchedulerUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SevenHarvestLittlePersonEntity extends RequestedTaskLittlePersonEntity {
    private static final double MIN_KITE_DISTANCE = 14.0D;
    private static final double MAX_KITE_DISTANCE = 24.0D;
    private int normalAttackCooldown;

    public SevenHarvestLittlePersonEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 4);
        this.healPerSecond = 3.0F;
        this.autoSkillRange = 24.0D;
        setCooldownSeconds(12, 15, 15, 3);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return requestedAttributes(3000.0D, 75.0D, 0.50D, 40.0D, 0.25D);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            return;
        }
        if (this.normalAttackCooldown > 0) {
            this.normalAttackCooldown--;
        }
        LivingEntity target = this.getTarget();
        if (target == null || !isValidSummonTarget(target) || this.hasSkill()) {
            return;
        }
        double distance = this.distanceTo(target);
        if (distance < MIN_KITE_DISTANCE) {
            Vec3 away = this.position().subtract(target.position());
            Vec3 horizontal = new Vec3(away.x, 0.0D, away.z);
            if (horizontal.lengthSqr() > 1.0E-4D) {
                horizontal = horizontal.normalize();
                this.getNavigation().moveTo(this.getX() + horizontal.x * 6.0D, this.getY(), this.getZ() + horizontal.z * 6.0D, 1.25D);
                this.push(horizontal.x * 0.18D, 0.02D, horizontal.z * 0.18D);
                this.hurtMarked = true;
            }
        }
        if (distance > MAX_KITE_DISTANCE) {
            this.getNavigation().moveTo(target, 0.9D);
        }
        if (distance > 8.0D && distance <= MAX_KITE_DISTANCE && this.normalAttackCooldown == 0) {
            performNormalAttack();
            this.normalAttackCooldown = 30;
        }
    }

    @Override
    protected void performNormalAttack() {
        this.setHasSkill(true);
        this.setNormalAttackKnockbackAllowed(false);
        this.setNoAi(false);
        this.triggerAnim("skill_controller", this.random.nextFloat() < 0.2F ? "attack_1" : "attack");
    }

    @Override
    protected boolean canUseNormalAttack(LivingEntity target) {
        double distance = this.distanceTo(target);
        return !this.hasSkill() && distance > 8.0D && distance <= MAX_KITE_DISTANCE;
    }

    @Override
    protected double skillRange(String skillName) {
        return "attack5".equals(skillName) ? 1.5D : 24.0D;
    }

    @Override
    protected void runAttack() {
        shootSkillProjectileAtTarget(ModEntities.SEVEN_HARVEST_BULLET, 75.0F, 0.0F, 1.9D, 80, false, false, false, 0.0D);
    }

    @Override
    protected void runAttackVariant(int variant) {
        shootLittleArrowAtTarget(ModEntities.LITTLE_ARROW, 8.0F, 1.6F);
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> {
                for (float yaw : new float[]{-8.0F, 0.0F, 8.0F}) {
                    Vec3 direction = Vec3.directionFromRotation(0.0F, this.getYRot() + yaw).normalize();
                    spawnSkillProjectile(ModEntities.SEVEN_HARVEST_EXPLOSIVE_BULLET, this.getEyePosition().add(direction.scale(0.8D)),
                            direction.scale(1.7D), 80.0F, 0.0F, 70, false, false, true, 2.5D);
                }
            }
            case 3 -> {
                for (int i = 0; i < 24; i++) {
                    TaskSchedulerUtil.runLater(i * 2, () -> {
                        if (!this.isRemoved() && this.isAlive()) {
                            shootSkillProjectileAtTarget(ModEntities.SEVEN_HARVEST_BULLET, 80.0F, 0.0F, 2.6D, 50, false, false, false, 0.0D);
                        }
                    });
                }
            }
            case 4 -> {
                LivingEntity target = this.getTarget();
                if (target == null || !isValidSummonTarget(target)) {
                    return;
                }
                for (int i = 0; i < 10; i++) {
                    TaskSchedulerUtil.runLater(i * 3, () -> {
                        LivingEntity current = this.getTarget();
                        if (current == null || !isValidSummonTarget(current) || this.isRemoved()) {
                            return;
                        }
                        double x = current.getX() + (this.random.nextDouble() - 0.5D) * 4.0D;
                        double z = current.getZ() + (this.random.nextDouble() - 0.5D) * 4.0D;
                        Vec3 position = new Vec3(x, current.getY() + current.getBbHeight() + 2.0D, z);
                        spawnSkillProjectile(ModEntities.SEVEN_HARVEST_BULLET, position, new Vec3(0.0D, -1.2D, 0.0D),
                                95.0F, 0.0F, 40, false, false, false, 0.0D);
                    });
                }
            }
            case 5 -> damageTargetNoInvulnerability(100.0F);
            default -> {
            }
        }
    }
}
