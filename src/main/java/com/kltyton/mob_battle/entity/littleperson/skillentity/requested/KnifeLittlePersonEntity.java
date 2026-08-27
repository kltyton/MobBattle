package com.kltyton.mob_battle.entity.littleperson.skillentity.requested;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.event.scheduler.ServerTickScheduler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class KnifeLittlePersonEntity extends RequestedTaskLittlePersonEntity {
    public KnifeLittlePersonEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 6);
        this.healPerSecond = 3.0F;
        this.blockChance = 10;
        this.blockDamageCap = 80.0F;
        this.autoSkillRange = 8.0D;
        setCooldownSeconds(8, 8, 20, 23, 6, 14);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return requestedAttributes(4000.0D, 40.0D, 0.55D, 40.0D, 0.20D);
    }

    @Override
    protected void runAttack() {
        damageTargetNoInvulnerability(40.0F);
        forwardBoxDamage(4.0D, 2.2D, 2.0D, 40.0F, 0.0F);
    }

    @Override
    protected boolean canUseNormalAttack(LivingEntity target) {
        return !this.hasSkill() && this.distanceTo(target) <= 4.5D;
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> shootSkillProjectileAtTarget(ModEntities.KNIFE_PROJECTILE, 80.0F, 0.0F, 2.2D, 70, false, false, false, 0.0D);
            case 3 -> {
                damageTargetNoInvulnerability(90.0F);
                forwardBoxDamage(4.5D, 2.5D, 2.0D, 90.0F, 0.0F);
            }
            case 4 -> {
                lungeTowardTarget(1.5D, 1.35D);
                startMovingHitbox(25, 80.0F);
            }
            case 5 -> forwardBoxDamage(6.0D, 3.9D, 2.0D, 150.0F, 0.0F);
            case 6 -> {
                damageTargetNoInvulnerability(100.0F);
                forwardBoxDamage(5.0D, 3.0D, 2.0D, 100.0F, 0.0F);
            }
            case 7 -> {
                damageTargetNoInvulnerability(100.0F);
                forwardBoxDamage(4.5D, 2.8D, 2.0D, 100.0F, 0.0F);
            }
            default -> {
            }
        }
    }

    @Override
    public void performSkill(String skill, boolean isAfterSkill) {
        super.performSkill(skill, isAfterSkill);
        if ("attack6".equals(skill)) {
            ServerTickScheduler.schedule(this.level().getServer(), 12, () -> {
                if (!this.isRemoved() && this.isAlive() && this.hasSkill()) {
                    runSkill(6, 0);
                }
            });
        }
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel world, @NotNull DamageSource source, float amount) {
        if (this.random.nextFloat() < 0.30F && !this.hasSkill() && !isEnvironmentDamage(source)) {
            this.setHasSkill(true);
            this.setNoAi(false);
            this.triggerAnim("skill_controller", switch (this.random.nextInt(3)) {
                case 0 -> "miss";
                case 1 -> "miss2";
                default -> "miss3";
            });
            setSkillCooldown("attack2");
            runSkill(2, 0);
            return false;
        }
        return super.hurtServer(world, source, amount);
    }
}
