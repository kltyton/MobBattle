package com.kltyton.mob_battle.entity.boss.dragon;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.meteorite.EnderDragonMeteoriteEntity;
import com.kltyton.mob_battle.entity.misc.ModifiedDragonBreathCloud;
import com.kltyton.mob_battle.sounds.ModSounds;
import com.kltyton.mob_battle.utils.TaskSchedulerUtil;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EnderDragonSkillManager {
    private final EnderDragon dragon;
    private final Random random = new Random();
    private long lastSkillTime = 0;
    public boolean isChargingRush = false;
    public long rushEndTime = 0;
    public boolean isChargingRush() {
        return isChargingRush;
    }
    // CD (tick)
    private long
            skill1CD = 0,
            skill2CD = 0,
            skill3CD = 0,
            skill4CD = 0,
            skill5CD = 0,
            skill6CD = 0;

    public EnderDragonSkillManager(EnderDragon dragon) {
        this.dragon = dragon;
    }

    public void tick(ServerLevel world) {
        long now = world.getGameTime();
        if (now - lastSkillTime < 60) return;

        boolean isFlying = false;
        if (dragon.getPhaseManager().getCurrentPhase() != null) {
            isFlying = !dragon.getPhaseManager().getCurrentPhase().isSitting();
        }

        if (isFlying) {
            tryCastRandomFlyingSkill(world, now);
        } else {
            passiveSittingSkills(world, now);
        }
    }

    private void tryCastRandomFlyingSkill(ServerLevel world, long now) {
        int r = random.nextInt(100);
        if (r < 18 && now > skill1CD) castSkill1(world);
        else if (r < 35 && now > skill2CD) castSkill2(world);
        else if (r < 50 && now > skill3CD) castSkill3(world);
        else if (r < 65 && now > skill4CD) castSkill4(world);
        else if (r < 78 && now > skill5CD) castSkill5(world);
        else if (r < 90 && now > skill6CD) castSkill6(world);
        else if (r < 95) tryCastSkill7(world); // 技能七协同
    }

    // ==================== 技能一：嘴部连吐10颗龙息弹（5秒CD） ====================
    private void castSkill1(ServerLevel world) {
        lastSkillTime = world.getGameTime();
        skill1CD = world.getGameTime() + 100; // 5s

        dragon.playSound(SoundEvents.ENDER_DRAGON_SHOOT, 5.0F, 0.8F);

        for (int i = 0; i < 10; i++) {
            TaskSchedulerUtil.runLater(i * 8, () -> { // 每8tick一颗
                if (dragon.isRemoved()) return;
                Vec3 dir = dragon.getViewVector(0).normalize().scale(1.8);
                DragonFireball fireball = new DragonFireball(
                        world, dragon, new Vec3(dir.x, dir.y - 0.5, dir.z));
                fireball.setPos(dragon.getX(), dragon.getEyeY() - 1.2, dragon.getZ());
                world.addFreshEntity(fireball);
            });
        }
    }

    // ==================== 技能二：散弹枪20颗（7秒CD） ====================
    private void castSkill2(ServerLevel world) {
        lastSkillTime = world.getGameTime();
        skill2CD = world.getGameTime() + 140; // 7s

        dragon.playSound(SoundEvents.ENDER_DRAGON_SHOOT, 5.0F, 0.6F);

        Vec3 baseDir = dragon.getViewVector(0).normalize();
        for (int i = 0; i < 20; i++) {
            Vec3 spread = baseDir.add(
                    (random.nextDouble() - 0.5) * 0.6,
                    (random.nextDouble() - 0.5) * 0.6,
                    (random.nextDouble() - 0.5) * 0.6
            ).normalize().scale(1.6);

            DragonFireball fb = new DragonFireball(world, dragon, new Vec3(spread.x, spread.y, spread.z));
            fb.setPos(dragon.getX(), dragon.getEyeY() - 1.0, dragon.getZ());
            world.addFreshEntity(fb);
        }
    }
    // ==================== 技能三：坠落3~5支三叉戟 + 冲击波（20s CD） ====================
    private void castSkill3(ServerLevel world) {
        lastSkillTime = world.getGameTime();
        skill3CD = world.getGameTime() + 400;
        dragon.playSound(SoundEvents.ENDER_DRAGON_GROWL, 8.0F, 0.6F);

        int count = 3 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            TaskSchedulerUtil.runLater(i * 12, () -> {
                if (dragon.isRemoved()) return;
                Vec3 target = dragon.position().add(
                        (random.nextDouble() - 0.5) * 60,
                        60 + random.nextDouble() * 20,
                        (random.nextDouble() - 0.5) * 60
                );
                EnderDragonMeteoriteEntity meteorite = new EnderDragonMeteoriteEntity(ModEntities.ENDER_DRAGON_METEORITE, world, dragon, 5.0f, false, 0);
                meteorite.snapTo(target.x, target.y, target.z, 0, 0);
                meteorite.setDeltaMovement(0, -1.5, 0);
                world.addFreshEntity(meteorite);
            });
        }
    }

    // ==================== 技能四：紫色粒子线 + 2秒后爆炸（18s CD） ====================
    private void castSkill4(ServerLevel world) {
        lastSkillTime = world.getGameTime();
        skill4CD = world.getGameTime() + 360;

        List<ServerPlayer> players = world.getPlayers(p -> canDragonTargetPlayer(p) && p.distanceToSqr(dragon) < 250 * 250);
        dragon.playSound(ModSounds.ENDER_DRAGON_SOUND_SKILL4_SOUND_EVENT, 6.0F, 0.5F);

        int lineCount = 4 + random.nextInt(3); // 强制4~6条
        List<Player> targets = new java.util.ArrayList<>(players);
        java.util.Collections.shuffle(targets);

        for (int line = 0; line < lineCount && !targets.isEmpty(); line++) {
            Player p = targets.removeFirst();

            Vec3 start = dragon.position().add(0, 2.5 + random.nextDouble() * 2, 0);
            Vec3 end = p.position().add(
                    (random.nextDouble() - 0.5) * 16,
                    (random.nextDouble() - 0.5) * 10 + 1.5,
                    (random.nextDouble() - 0.5) * 16
            );

            // 生成更粗、更密的粒子线（多层 + 轻微偏移模拟能量波动）
            for (int i = 0; i <= 50; i++) {  // 增加采样点，从30→50
                double t = i / 50.0;
                Vec3 basePos = start.lerp(end, t);

                // 每段生成多粒子，形成“粗线”效果
                TaskSchedulerUtil.runLater(i, () -> {  // 每tick生成一帧粒子（更连贯）
                    if (dragon.isRemoved()) return;

                    for (int layer = 0; layer < 4; layer++) {  // 4层叠加，显得更粗
                        double offset = (layer - 1.5) * 0.4;  // 轻微径向偏移
                        Vec3 pos = basePos.add(
                                (random.nextDouble() - 0.5) * 0.6,
                                offset + (random.nextDouble() - 0.5) * 0.3,
                                (random.nextDouble() - 0.5) * 0.6
                        );

                        world.sendParticles(ParticleTypes.DRAGON_BREATH, pos.x, pos.y, pos.z, 5, 0.1, 0.1, 0.1, 0.02);
                        world.sendParticles(ParticleTypes.PORTAL, pos.x, pos.y, pos.z, 4, 0.08, 0.08, 0.08, 0.015);
                        world.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.x, pos.y, pos.z, 1, 0, 0.05, 0, 0.02);
                    }
                });
            }

            // 2秒后爆炸（范围略扩大，视觉更震撼）
            TaskSchedulerUtil.runLater(40, () -> {
                if (dragon.isRemoved()) return;
                world.explode(dragon, end.x, end.y, end.z, 8.0F, false, Level.ExplosionInteraction.MOB);
                AABB box = new AABB(end.x - 10, end.y - 10, end.z - 10, end.x + 10, end.y + 10, end.z + 10);
                for (Entity e : world.getEntities(null, box)) {
                    if (e instanceof LivingEntity living && !e.isSpectator()) {
                        if (living.isAlliedTo(dragon)) continue;
                        living.hurtServer(world, living.damageSources().explosion(dragon, dragon), 400.0F);
                    }
                }
                // 额外爆炸粒子
                world.sendParticles(ParticleTypes.EXPLOSION_EMITTER, end.x, end.y, end.z, 1, 0, 0, 0, 0);
            });
        }
    }

    // ==================== 技能五：高速冲刺（15s CD） ====================
    private void castSkill5(ServerLevel world) {
        lastSkillTime = world.getGameTime();
        skill5CD = world.getGameTime() + 300;
        dragon.playSound(SoundEvents.ENDER_DRAGON_GROWL, 5.0F, 1.2F);

        LivingEntity target = dragon.getTarget();
        if (target instanceof Player player && !canDragonTargetPlayer(player)) {
            target = null;
        }
        if (target == null) {
            target = world.getNearestPlayer(
                    TargetingConditions.forCombat().range(96.0),
                    dragon,
                    dragon.getX(), dragon.getY(), dragon.getZ()
            );
        }
        if (target instanceof Player player && !canDragonTargetPlayer(player)) return;
        if (target == null) return;

        Vec3 dir = target.position().subtract(dragon.position()).normalize();
        dragon.setDeltaMovement(dir.scale(2.0)); // 2倍速度
        dragon.hurtMarked = true;

        isChargingRush = true;
        rushEndTime = world.getGameTime() + 40; // 最多持续2秒（可被打断或自然结束）

        // 定时结束冲刺状态（防止卡住）
        TaskSchedulerUtil.runLater(40, () -> {
            isChargingRush = false;
            if (!dragon.isRemoved()) {
                dragon.setDeltaMovement(dragon.getDeltaMovement().scale(0.4)); // 减速
            }
        });
    }

    // ==================== 技能六：玩家周围紫色粒子圈 + 爆炸（15s CD） ====================
    private void castSkill6(ServerLevel world) {
        lastSkillTime = world.getGameTime();
        skill6CD = world.getGameTime() + 300;

        List<ServerPlayer> players = world.getPlayers(p -> canDragonTargetPlayer(p) && p.distanceToSqr(dragon) < 200*200);
        for (Player p : players) {
            for (int i = 0; i < 6; i++) {
                Vec3 center = p.position().add(
                        (random.nextDouble()-0.5)*12,
                        0.5,
                        (random.nextDouble()-0.5)*12
                );

                // 画直径3格紫色圈（持续2秒）
                for (int t = 0; t < 40; t++) {
                    TaskSchedulerUtil.runLater(t, () -> {
                        double r = 1.5;
                        for (int a = 0; a < 24; a++) {
                            double ang = a * Math.PI * 2 / 24;
                            world.sendParticles(ParticleTypes.DRAGON_BREATH,
                                    center.x + Math.cos(ang)*r, center.y, center.z + Math.sin(ang)*r,
                                    1, 0, 0.05, 0, 0);
                        }
                    });
                }

                // 2秒后爆炸
                TaskSchedulerUtil.runLater(40, () -> {
                    AABB box = new AABB(center.x-2, center.y-2, center.z-2, center.x+2, center.y+3, center.z+2);
                    for (Entity e : world.getEntities(null, box)) {
                        if (e instanceof LivingEntity living) {
                            living.hurtServer(world, living.damageSources().magic(), 20.0F);
                            world.explode(null, center.x, center.y, center.z, 5.0F, false, Level.ExplosionInteraction.MOB);
                        }
                    }
                });
            }
        }
    }

    // ==================== 技能七：拍飞玩家落地（协同10%概率） ====================
    private void tryCastSkill7(ServerLevel world) {
        if (random.nextInt(10) != 0) return; // 10%概率
        dragon.playSound(SoundEvents.ENDER_DRAGON_GROWL, 4.0F, 0.7F);

        List<ServerPlayer> flyingPlayers = world.getPlayers(p ->
                canDragonTargetPlayer(p) && !p.onGround() && p.distanceToSqr(dragon) < 150*150);

        for (Player p : flyingPlayers) {
            p.setDeltaMovement(p.getDeltaMovement().add(0, -120.0, 0));
            p.push(0, -120.0, 0);
            p.hurtMarked = true;
            p.addEffect(new MobEffectInstance(
                    MobEffects.SLOWNESS, 40, 2));
        }
    }

    // ==================== 停歇被动 ====================
    private void passiveSittingSkills(ServerLevel world, long now) {
        // 被动1：全岛稀疏龙息覆盖（粒子少）
        if (now % 5 == 0) {
            // 每次生成 8-12 个云团，这样在短时间内就能铺满全岛
            int spawnCount = 8 + random.nextInt(5);

            for (int i = 0; i < spawnCount; i++) {
                // 1. 极坐标随机：确保在半径 85 的圆盘内均匀分布
                double angle = random.nextDouble() * 2 * Math.PI;
                // 使用 sqrt 保证圆心到边缘的分布密度一致
                double distance = Math.sqrt(random.nextDouble()) * 85;

                double x = Math.cos(angle) * distance;
                double z = Math.sin(angle) * distance;

                // 2. 准确定位地表：注意这里是基于主岛中心 (0,0) 的偏移
                BlockPos targetPos = new BlockPos((int)x, 0, (int)z);
                targetPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, targetPos);

                // 过滤掉掉进虚空的点
                if (targetPos.getY() < 50) continue;

                // 3. 创建并配置龙息云
                ModifiedDragonBreathCloud cloud = new ModifiedDragonBreathCloud(world, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);

                cloud.setOwner(dragon);
                cloud.setCustomParticle(ParticleTypes.DRAGON_BREATH);

                // 增大初始半径，让重叠更紧密
                float initialRadius = 3.5F;
                cloud.setRadius(initialRadius);

                // 延长持续时间：20秒 (400 tick)，确保旧的还没消失，新的已经铺上来了
                cloud.setDuration(400);

                // 设置半径增长：让它缓慢扩散到 8.0 左右
                float targetRadius = 8.0F;
                cloud.setRadiusPerTick((targetRadius - initialRadius) / cloud.getDuration());

                // 药水效果设置
                cloud.setPotionDurationScale(0.25F);

                world.addFreshEntity(cloud);
            }
        }

        // 被动2：陆续掉20颗
        if (now % 200 == 0) { // 每10秒掉一批
            for (int i = 0; i < 20; i++) {
                TaskSchedulerUtil.runLater(i * 3, () -> {
                    Vec3 spawn = dragon.position().add(
                            (random.nextDouble()-0.5)*120,
                            70 + random.nextDouble()*30,
                            (random.nextDouble()-0.5)*120
                    );
                    EnderDragonMeteoriteEntity meteorite = new EnderDragonMeteoriteEntity(ModEntities.ENDER_DRAGON_METEORITE, world, dragon, 5.0f, false, 0);
                    meteorite.snapTo(spawn.x, spawn.y, spawn.z, 0, 0);
                    meteorite.setDeltaMovement(0, -1.5, 0);
                    world.addFreshEntity(meteorite);
                });
            }
        }
    }

    private static boolean canDragonTargetPlayer(Player player) {
        return !player.isCreative() && !player.isSpectator();
    }
}
