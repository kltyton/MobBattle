package com.kltyton.mob_battle.entity.meteorite;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.customfireball.CustomFireballEntity;
import com.kltyton.mob_battle.sounds.ModSounds;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import com.kltyton.mob_battle.event.scheduler.ServerTickScheduler;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

public class EnderDragonMeteoriteEntity extends MeteoriteEntity implements GeoEntity {
    private static final double MAX_SHOCKWAVE_RADIUS = 25.0;
    private static final int SHOCKWAVE_STEPS = 50;
    private static final int PARTICLE_POINTS = 24;
    private static final int PARTICLE_STEP_INTERVAL = 2;
    static final double PARTICLE_VIEW_RADIUS = 64.0;
    private static final double PARTICLE_VIEW_RADIUS_SQUARED = PARTICLE_VIEW_RADIUS * PARTICLE_VIEW_RADIUS;

    public EnderDragonMeteoriteEntity(EntityType<? extends CustomFireballEntity> entityType, Level world) {
        super(entityType, world);
    }

    public EnderDragonMeteoriteEntity(EntityType<? extends CustomFireballEntity> entityType, Level world, LivingEntity owner, float power, boolean createFire, float damage) {
        super(entityType, world, owner, power, createFire, damage);
    }
    @Override

    protected void explodeAndApplyEffects() {
        Level world = this.level();
        // 1. 播放音效
        world.playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.METEORITE_SOUND_EVENT_REFERENCE, SoundSource.BLOCKS, 4.0f, 0.5f);
        if (world instanceof ServerLevel serverWorld) {
            spawnShockwave(serverWorld);
            sendParticleToNearbyPlayers(serverWorld, this.position(), ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(), this.getY(), this.getZ(), 3, 1.0, 1.0, 1.0, 0.1);
        }
    }
    private void spawnShockwave(ServerLevel world) {
        Vec3 center = this.position();
        Entity owner = this.getOwner();
        java.util.Set<Integer> hitEntities = new java.util.HashSet<>();

        double maxRadius = MAX_SHOCKWAVE_RADIUS;
        double wallHeight = 6.0;
        double thickness = 12.0;

        for (int step = 0; step < SHOCKWAVE_STEPS; step++) {
            int finalStep = step;
            ServerTickScheduler.schedule(world.getServer(), step, () -> {
                if (world.isClientSide()) return;

                double progress = (double) finalStep / (SHOCKWAVE_STEPS - 1);
                double currentRadius = maxRadius * progress;

                // 由服务端按冲击波中心的64格观察距离定向发送，避免向整个 ServerLevel 广播。
                if (finalStep % PARTICLE_STEP_INTERVAL == 0) {
                    spawnParticleRing(world, center, currentRadius, wallHeight);
                }

                applyDamage(world, center, currentRadius, thickness, wallHeight, owner, hitEntities);
            });
        }
    }

    private void spawnParticleRing(ServerLevel world, Vec3 center, double radius, double wallHeight) {
        for (int i = 0; i < PARTICLE_POINTS; i++) {
            double angle = i * (2.0D * Math.PI / PARTICLE_POINTS);
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;
            double y = center.y + world.getRandom().nextDouble() * wallHeight - 1.0D;

            sendParticleToNearbyPlayers(world, center,
                    PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1.0F),
                    x, y, z, 1, 0.12D, 0.12D, 0.12D, 0.0D
            );
            if (i % 3 == 0) {
                sendParticleToNearbyPlayers(world, center, ParticleTypes.LARGE_SMOKE,
                        x, y + 0.8D, z, 1, 0.12D, 0.12D, 0.12D, 0.02D);
            }
            if (i % 8 == 0) {
                sendParticleToNearbyPlayers(world, center, ParticleTypes.END_ROD,
                        x, center.y + 0.2D, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    /**
     * 只向冲击波中心 64 格内的玩家发送粒子数据。调用点来自服务端 tick，符合
     * PlayerLookup.around 的服务端线程约束。
     */
    private static <T extends ParticleOptions> void sendParticleToNearbyPlayers(
            ServerLevel world,
            Vec3 viewCenter,
            T particle,
            double x,
            double y,
            double z,
            int count,
            double xDist,
            double yDist,
            double zDist,
            double speed
    ) {
        for (ServerPlayer player : PlayerLookup.around(world, viewCenter, PARTICLE_VIEW_RADIUS)) {
            if (!isWithinParticleViewRange(viewCenter, player.position())) {
                continue;
            }

            // 64格筛选由 PlayerLookup 完成；overrideLimiter=true 避免 ServerLevel 的默认32格裁剪。
            world.sendParticles(player, particle, true, false,
                    x, y, z, count, xDist, yDist, zDist, speed);
        }
    }

    static boolean isWithinParticleViewRange(Vec3 viewCenter, Vec3 playerPosition) {
        return viewCenter.distanceToSqr(playerPosition) <= PARTICLE_VIEW_RADIUS_SQUARED;
    }

    private void applyDamage(ServerLevel world, Vec3 center, double radius, double thickness, double height, Entity owner, java.util.Set<Integer> hitEntities) {
        AABB damageBox = new AABB(
                center.x - radius - 5, center.y - 2, center.z - radius - 5,
                center.x + radius + 5, center.y + height + 2, center.z + radius + 5
        );

        for (Entity e : world.getEntities(null, damageBox)) {
            if (e instanceof LivingEntity living && !hitEntities.contains(e.getId())) {
                double dist = Math.sqrt(e.distanceToSqr(center.x, e.getY(), center.z));
                if (Math.abs(dist - radius) < thickness) {
                    if (!EntityQueries.isValidSummonCombatTarget(this, owner, living)) continue;

                    living.hurtServer(world, this.damageSources().magic(), 40.0F);
                    living.addEffect(new MobEffectInstance(ModEffects.HEART_EATER_ENTRY, 100, 7));

                    Vec3 push = e.position().subtract(center).normalize().scale(2.5);
                    e.push(push.x, 0.6, push.z);
                    e.hurtMarked = true;
                    hitEntities.add(e.getId());
                }
            }
        }
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", s -> s.setAndContinue(IDEA_ANIM)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
