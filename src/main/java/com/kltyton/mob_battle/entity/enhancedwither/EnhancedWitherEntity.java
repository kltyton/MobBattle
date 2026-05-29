package com.kltyton.mob_battle.entity.enhancedwither;

import com.kltyton.mob_battle.entity.OwnedSummon;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EnhancedWitherEntity extends WitherBoss implements OwnedSummon {
    @Nullable
    private LivingEntity summonOwner;

    public EnhancedWitherEntity(EntityType<? extends WitherBoss> entityType, Level world) {
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
    public boolean canAttack(LivingEntity target) {
        return isValidSummonTarget(target) && super.canAttack(target);
    }

    @Override
    protected void customServerAiStep(ServerLevel world) {
        super.customServerAiStep(world);
        if (this.tickCount % 20 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(2.0F);
        }
        LivingEntity target = this.getTarget();
        if (target != null && !isValidSummonTarget(target)) {
            this.setTarget(null);
            target = null;
        }
        if (this.tickCount % 30 == 0 && target != null && target.isAlive()) {
            Vec3 velocity = target.getEyePosition().subtract(this.getEyePosition()).normalize().scale(1.5D);
            WitherSkull skull = new WitherSkull(world, this, velocity);
            skull.setPos(this.getX(), this.getEyeY(), this.getZ());
            skull.setDangerous(this.random.nextFloat() < 0.25F);
            world.addFreshEntity(skull);
            this.playSound(SoundEvents.WITHER_SHOOT, 1.0F, 1.0F);
        }
    }

    public static AttributeSupplier.Builder createEnhancedWitherAttributes() {
        return WitherBoss.createAttributes()
                .add(Attributes.MAX_HEALTH, 1500.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }
}
