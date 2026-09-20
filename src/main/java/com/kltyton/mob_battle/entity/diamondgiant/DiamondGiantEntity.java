package com.kltyton.mob_battle.entity.diamondgiant;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.kltyton.mob_battle.client.animation.gecko.SkillAnimationPlayback;
import com.kltyton.mob_battle.client.animation.keyframe.ParticleKeyframeHandler;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.skillentity.SkillVisualEntity;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import com.kltyton.mob_battle.event.scheduler.ServerTickScheduler;
import com.kltyton.mob_battle.skill.api.SkillEntity;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

/**
 * 钻石巨人的服务端技能实体。
 *
 * <p>动画只负责向客户端同步视觉状态；普攻、技能冷却、技能计数和激光命中全部在
 * 服务端 tick 中完成。客户端关键帧没有伤害或 AI 控制入口，避免重复伤害以及依赖
 * 客户端 stop 包恢复实体状态。</p>
 */
public final class DiamondGiantEntity extends Monster implements SkillEntity, GeoEntity {
    public static final int ATTACK_2_COOLDOWN_TICKS = 200;
    public static final int ATTACK_2_FANG_COUNT = 16;
    public static final double ATTACK_2_FANG_SPACING = 1.25D;
    public static final float NORMAL_ATTACK_DAMAGE = 40.0F;
    public static final float ATTACK_2_DAMAGE = 10.0F;
    public static final float ATTACK_3_DAMAGE = 10.0F;
    public static final double ATTACK_3_LASER_LENGTH = 240.0D;
    public static final double ATTACK_3_LASER_RADIUS = 1.0D;
    public static final int ATTACK_3_VISUAL_IDLE = 0;
    public static final int ATTACK_3_VISUAL_CHARGING = 1;
    public static final int ATTACK_3_VISUAL_FIRING = 2;

    private static final EntityDataAccessor<Boolean> HAS_SKILL =
            SynchedEntityData.defineId(DiamondGiantEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_3_VISUAL_PHASE =
            SynchedEntityData.defineId(DiamondGiantEntity.class, EntityDataSerializers.INT);
    private static final int NORMAL_ATTACK_HIT_TICKS_1 = 11;
    private static final int NORMAL_ATTACK_HIT_TICKS_2 = 12;
    private static final int NORMAL_ATTACK_DURATION_TICKS = 23;
    private static final int ATTACK_2_HIT_TICKS = 14;
    private static final int ATTACK_2_DURATION_TICKS = 30;
    private static final int ATTACK_3_LASER_TICKS = 18;
    private static final int ATTACK_3_VISUAL_END_TICKS = 28;
    private static final int ATTACK_3_DURATION_TICKS = 46;
    private static final int MAX_ATTACK_3_IMPACT_EFFECTS = 12;
    private static final double NORMAL_ATTACK_RADIUS = 4.0D;

    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIMATION = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK_1_1_ANIMATION = RawAnimation.begin().thenPlay("attack1_1");
    private static final RawAnimation ATTACK_1_2_ANIMATION = RawAnimation.begin().thenPlay("attack1_2");
    private static final RawAnimation ATTACK_2_ANIMATION = RawAnimation.begin().thenPlay("attack2");
    private static final RawAnimation ATTACK_3_ANIMATION = RawAnimation.begin().thenPlay("attack3");

    private final AnimatableInstanceCache geoCache = com.geckolib.util.GeckoLibUtil.createInstanceCache(this);
    private ActiveSkill activeSkill = ActiveSkill.NONE;
    private int skillTicks;
    private int attack2Cooldown;
    private int attack2FangsSpawned;
    private int normalAttackCount;
    private int attack3ReleaseCount;
    private boolean nextAttackIsFirstVariant = true;
    private boolean chainAttack3;

    public DiamondGiantEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 100;
    }

