package com.kltyton.mob_battle.entity.summon.presence;

import com.kltyton.mob_battle.entity.support.EntityQueries;
import com.kltyton.mob_battle.items.armor.presence.PresenceArmorSkills;
import com.kltyton.mob_battle.items.armor.presence.PresenceArmorParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** 沿原版投射物物理与碰撞链路飞行；直接命中才返还冷却，爆炸命中不会重复结算。 */
public final class PresenceBlockProjectile extends ThrowableProjectile implements BlockAppearance {
    public PresenceBlockProjectile(EntityType<? extends PresenceBlockProjectile> type, Level level) {
        super(type, level);
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
    @Override public BlockState visualBlockState() { return Blocks.GRAY_CONCRETE.defaultBlockState(); }
    @Override public float visualScale() { return 0.45F; }
    @Override public float visualRoll(float partialTick) { return (tickCount + partialTick) * 20; }

    @Override
    public boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !EntityQueries.isFriendlyToSummon(this, getOwner(), entity);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide() && !isRemoved()) {
            // 飞行拖尾由客户端生成，避免为每一帧额外发送粒子网络包。
            level().addParticle(ParticleTypes.END_ROD, getX(), getY() + 0.2, getZ(), 0, 0, 0);
            if ((tickCount & 1) == 0) {
                level().addParticle(ParticleTypes.REVERSE_PORTAL, getX(), getY() + 0.2, getZ(), 0, 0, 0);
            }
        }
        if (!level().isClientSide() && tickCount > 200) discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (getOwner() instanceof ServerPlayer player && result.getEntity() instanceof LivingEntity) {
            PresenceArmorSkills.reduceCooldown(player, 200);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level() instanceof ServerLevel world) {
            world.explode(this, null, new ExplosionDamageCalculator() {
                @Override
                public boolean shouldDamageEntity(Explosion explosion, Entity target) {
                    return target instanceof LivingEntity && !EntityQueries.isFriendlyToSummon(PresenceBlockProjectile.this, getOwner(), target);
                }

                @Override
                public float getEntityDamageAmount(Explosion explosion, Entity target, float exposure) {
                    return 150.0F * exposure;
                }
            }, getX(), getY(), getZ(), 1.0F, false, Level.ExplosionInteraction.MOB);
            PresenceArmorParticles.impact(world, position());
            discard();
        }
    }
}
