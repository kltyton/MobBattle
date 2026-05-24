package com.kltyton.mob_battle.entity.cbot;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class SnowmanIceBlockEntity extends ProjectileEntity {
    private static final int MAX_AGE = 20;
    private static final double HIT_RADIUS = 0.6D;
    private static final double EXPLOSION_RADIUS = 4.0D;

    public SnowmanIceBlockEntity(EntityType<? extends SnowmanIceBlockEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    @Override
    protected void readCustomData(ReadView view) {
    }

    @Override
    protected void writeCustomData(WriteView view) {
    }

    @Override
    public void tick() {
        super.tick();
        this.move(MovementType.SELF, this.getVelocity());
        if (this.getWorld().isClient()) {
            return;
        }
        if (this.age >= MAX_AGE || touchesSolidBlock()) {
            explode();
            return;
        }
        Entity owner = this.getOwner();
        Box box = this.getBoundingBox().expand(HIT_RADIUS);
        for (LivingEntity target : this.getWorld().getEntitiesByClass(LivingEntity.class, box,
                target -> EntityUtil.isValidSummonCombatTarget(this, owner, target))) {
            explode();
            return;
        }
    }

    private boolean touchesSolidBlock() {
        return this.getWorld().getBlockState(this.getBlockPos()).isSolidBlock(this.getWorld(), this.getBlockPos());
    }

    private void explode() {
        if (!(this.getWorld() instanceof ServerWorld world)) {
            this.discard();
            return;
        }
        Entity owner = this.getOwner();
        world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.ICE.getDefaultState()),
                this.getX(), this.getY(), this.getZ(), 45, 1.0D, 0.8D, 1.0D, 0.12D);
        world.playSound(null, this.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, this.getSoundCategory(), 1.3F, 0.7F);
        Box box = this.getBoundingBox().expand(EXPLOSION_RADIUS);
        for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, box,
                target -> EntityUtil.isValidSummonCombatTarget(this, owner, target))) {
            Entity attacker = owner == null ? this : owner;
            target.damage(world, this.getDamageSources().explosion(this, attacker), 50.0F);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 5 * 20, 1), attacker);
            if (attacker instanceof LivingEntity livingAttacker) {
                target.addStatusEffect(new StatusEffectInstance(ModEffects.ARMOR_PIERCING_ENTRY, 5 * 20, 1), livingAttacker);
            } else {
                target.addStatusEffect(new StatusEffectInstance(ModEffects.ARMOR_PIERCING_ENTRY, 5 * 20, 1));
            }
        }
        this.discard();
    }
}
