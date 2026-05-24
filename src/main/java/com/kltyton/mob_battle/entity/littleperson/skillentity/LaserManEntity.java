package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LaserManEntity extends RequestedLittlePersonEntity {
    public LaserManEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, 6);
        this.healPerSecond = 3.0F;
        this.blockChance = 20;
        this.blockDamageCap = 100.0F;
        this.autoSkillRange = 16.0D;
        setCooldownSeconds(5, 25, 10, 10, 15, 2);
    }

    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return createRequestedAttributes(4500.0D, 90.0D, 0.5D, 40.0D, 0.25D);
    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        if (!(target instanceof LivingEntity living) || living.isTeammate(this) || !canSkill()) {
            return false;
        }
        double distance = this.distanceTo(living);
        if (distance <= 8.0D) {
            for (int attack : new int[]{6, 5, 3, 2}) {
                String skillName = "attack" + attack;
                if (canUseSkill(skillName, living)) {
                    performSkill(skillName);
                    return true;
                }
            }
            if (canUseNormalAttack(living)) {
                performNormalAttack();
                return true;
            }
            if (canUseSkill("attack4", living)) {
                performSkill("attack4");
                return true;
            }
            return false;
        }
        if (canUseSkill("attack7", living)) {
            performSkill("attack7");
            return true;
        }
        if (canUseSkill("attack4", living)) {
            performSkill("attack4");
            return true;
        }
        return false;
    }

    @Override
    protected boolean canUseSkill(String skillName, LivingEntity target) {
        if (("attack2".equals(skillName) || "attack3".equals(skillName) || "attack5".equals(skillName) || "attack6".equals(skillName))
                && this.distanceTo(target) > 8.0D) {
            return false;
        }
        if ("attack4".equals(skillName) && this.distanceTo(target) > 15.0D) {
            return false;
        }
        if ("attack7".equals(skillName) && this.distanceTo(target) <= 8.0D) {
            return false;
        }
        return super.canUseSkill(skillName, target);
    }

    @Override
    protected double skillRange(String skillName) {
        return switch (skillName) {
            case "attack4" -> 15.0D;
            case "attack7" -> 16.0D;
            default -> 8.0D;
        };
    }

    @Override
    protected void runAttack() {
        damageTarget(90.0F, 0.0F);
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> damageTarget(100.0F, 0.0F);
            case 3 -> coneDamage(3.0D, 100.0F, 90.0F, 0.0F);
            case 4 -> damageTarget(150.0F, 0.0F);
            case 5 -> {
                this.setAiDisabled(false);
                startMovingHitbox(30, 100.0F);
            }
            case 6 -> areaDamage(3.0D, 80.0F, 0.0F);
            case 7 -> shootLaser();
            default -> {
            }
        }
    }

    private void shootLaser() {
        LivingEntity target = this.getTarget();
        if (target == null || !(this.getWorld() instanceof ServerWorld world)) {
            return;
        }
        SkillProjectileEntity projectile = ModEntities.LASER.create(world, SpawnReason.MOB_SUMMONED);
        if (projectile == null) {
            return;
        }
        Vec3d start = this.getEyePos().add(this.getRotationVec(1.0F).multiply(0.8D));
        Vec3d velocity = target.getEyePos().subtract(start).normalize().multiply(1.4D);
        projectile.configure(this, start, velocity, 85.0F, 0.0F, false, false, false, 60);
        world.spawnEntity(projectile);
        this.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 0.8F, 1.2F);
    }
}
