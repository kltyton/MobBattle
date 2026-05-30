package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NinjaEntity extends RequestedLittlePersonEntity {
    private static final int CLONES_PER_VARIANT = 20;
    private static final int CLONE_MAX_AGE = 25;
    private int cloneSequenceVariant;
    private int cloneSequenceSpawned;
    private int cloneSequenceDelay;

    public NinjaEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 5);
        this.healPerSecond = 3.0F;
        this.blockChance = 10;
        this.blockDamageCap = 100.0F;
        this.autoSkillRange = 8.0D;
        setCooldownSeconds(8, 10, 25, 25, 15);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return createRequestedAttributes(4000.0D, 80.0D, 0.55D, 40.0D, 0.25D);
    }

    @Override
    protected void runAttack() {
        damageTarget(80.0F, 0.0F);
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> damageTarget(120.0F, 0.0F);
            case 3 -> forwardBoxDamage(4.0D, 4.0D, 4.0D, 100.0F, 0.0F);
            case 4 -> forwardBoxDamage(4.0D, 3.0D, 3.0D, 150.0F, 0.0F);
            case 5 -> summonVisualClones();
            case 6 -> {
                this.setNoAi(false);
                startMovingHitbox(30, 85.0F);
            }
            default -> {
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel world) {
            tickCloneSequence(world);
        }
    }

    private void summonVisualClones() {
        if (this.getTarget() == null || !(this.level() instanceof ServerLevel)) {
            return;
        }
        this.cloneSequenceVariant = 1;
        this.cloneSequenceSpawned = 0;
        this.cloneSequenceDelay = 0;
    }

    private void tickCloneSequence(ServerLevel world) {
        if (this.cloneSequenceVariant <= 0) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            clearCloneSequence();
            return;
        }
        if (this.cloneSequenceDelay > 0) {
            this.cloneSequenceDelay--;
            return;
        }
        if (this.cloneSequenceSpawned < CLONES_PER_VARIANT) {
            spawnClone(world, target, this.cloneSequenceVariant);
            target.hurtServer(world, this.damageSources().indirectMagic(this, this), 90.0F);
            this.cloneSequenceSpawned++;
            if (this.cloneSequenceSpawned >= CLONES_PER_VARIANT) {
                this.cloneSequenceDelay = CLONE_MAX_AGE + 2;
            }
            return;
        }
        if (this.cloneSequenceVariant < 3) {
            this.cloneSequenceVariant++;
            this.cloneSequenceSpawned = 0;
            this.cloneSequenceDelay = 0;
        } else {
            clearCloneSequence();
        }
    }

    private void clearCloneSequence() {
        this.cloneSequenceVariant = 0;
        this.cloneSequenceSpawned = 0;
        this.cloneSequenceDelay = 0;
    }

    private void spawnClone(ServerLevel world, LivingEntity target, int variant) {
        SkillVisualEntity clone = ModEntities.NINJA_CLONE.create(world, EntitySpawnReason.MOB_SUMMONED);
        if (clone == null) {
            return;
        }
        Vec3 direction = randomCloneDirection(variant).normalize();
        Vec3 position = target.position().subtract(direction.scale(1.8D)).add(0.0D, 1.0D, 0.0D);
        float yaw = (float)(Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
        float pitch = (float)(-Mth.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * Mth.RAD_TO_DEG);
        clone.snapTo(position.x, position.y, position.z, yaw, pitch);
        clone.configure(this, 0.0F, -1, CLONE_MAX_AGE, 0.0D, variant);
        world.addFreshEntity(clone);
    }

    private Vec3 randomCloneDirection(int variant) {
        double yaw = this.random.nextDouble() * Math.PI * 2.0D;
        if (variant == 2) {
            return new Vec3(Math.cos(yaw), 0.0D, Math.sin(yaw));
        }
        if (variant == 3) {
            double pitch = (this.random.nextDouble() * 2.0D - 1.0D) * Math.PI * 0.5D;
            Vec3 forward = this.getViewVector(1.0F);
            Vec3 horizontal = new Vec3(forward.x, 0.0D, forward.z);
            if (horizontal.lengthSqr() < 1.0E-4D) {
                horizontal = Vec3.directionFromRotation(0.0F, this.getYRot());
            }
            horizontal = horizontal.normalize();
            return horizontal.scale(Math.cos(pitch)).add(0.0D, Math.sin(pitch), 0.0D);
        }
        double y = this.random.nextDouble() * 2.0D - 1.0D;
        double horizontal = Math.sqrt(Math.max(0.0D, 1.0D - y * y));
        return new Vec3(horizontal * Math.cos(yaw), y, horizontal * Math.sin(yaw));
    }
}
