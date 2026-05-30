package com.kltyton.mob_battle.entity.cbot;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

public class SnowmanIceBlockEntity extends Projectile {
    private static final int MAX_AGE = 20;
    private static final double HIT_RADIUS = 0.6D;
    private static final double EXPLOSION_RADIUS = 4.0D;

    public SnowmanIceBlockEntity(EntityType<? extends SnowmanIceBlockEntity> entityType, Level world) {
        super(entityType, world);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
    }

    @Override
    public void tick() {
        super.tick();
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (this.level().isClientSide()) {
            return;
        }
        if (this.tickCount >= MAX_AGE || touchesSolidBlock()) {
            explode();
            return;
        }
        Entity owner = this.getOwner();
        AABB box = this.getBoundingBox().inflate(HIT_RADIUS);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, box,
                target -> EntityUtil.isValidSummonCombatTarget(this, owner, target))) {
            explode();
            return;
        }
    }

    private boolean touchesSolidBlock() {
        return this.level().getBlockState(this.blockPosition()).isRedstoneConductor(this.level(), this.blockPosition());
    }

    private void explode() {
        if (!(this.level() instanceof ServerLevel world)) {
            this.discard();
            return;
        }
        Entity owner = this.getOwner();
        world.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState()),
                this.getX(), this.getY(), this.getZ(), 45, 1.0D, 0.8D, 1.0D, 0.12D);
        world.playSound(null, this.blockPosition(), SoundEvents.GLASS_BREAK, this.getSoundSource(), 1.3F, 0.7F);
        AABB box = this.getBoundingBox().inflate(EXPLOSION_RADIUS);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, box,
                target -> EntityUtil.isValidSummonCombatTarget(this, owner, target))) {
            Entity attacker = owner == null ? this : owner;
            target.hurtServer(world, this.damageSources().explosion(this, attacker), 50.0F);
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 5 * 20, 1), attacker);
            if (attacker instanceof LivingEntity livingAttacker) {
                target.addEffect(new MobEffectInstance(ModEffects.ARMOR_PIERCING_ENTRY, 5 * 20, 1), livingAttacker);
            } else {
                target.addEffect(new MobEffectInstance(ModEffects.ARMOR_PIERCING_ENTRY, 5 * 20, 1));
            }
        }
        this.discard();
    }
}
