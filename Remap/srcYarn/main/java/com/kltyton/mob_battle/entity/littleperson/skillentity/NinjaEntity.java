package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class NinjaEntity extends RequestedLittlePersonEntity {
    private static final int CLONES_PER_VARIANT = 20;
    private static final int CLONE_MAX_AGE = 25;
    private int cloneSequenceVariant;
    private int cloneSequenceSpawned;
    private int cloneSequenceDelay;

    public NinjaEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, 5);
        this.healPerSecond = 3.0F;
        this.blockChance = 10;
        this.blockDamageCap = 100.0F;
        this.autoSkillRange = 8.0D;
        setCooldownSeconds(8, 10, 25, 25, 15);
    }

    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
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
                this.setAiDisabled(false);
                startMovingHitbox(30, 85.0F);
            }
            default -> {
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient() && this.getWorld() instanceof ServerWorld world) {
            tickCloneSequence(world);
        }
    }

    private void summonVisualClones() {
        if (this.getTarget() == null || !(this.getWorld() instanceof ServerWorld)) {
            return;
        }
        this.cloneSequenceVariant = 1;
        this.cloneSequenceSpawned = 0;
        this.cloneSequenceDelay = 0;
    }

    private void tickCloneSequence(ServerWorld world) {
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
            target.damage(world, this.getDamageSources().indirectMagic(this, this), 90.0F);
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

    private void spawnClone(ServerWorld world, LivingEntity target, int variant) {
        SkillVisualEntity clone = ModEntities.NINJA_CLONE.create(world, SpawnReason.MOB_SUMMONED);
        if (clone == null) {
            return;
        }
        Vec3d direction = randomCloneDirection(variant).normalize();
        Vec3d position = target.getPos().subtract(direction.multiply(1.8D)).add(0.0D, 1.0D, 0.0D);
        float yaw = (float)(MathHelper.atan2(direction.z, direction.x) * MathHelper.DEGREES_PER_RADIAN) - 90.0F;
        float pitch = (float)(-MathHelper.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * MathHelper.DEGREES_PER_RADIAN);
        clone.refreshPositionAndAngles(position.x, position.y, position.z, yaw, pitch);
        clone.configure(this, 0.0F, -1, CLONE_MAX_AGE, 0.0D, variant);
        world.spawnEntity(clone);
    }

    private Vec3d randomCloneDirection(int variant) {
        double yaw = this.random.nextDouble() * Math.PI * 2.0D;
        if (variant == 2) {
            return new Vec3d(Math.cos(yaw), 0.0D, Math.sin(yaw));
        }
        if (variant == 3) {
            double pitch = (this.random.nextDouble() * 2.0D - 1.0D) * Math.PI * 0.5D;
            Vec3d forward = this.getRotationVec(1.0F);
            Vec3d horizontal = new Vec3d(forward.x, 0.0D, forward.z);
            if (horizontal.lengthSquared() < 1.0E-4D) {
                horizontal = Vec3d.fromPolar(0.0F, this.getYaw());
            }
            horizontal = horizontal.normalize();
            return horizontal.multiply(Math.cos(pitch)).add(0.0D, Math.sin(pitch), 0.0D);
        }
        double y = this.random.nextDouble() * 2.0D - 1.0D;
        double horizontal = Math.sqrt(Math.max(0.0D, 1.0D - y * y));
        return new Vec3d(horizontal * Math.cos(yaw), y, horizontal * Math.sin(yaw));
    }
}
