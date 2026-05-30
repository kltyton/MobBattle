package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BloodManEntity extends RequestedLittlePersonEntity {
    public BloodManEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 5);
        this.healPerSecond = 10.0F;
        this.blockChance = 30;
        this.blockDamageCap = 150.0F;
        this.autoSkillRange = 10.0D;
        setCooldownSeconds(10, 20, 15, 20, 30);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return createRequestedAttributes(5500.0D, 95.0D, 0.5D, 40.0D, 0.0D);
    }

    @Override
    protected void runAttack() {
        damageTarget(95.0F, 0.0F);
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> forwardBoxDamage(2.0D, 2.0D, 2.0D, 150.0F, 0.0F);
            case 3 -> areaDamage(2.5D, 200.0F, 0.0F);
            case 4 -> {
                for (LivingEntity target : nearestTargets(5.0D, 64)) {
                    damagePhysical(target, 95.0F);
                    addPiercing(target, 4, 1);
                }
            }
            case 5 -> damageTarget(100.0F, 0.0F);
            case 6 -> shootBloodBlades();
            default -> {
            }
        }
    }

    private void shootBloodBlades() {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        for (float yawOffset : new float[]{-10.0F, 0.0F, 10.0F}) {
            SkillProjectileEntity projectile = ModEntities.BLOOD_SWORD_ENERGY.create(world, EntitySpawnReason.MOB_SUMMONED);
            if (projectile == null) {
                continue;
            }
            Vec3 direction = Vec3.directionFromRotation(0.0F, this.getYRot() + yawOffset).normalize();
            Vec3 start = this.getEyePosition().add(direction.scale(0.8D));
            projectile.configure(this, start, direction.scale(0.85D), 100.0F, 0.0F, true, true, false, 45);
            world.addFreshEntity(projectile);
        }
        this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 0.7F);
    }
}
