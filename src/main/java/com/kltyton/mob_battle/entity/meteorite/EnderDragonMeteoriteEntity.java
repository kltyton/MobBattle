package com.kltyton.mob_battle.entity.meteorite;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.customfireball.CustomFireballEntity;
import com.kltyton.mob_battle.sounds.ModSounds;
import com.kltyton.mob_battle.utils.EntityUtil;
import com.kltyton.mob_battle.utils.TaskSchedulerUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EnderDragonMeteoriteEntity extends MeteoriteEntity implements GeoEntity {
    private static final double MAX_SHOCKWAVE_RADIUS = 25.0;

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
            serverWorld.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(), this.getY(), this.getZ(), 3, 1.0, 1.0, 1.0, 0.1);
        }
    }
    private void spawnShockwave(ServerLevel world) {
        Vec3 center = this.position();
        Entity owner = this.getOwner();
        java.util.Set<Integer> hitEntities = new java.util.HashSet<>();

        int totalSteps = 800;           // ← 关键：原来400，现在800，扩散慢一倍
        double maxRadius = MAX_SHOCKWAVE_RADIUS;
        double wallHeight = 6.0;        // ← 加高一点，更有冲击波高度
        double thickness = 12.0;        // ← 厚度加大，烟雾更连贯

        for (int step = 0; step < totalSteps; step++) {
            int finalStep = step;
            TaskSchedulerUtil.runLater(step, () -> {
                if (world.isClientSide) return;

                double progress = (double) finalStep / totalSteps;
                double currentRadius = maxRadius * progress;   // ← 改成线性！最自然

                // 遍历所有玩家
                for (net.minecraft.server.level.ServerPlayer player : world.players()) {
                    Vec3 playerPos = player.position();
                    double distToCenter = playerPos.distanceTo(center);   // 3D距离也行

                    // 优化可见性：波浪正在靠近或刚经过玩家时才渲染（提前400格看到）
                    if (currentRadius > distToCenter - 400 && currentRadius < distToCenter + 150) {
                        int burstCount = 900;   // 全圆环密度，够密又不卡

                        for (int i = 0; i < burstCount; i++) {
                            // === 核心修复：完整360°均匀分布 ===
                            double angle = i * (2.0 * Math.PI / burstCount);   // 均匀，不再是扇形！

                            double rOffset = (Math.random() - 0.5) * thickness;
                            double finalRadius = currentRadius + rOffset;

                            double x = center.x + Math.cos(angle) * finalRadius;
                            double z = center.z + Math.sin(angle) * finalRadius;
                            double y = center.y + (Math.random() * wallHeight) - 1.0;  // 稍微贴地一点

                            // 粒子混合（云团感更强）
                            if (i % 3 == 0) {  // 更多烟雾
                                world.sendParticles(player, ParticleTypes.CAMPFIRE_COSY_SMOKE, true, false,
                                        x, y, z, 1, 0, 0.1, 0, 0.04);
                            }
                            if (i % 2 == 0) {
                                world.sendParticles(player, ParticleTypes.LARGE_SMOKE, true, false,
                                        x, y + 1.0, z, 1, 0.15, 0.15, 0.15, 0.02);  // 新增大烟雾，超级推荐！
                            }
                            world.sendParticles(player, ParticleTypes.DRAGON_BREATH, true, false,
                                    x, y, z, 1, 0.12, 0.12, 0.12, 0);

                            if (i % 6 == 0) {
                                world.sendParticles(player, ParticleTypes.WITCH, true, false,
                                        x, y + 0.8, z, 1, 0, 0, 0, 0);
                            }
                            if (i % 25 == 0) {
                                world.sendParticles(player, ParticleTypes.END_ROD, true, false,
                                        x, center.y + 0.2, z, 1, 0, 0, 0, 0);
                            }
                        }
                    }
                }

                // 伤害逻辑（步长改成每3步一次，更平滑）
                if (finalStep % 3 == 0) {
                    applyDamage(world, center, currentRadius, thickness, wallHeight, owner, hitEntities);
                }
            });
        }
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
                    if (!EntityUtil.isValidSummonCombatTarget(this, owner, living)) continue;

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