    /** 创建 8000 最大生命值的实体属性。 */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 8000.0D)
                .add(Attributes.ATTACK_DAMAGE, NORMAL_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.STEP_HEIGHT, 2.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                (target, level) -> EntityQueries.isValidCombatTarget(this, target)));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false,
                (target, level) -> target instanceof Enemy && EntityQueries.isValidCombatTarget(this, target)));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_SKILL, false);
        builder.define(ATTACK_3_VISUAL_PHASE, ATTACK_3_VISUAL_IDLE);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.attack2Cooldown = Math.max(0, input.getIntOr("Attack2Cooldown", 0));
        this.normalAttackCount = Math.max(0, input.getIntOr("NormalAttackCount", 0));
        this.attack3ReleaseCount = Math.max(0, input.getIntOr("Attack3ReleaseCount", 0));
        this.nextAttackIsFirstVariant = input.getBooleanOr("NextAttackIsFirstVariant", true);
        this.activeSkill = ActiveSkill.NONE;
        this.skillTicks = 0;
        this.attack2FangsSpawned = 0;
        this.chainAttack3 = false;
        this.setHasSkill(false);
        this.setAttack3VisualPhase(ATTACK_3_VISUAL_IDLE);
        this.setNoAi(false);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Attack2Cooldown", this.attack2Cooldown);
        output.putInt("NormalAttackCount", this.normalAttackCount);
        output.putInt("Attack3ReleaseCount", this.attack3ReleaseCount);
        output.putBoolean("NextAttackIsFirstVariant", this.nextAttackIsFirstVariant);
    }

    @Override
    public boolean canSkill() {
        return !this.level().isClientSide() && this.isAlive() && this.getTarget() != null
                && EntityQueries.isValidCombatTarget(this, this.getTarget());
    }

    @Override
    public boolean hasSkill() {
        return this.entityData.get(HAS_SKILL);
    }

    @Override
    public void setHasSkill(boolean hasSkill) {
        this.entityData.set(HAS_SKILL, hasSkill);
    }

    /** 返回服务端维护的 attack2 剩余冷却，供 GameTest 和诊断读取。 */
    public int getAttack2Cooldown() {
        return this.attack2Cooldown;
    }

    /** 返回本次 attack2 已由服务端生成的冰晶尖牙数量；技能结束时归零。 */
    public int getAttack2FangsSpawned() {
        return this.attack2FangsSpawned;
    }

    /** 返回当前已经完成的普攻次数（达到四次后会归零并衔接 attack3）。 */
    public int getNormalAttackCount() {
        return this.normalAttackCount;
    }

    /** 返回本实体已开始的 attack3 次数，服务端每次释放只增加一次。 */
    public int getAttack3ReleaseCount() {
        return this.attack3ReleaseCount;
    }

    /** 客户端只读的 attack3 视觉阶段；伤害仍完全由服务端状态机裁决。 */
    public int getAttack3VisualPhase() {
        return this.entityData.get(ATTACK_3_VISUAL_PHASE);
    }

    private void setAttack3VisualPhase(int phase) {
        this.entityData.set(ATTACK_3_VISUAL_PHASE, phase);
    }

    /** 返回当前服务端技能名称，空闲时为 {@code none}。 */
    public String getActiveSkillName() {
        return this.activeSkill.animationName;
    }

    /**
     * GameTest 的精确普攻触发入口；包可见且不属于网络协议，生产网络无法调用该入口。
     */
    boolean triggerNormalAttackForGameTest() {
        return startNormalAttack();
    }

    /** GameTest 专用：避免自动 attack2 抢占需要独立验证的普攻状态机。 */
    void suppressAutomaticAttack2ForGameTest() {
        this.attack2Cooldown = Integer.MAX_VALUE;
    }

    /** GameTest 专用：从服务端直接开始一次 attack2，验证其完整计时队列。 */
    boolean triggerAttack2ForGameTest() {
        if (!(this.level() instanceof ServerLevel) || this.hasSkill()) {
            return false;
        }
        startAttack2();
        return true;
    }

    /** GameTest 专用：直接开始 attack3，以锁定客户端视觉阶段的服务端时序。 */
    boolean triggerAttack3ForGameTest() {
        if (!(this.level() instanceof ServerLevel) || this.hasSkill() || this.getTarget() == null) {
            return false;
        }
        startAttack3();
        return true;
    }

    /** GameTest 专用：读取当前 attack2 动画的服务端 tick，不作为运行时协议。 */
    int getAttack2SkillTicksForGameTest() {
        return this.activeSkill == ActiveSkill.ATTACK_2 ? this.skillTicks : 0;
    }

    /** GameTest 专用：立即执行一次已锁定朝向的激光判定。 */
    void fireLaserForGameTest() {
        faceCurrentTarget();
        fireLaser();
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        if (!(target instanceof LivingEntity living) || this.hasSkill()
                || !EntityQueries.isValidCombatTarget(this, living)) {
            return false;
        }
        return startNormalAttack();
    }

    private boolean startNormalAttack() {
        if (!(this.level() instanceof ServerLevel) || this.hasSkill()) {
            return false;
        }
        faceCurrentTarget();
        this.activeSkill = this.nextAttackIsFirstVariant ? ActiveSkill.NORMAL_1_1 : ActiveSkill.NORMAL_1_2;
        this.nextAttackIsFirstVariant = !this.nextAttackIsFirstVariant;
        this.skillTicks = 0;
        this.chainAttack3 = false;
        this.setHasSkill(true);
        this.setNoAi(true);
        this.triggerAnim("skill_controller", this.activeSkill.animationName);
        return true;
    }

    private void startAttack2() {
        faceCurrentTarget();
        this.activeSkill = ActiveSkill.ATTACK_2;
        this.skillTicks = 0;
        this.attack2FangsSpawned = 0;
        this.attack2Cooldown = ATTACK_2_COOLDOWN_TICKS;
        this.setHasSkill(true);
        this.setNoAi(true);
        this.triggerAnim("skill_controller", "attack2");
    }

    private void startAttack3() {
        faceCurrentTarget();
        this.activeSkill = ActiveSkill.ATTACK_3;
        this.skillTicks = 0;
        this.attack3ReleaseCount++;
        this.setHasSkill(true);
        this.setAttack3VisualPhase(ATTACK_3_VISUAL_CHARGING);
        this.setNoAi(true);
        this.triggerAnim("skill_controller", "attack3");
    }

    /** 在冻结 AI 前把本次技能朝向锁定到当前目标眼位。 */
    private void faceCurrentTarget() {
        LivingEntity target = this.getTarget();
        if (target == null) {
            return;
        }
        Vec3 delta = target.getEyePosition().subtract(this.getEyePosition());
        if (delta.lengthSqr() < 1.0E-8D) {
            return;
        }
        double horizontalDistance = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        float yaw = (float) (Mth.atan2(delta.z, delta.x) * Mth.RAD_TO_DEG) - 90.0F;
        float pitch = (float) -(Mth.atan2(delta.y, horizontalDistance) * Mth.RAD_TO_DEG);
        this.setYRot(yaw);
        this.setYHeadRot(yaw);
        this.setYBodyRot(yaw);
        this.setXRot(Mth.clamp(pitch, -90.0F, 90.0F));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            tickServerSkillState();
        }
    }

    private void tickServerSkillState() {
        if (!this.isAlive()) {
            clearAttack2FangQueue();
            if (this.activeSkill != ActiveSkill.NONE) {
                finishSkill();
            }
            return;
        }
        if (this.attack2Cooldown > 0) {
            this.attack2Cooldown--;
        }
        if (this.activeSkill != ActiveSkill.NONE) {
            this.skillTicks++;
            switch (this.activeSkill) {
                case NORMAL_1_1 -> {
                    if (this.skillTicks == NORMAL_ATTACK_HIT_TICKS_1) {
                        performNormalAttackDamage();
                    }
                    if (this.skillTicks >= NORMAL_ATTACK_DURATION_TICKS) {
                        finishNormalAttack();
                    }
                }
                case NORMAL_1_2 -> {
                    if (this.skillTicks == NORMAL_ATTACK_HIT_TICKS_2) {
                        performNormalAttackDamage();
                    }
                    if (this.skillTicks >= NORMAL_ATTACK_DURATION_TICKS) {
                        finishNormalAttack();
                    }
                }
                case ATTACK_2 -> {
                    if (this.skillTicks >= ATTACK_2_HIT_TICKS
                            && this.attack2FangsSpawned < ATTACK_2_FANG_COUNT) {
                        spawnNextIceFang();
                    }
                    if (this.skillTicks >= ATTACK_2_DURATION_TICKS) {
                        finishSkill();
                    }
                }
                case ATTACK_3 -> {
                    if (this.skillTicks == ATTACK_3_LASER_TICKS) {
                        this.setAttack3VisualPhase(ATTACK_3_VISUAL_FIRING);
                    }
                    // 第 18 至 27 tick 各结算一次，沿技能开始时锁定的朝向允许横向躲避。
                    if (this.skillTicks >= ATTACK_3_LASER_TICKS
                            && this.skillTicks < ATTACK_3_VISUAL_END_TICKS) {
                        fireLaser();
                    }
                    if (this.skillTicks == ATTACK_3_VISUAL_END_TICKS) {
                        this.setAttack3VisualPhase(ATTACK_3_VISUAL_IDLE);
                    }
                    if (this.skillTicks >= ATTACK_3_DURATION_TICKS) {
                        finishSkill();
                    }
                }
                case NONE -> {
                }
            }
            return;
        }

        this.setHasSkill(false);
        this.setNoAi(false);
        LivingEntity target = this.getTarget();
        if (this.attack2Cooldown == 0 && target != null && EntityQueries.isValidCombatTarget(this, target)) {
            startAttack2();
        }
    }

    private void performNormalAttackDamage() {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(NORMAL_ATTACK_RADIUS),
                candidate -> EntityQueries.isValidCombatTarget(this, candidate))) {
            target.hurtServer(world, this.damageSources().mobAttack(this), NORMAL_ATTACK_DAMAGE);
        }
        this.normalAttackCount++;
        if (this.normalAttackCount >= 4) {
            this.normalAttackCount = 0;
            this.chainAttack3 = true;
        }
    }

    private void finishNormalAttack() {
        if (this.chainAttack3) {
            this.chainAttack3 = false;
            startAttack3();
            return;
        }
        finishSkill();
    }

    private void spawnNextIceFang() {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        Vec3 forward = Vec3.directionFromRotation(0.0F, this.getYRot()).normalize();
        Vec3 position = this.position().add(forward.scale(
                ATTACK_2_FANG_SPACING * (this.attack2FangsSpawned + 1)));
        SkillVisualEntity fangs = ModEntities.DIAMOND_GIANT_SPIKE.create(world, EntitySpawnReason.MOB_SUMMONED);
        if (fangs == null) {
            return;
        }
        fangs.snapTo(position.x, position.y, position.z, this.getYRot(), this.getXRot());
        fangs.setNoGravity(true);
        int variant = this.attack2FangsSpawned == ATTACK_2_FANG_COUNT - 1 ? 2 : 0;
        fangs.configure(this, ATTACK_2_DAMAGE, 1, 9, 0.5D, variant);
        world.addFreshEntity(fangs);
        ServerTickScheduler.schedule(world.getServer(), 10, () -> {
            if (!fangs.isRemoved()) {
                fangs.discard();
            }
        });
        this.attack2FangsSpawned++;
    }

    private void fireLaser() {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        Vec3 direction = this.getViewVector(1.0F).normalize();
        Vec3 start = this.getEyePosition().add(direction.scale(0.5D));
        Vec3 end = start.add(direction.scale(ATTACK_3_LASER_LENGTH));
        AABB searchBox = new AABB(start, end).inflate(ATTACK_3_LASER_RADIUS);
        Set<java.util.UUID> hitTargets = new HashSet<>();
        int impactEffects = 0;
        spawnLaserMuzzleBurst(world, start);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, searchBox,
                candidate -> EntityQueries.isValidCombatTarget(this, candidate))) {
            if (!hitTargets.add(target.getUUID()) || !isOnLaserPath(start, direction, target)) {
                continue;
            }
            // 连续十次命中必须独立结算，不能被上一 tick 的同额伤害无敌帧吞掉。
            target.invulnerableTime = 0;
            boolean damaged = target.hurtServer(
                    world, this.damageSources().indirectMagic(this, this), ATTACK_3_DAMAGE);
            target.invulnerableTime = 0;
            if (damaged && impactEffects < MAX_ATTACK_3_IMPACT_EFFECTS) {
                spawnLaserImpact(world, target.getBoundingBox().getCenter());
                impactEffects++;
            }
        }
    }

    private static void spawnLaserMuzzleBurst(ServerLevel world, Vec3 position) {
        world.sendParticles(ColorParticleOption.create(ParticleTypes.FLASH, 0xBFFFFF),
                position.x, position.y, position.z, 2, 0.15D, 0.15D, 0.15D, 0.0D);
        world.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                position.x, position.y, position.z, 32, 1.1D, 0.8D, 1.1D, 0.16D);
        world.sendParticles(ParticleTypes.END_ROD,
                position.x, position.y, position.z, 16, 0.55D, 0.55D, 0.55D, 0.08D);
    }

    private static void spawnLaserImpact(ServerLevel world, Vec3 position) {
        world.sendParticles(ColorParticleOption.create(ParticleTypes.FLASH, 0x55EEFF),
                position.x, position.y, position.z, 2, 0.2D, 0.2D, 0.2D, 0.0D);
        world.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                position.x, position.y, position.z, 18, 0.7D, 0.7D, 0.7D, 0.14D);
        world.sendParticles(ParticleTypes.END_ROD,
                position.x, position.y, position.z, 10, 0.4D, 0.5D, 0.4D, 0.06D);
    }

    private static boolean isOnLaserPath(Vec3 start, Vec3 direction, LivingEntity target) {
        Vec3 center = target.getBoundingBox().getCenter();
        double projection = center.subtract(start).dot(direction);
        if (projection < 0.0D || projection > ATTACK_3_LASER_LENGTH) {
            return false;
        }
        Vec3 closest = start.add(direction.scale(projection));
        return distanceSquaredToBox(closest, target.getBoundingBox())
                <= ATTACK_3_LASER_RADIUS * ATTACK_3_LASER_RADIUS;
    }

    private static double distanceSquaredToBox(Vec3 point, AABB box) {
        double dx = Math.max(box.minX - point.x, Math.max(0.0D, point.x - box.maxX));
        double dy = Math.max(box.minY - point.y, Math.max(0.0D, point.y - box.maxY));
        double dz = Math.max(box.minZ - point.z, Math.max(0.0D, point.z - box.maxZ));
        return dx * dx + dy * dy + dz * dz;
    }

    private void finishSkill() {
        this.activeSkill = ActiveSkill.NONE;
        this.skillTicks = 0;
        clearAttack2FangQueue();
        this.chainAttack3 = false;
        this.setHasSkill(false);
        this.setAttack3VisualPhase(ATTACK_3_VISUAL_IDLE);
        this.setNoAi(false);
    }

    private void clearAttack2FangQueue() {
        this.attack2FangsSpawned = 0;
    }

    /** 钻石巨人不接受客户端关键帧控制；客户端仅负责播放服务端同步的动画。 */
    @Override
    public boolean handleSkillPayload(String skillName) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 0, state -> {
            if (this.hasSkill() && SkillAnimationPlayback.hasActiveSkill(state)) {
                return PlayState.CONTINUE;
            }
            return state.isMoving() ? state.setAndContinue(WALK_ANIMATION) : state.setAndContinue(IDLE_ANIMATION);
        }));
        controllers.add(new AnimationController<>("skill_controller", 0,
                state -> SkillAnimationPlayback.playTriggeredAnimationOrStop(state))
                .receiveTriggeredAnimations()
                .triggerableAnim("attack1_1", ATTACK_1_1_ANIMATION)
                .triggerableAnim("attack1_2", ATTACK_1_2_ANIMATION)
                .triggerableAnim("attack2", ATTACK_2_ANIMATION)
                .triggerableAnim("attack3", ATTACK_3_ANIMATION)
                // ParticleStorm 1.4.0 自动消费 particle_effects；该 handler 保留兼容调用，
                // 不在客户端执行任何伤害或 AI 状态变更。
                .setParticleKeyframeHandler(s -> ParticleKeyframeHandler.handle(this, s))
                .setCustomInstructionKeyframeHandler(s -> {
                }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    private enum ActiveSkill {
        NONE("none"),
        NORMAL_1_1("attack1_1"),
        NORMAL_1_2("attack1_2"),
        ATTACK_2("attack2"),
        ATTACK_3("attack3");

        private final String animationName;

        ActiveSkill(String animationName) {
            this.animationName = animationName;
        }
    }
}
