package com.kltyton.mob_battle.entity.enhancedwither;

import com.kltyton.mob_battle.entity.OwnedSummon;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnhancedWitherEntity extends WitherEntity implements OwnedSummon {
    @Nullable
    private LivingEntity summonOwner;

    public EnhancedWitherEntity(EntityType<? extends WitherEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setSummonOwner(@Nullable LivingEntity summonOwner) {
        this.summonOwner = summonOwner;
        if (summonOwner != null) {
            EntityUtil.joinSameTeam(this, summonOwner);
        }
    }

    @Nullable
    @Override
    public LivingEntity getSummonOwner() {
        return this.summonOwner;
    }

    private boolean isValidSummonTarget(LivingEntity target) {
        return EntityUtil.isValidSummonCombatTarget(this, this.summonOwner, target);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target != null && !isValidSummonTarget(target) ? null : target);
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        return isValidSummonTarget(target) && super.canTarget(target);
    }

    @Override
    protected void mobTick(ServerWorld world) {
        super.mobTick(world);
        if (this.age % 20 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(2.0F);
        }
        LivingEntity target = this.getTarget();
        if (target != null && !isValidSummonTarget(target)) {
            this.setTarget(null);
            target = null;
        }
        if (this.age % 30 == 0 && target != null && target.isAlive()) {
            Vec3d velocity = target.getEyePos().subtract(this.getEyePos()).normalize().multiply(1.5D);
            WitherSkullEntity skull = new WitherSkullEntity(world, this, velocity);
            skull.setPosition(this.getX(), this.getEyeY(), this.getZ());
            skull.setCharged(this.random.nextFloat() < 0.25F);
            world.spawnEntity(skull);
            this.playSound(SoundEvents.ENTITY_WITHER_SHOOT, 1.0F, 1.0F);
        }
    }

    public static DefaultAttributeContainer.Builder createEnhancedWitherAttributes() {
        return WitherEntity.createWitherAttributes()
                .add(EntityAttributes.MAX_HEALTH, 1500.0D)
                .add(EntityAttributes.FOLLOW_RANGE, 48.0D);
    }
}
