package com.kltyton.mob_battle.entity.deepcreature;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.deepcreature.goal.DeepCreatureEntityNavigation;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Objects;

public class DeepCreatureEntity extends Monster implements GeoEntity, ModSkillEntityType {

    private final ServerBossEvent bossBar = new ServerBossEvent(
            this.getDisplayName(),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS
    );
    public static final EntityDataAccessor<Boolean> SPAWN_ANIM_BOOLEAN = SynchedEntityData.defineId(DeepCreatureEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(DeepCreatureEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN = SynchedEntityData.defineId(DeepCreatureEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> GRAB_TARGET_ID = SynchedEntityData.defineId(DeepCreatureEntity.class, EntityDataSerializers.INT);
    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        Holder<MobEffect> effectType = effect.getEffect();
        if (effectType.equals(MobEffects.SLOWNESS)) {
            return false;
        }
        return super.canBeAffected(effect);
    }
    public DeepCreatureEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.setHasSkill(false);
        this.setSkillCooldown(0);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SPAWN_ANIM_BOOLEAN, false);
        builder.define(HAS_SKILL, false);
        builder.define(SKILL_COOLDOWN, 0);
        builder.define(GRAB_TARGET_ID, -1);
    }
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }
    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity entity) {
        AABB attackBox = this.getAttackBoundingBox().inflate(5.0); // 扩大3格攻击距离
        return attackBox.intersects(entity.getBoundingBox());
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }
    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));// 添加复仇目标
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true)); // 添加主动攻击玩家目标
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, true)); // 添加僵尸攻击目标
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0)); // 添加远距离游荡目标
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 64.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation SPAWN_ANIM = RawAnimation.begin().thenLoop("spawn");
    protected static final RawAnimation ROAR_ANIM = RawAnimation.begin().thenPlay("roar");
    protected static final RawAnimation EARTHQUAKE_ANIM = RawAnimation.begin().thenPlay("earthquake");
    protected static final RawAnimation DEAD_ANIM = RawAnimation.begin().thenPlay("death");
    protected static final RawAnimation LEFT_SMASH = RawAnimation.begin().thenPlay("left_smash").thenPlay("left_smash_cont");
    protected static final RawAnimation RIGHT_SMASH = RawAnimation.begin().thenPlay("right_smash").thenPlay("right_smash_cont");
    protected static final RawAnimation LEFT_SIDE_STRIKE = RawAnimation.begin().thenPlay("left_side_strike");
    protected static final RawAnimation RIGHT_SIDE_STRIKE = RawAnimation.begin().thenPlay("right_side_strike");
    protected static final RawAnimation SONIC_BOOM = RawAnimation.begin().thenPlay("sonic_shot");
    protected static final RawAnimation CHARGE = RawAnimation.begin().thenPlay("charge");
    protected static final RawAnimation JUMP = RawAnimation.begin().thenPlay("jump_smash").thenPlay("jump_smash_cont");
    protected static final RawAnimation CATCH = RawAnimation.begin().thenPlay("catch").thenPlay("catch_shout");
    @Override
    protected void customServerAiStep(ServerLevel world) {
        this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());
        this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
    }
    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossBar.addPlayer(player);
    }
    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossBar.removePlayer(player);
    }
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 5 ,this::animationController)
                .setSoundKeyframeHandler(s -> {
            Player player = ClientUtil.getClientPlayer();
            if ("minecraft:entity.polar_bear.warning".equals(s.keyframeData().getSound())) {
                player.playSound(SoundEvents.POLAR_BEAR_WARNING, 5.0F, 0.85F);
            }
            if ("minecraft:entity.ender_dragon.growl".equals(s.keyframeData().getSound())) {
                player.playSound(SoundEvents.ENDER_DRAGON_GROWL, 5.0F, 0.85F);
            }
        }).triggerableAnim("death", DEAD_ANIM));
        controllers.add(new AnimationController<>("skill_controller",animTest -> {
            if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED && this.hasSkill()) {
                ClientPlayNetworking.send(new SkillPayload(
                        "stop", this.getId()
                ));
            }
            return PlayState.STOP;
        }).setSoundKeyframeHandler(s -> {
            Player player = ClientUtil.getClientPlayer();
            if ("minecraft:entity.polar_bear.warning".equals(s.keyframeData().getSound())) {
                player.playSound(SoundEvents.POLAR_BEAR_WARNING, 5.0F, 0.85F);
            }
            if ("minecraft:entity.ender_dragon.growl".equals(s.keyframeData().getSound())) {
                player.playSound(SoundEvents.ENDER_DRAGON_GROWL, 5.0F, 0.85F);
            }
            if ("runSideSound".equals(s.keyframeData().getSound())) {
                player.playSound(SoundEvents.POLAR_BEAR_WARNING, 1.0F, 0.5F);
                player.playSound(SoundEvents.ENDER_DRAGON_GROWL, 1.0F, 0.2F);
                if (this.getGrabTargetId() != -1) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "damage", this.getId()
                    ));
                }
            }
            if ("runLaunch".equals(s.keyframeData().getSound())) {
                player.playSound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 1.0F, 0.5F);
            }
        })
                .triggerableAnim("roar",ROAR_ANIM)
                .triggerableAnim("earthquake",EARTHQUAKE_ANIM)
                .triggerableAnim("lefts_smash", LEFT_SMASH)
                .triggerableAnim("rights_smash", RIGHT_SMASH)
                .triggerableAnim("left_side_strike", LEFT_SIDE_STRIKE)
                .triggerableAnim("right_side_strike", RIGHT_SIDE_STRIKE)
                .triggerableAnim("sonic_boom", SONIC_BOOM)
                .triggerableAnim("charge", CHARGE)
                .triggerableAnim("jump", JUMP)
                .triggerableAnim("catch", CATCH)
                .setCustomInstructionKeyframeHandler(s -> {
            if ("runRoar".equals(s.keyframeData().getInstructions())) {
                Player player = ClientUtil.getClientPlayer();
                player.playSound(SoundEvents.POLAR_BEAR_WARNING, 1.0F, 0.7F);
                player.playSound(SoundEvents.ENDER_DRAGON_GROWL, 1.0F, 0.7F);
                ClientPlayNetworking.send(new SkillPayload(
                        "roar", this.getId()
                ));
            }
            if ("runEarthquake".equals(s.keyframeData().getInstructions())) {
                ClientPlayNetworking.send(new SkillPayload(
                        "earthquake", this.getId()
                ));
            }
            if ("runSmash".equals(s.keyframeData().getInstructions())) {
                ClientPlayNetworking.send(new SkillPayload(
                        "smash", this.getId()
                ));
                Player player = ClientUtil.getClientPlayer();
                player.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 0.7F);
            }
            if ("runSide".equals(s.keyframeData().getInstructions())) {
                ClientPlayNetworking.send(new SkillPayload(
                        "side", this.getId()
                ));
                Player player = ClientUtil.getClientPlayer();
                player.playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1.0F, 0.7F);
            }
            if ("runSonicBoom".equals(s.keyframeData().getInstructions())) {
                ClientPlayNetworking.send(new SkillPayload(
                        "sonic_boom", this.getId()
                ));
            }
            if ("runCharge".equals(s.keyframeData().getInstructions())) {
                ClientPlayNetworking.send(new SkillPayload(
                        "charge", this.getId()
                ));
            }
            if ("runStopAi".equals(s.keyframeData().getInstructions())) {
                ClientPlayNetworking.send(new SkillPayload(
                        "stop_ai", this.getId()
                ));
            }
            if ("runBoom".equals(s.keyframeData().getInstructions())) {
                ClientPlayNetworking.send(new SkillPayload(
                        "earthquake", this.getId()
                ));
                Player player = ClientUtil.getClientPlayer();
                player.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 0.7F);
            }
            if ("runSmashGround_S".equals(s.keyframeData().getInstructions())) {
                ClientPlayNetworking.send(new SkillPayload(
                        "smash_ground_s", this.getId()
                ));
                Player player = ClientUtil.getClientPlayer();
                player.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 0.7F);
            }
            if ("runSmashGround_XL".equals(s.keyframeData().getInstructions())) {
                ClientPlayNetworking.send(new SkillPayload(
                        "smash_ground_xl", this.getId()
                ));
                Player player = ClientUtil.getClientPlayer();
                player.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 0.7F);
            }
            if ("runSmashGround_End".equals(s.keyframeData().getInstructions())) {
                ClientPlayNetworking.send(new SkillPayload(
                        "start_ai", this.getId()
                ));
            }
            if ("runCatch".equals(s.keyframeData().getInstructions())) {
                ClientPlayNetworking.send(new SkillPayload(
                        "catch", this.getId()
                ));
                s.getRenderState().addGeckolibData(DeepCreatureEntityRenderer.IS_CATCH, true);
            }
            if ("stopRunCatch".equals(s.keyframeData().getInstructions())) {
                ClientPlayNetworking.send(new SkillPayload(
                        "stop_run_catch", this.getId()
                ));
            }
            if ("runCatchDamage".equals(s.keyframeData().getInstructions())) {
                ClientPlayNetworking.send(new SkillPayload(
                        "catch_damage", this.getId()
                ));
            }
            if ("runCatch_End".equals(s.keyframeData().getInstructions())) {
                ClientPlayNetworking.send(new SkillPayload(
                        "catch_end", this.getId()
                ));
                s.getRenderState().addGeckolibData(DeepCreatureEntityRenderer.IS_CATCH, false);
            }
        }));
    }
    @Override
    public void readAdditionalSaveData(ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        this.setSpawnAnim(nbt.getBooleanOr("SpawnAnimBoolean", false));
        if (this.hasCustomName()) {
            this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
        }
    }
    @Override
    public void addAdditionalSaveData(ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("SpawnAnimBoolean", this.isSpawnAnimEnd());
    }

    @Override
    public void setHealth(float health) {
        if (this.bossBar != null) {
            this.bossBar.setProgress(health / this.getMaxHealth());
            this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
        }
        if (health <= 0.0F) {
            super.setHealth(0.1F);
            this.setNoAi(true);
            this.triggerAnim("main_controller", "death");
        } else {
            super.setHealth(health);
        }
    }
    @Override
    protected PathNavigation createNavigation(Level world) {
        return new DeepCreatureEntityNavigation(this, world);
    }

    private PlayState animationController(final AnimationTest<DeepCreatureEntity> state) {
        state.renderState().addGeckolibData(DeepCreatureEntityRenderer.ENTITY_ID, this.getId());
        if (state.isCurrentAnimation(DEAD_ANIM)) {
            ClientPlayNetworking.send(new SkillPayload(
                    "stop_ai", this.getId()
            ));
            if (state.controller().getAnimationState() == AnimationController.State.STOPPED) {
                ClientPlayNetworking.send(new SkillPayload(
                        "kill", this.getId()
                ));
            }
        }
        // 当实体刚生成时播放spawn动画
        if (this.tickCount < 220 && !this.isSpawnAnimEnd()) {
            return state.setAndContinue(SPAWN_ANIM);
        }
        // 否则，如果在移动，播放walk动画，否则idle动画
        if (this.getDeltaMovement().lengthSqr() > 0.015) {
            return state.setAndContinue(WALK_ANIM);
        }
        return state.setAndContinue(IDEA_ANIM);
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
    public int spawnAnimTick = 0;
    public int remoteCooldown = 0;
    // 冲刺方向，null 表示没有冲刺
    public Vec3 chargeDir = null;
    // 剩余冲刺 tick
    public int chargeTicksLeft = 0;
    public int stuckCooldown = 200;
    public int catchCooldown = 600;
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (getGrabTargetId() != -1 && this.tickCount % 20 == 0) {
                //抓取攻击
                if (this.getTarget() != null) {
                    this.getTarget().hurtServer((ServerLevel) this.level(), this.getTarget().damageSources().indirectMagic(this, this), 60F);
                }
            }
            if (isNoAi()) {
                //this.setInvulnerable(true);
                if (stuckCooldown > 0) {
                    stuckCooldown--;
                } else {
                    this.setNoAi(false);
                    stuckCooldown = 200;
                }
            } else {
                //this.setInvulnerable(false);
                stuckCooldown = 200;
            }
            if (catchCooldown > 0) {
                catchCooldown--;
            }
            if (catchCooldown == 0 && canSkill()) {
                performCatchSkill();
                catchCooldown = 600;
            }
            if (chargeTicksLeft > 0 && canSkill()) {
                // 1. 倒计时
                chargeTicksLeft--;
                // 2. 保持朝向
                float yaw = (float) Math.toDegrees(Math.atan2(-chargeDir.x, chargeDir.z));
                this.setYRot(yaw);
                this.yHeadRot = yaw;

                // 3. 位移：每 gt 走 0.55 格（可调）
                Vec3 step = chargeDir.scale(0.65);
                this.move(MoverType.SELF, step);
                // 4. 撞击检测：以当前碰撞盒稍扩大一点
                AABB hitBox = this.getBoundingBox().inflate(8);
                List<LivingEntity> list = level().getEntities(this, hitBox,
                                e -> e instanceof LivingEntity living && EntityUtil.isValidCombatTarget(this, living))
                        .stream()
                        .map(e -> (LivingEntity) e)
                        .toList();


                for (LivingEntity e : list) {
                    e.hurtServer((ServerLevel)this.level(), this.damageSources().mobAttack(this), 10.0F);
                    e.knockback(1.5, chargeDir.x, chargeDir.z);
                }

                // 5. 结束处理
                if (chargeTicksLeft == 0) {
                    chargeDir = null;
                }
            }
            if (this.getTarget() != null
                    && this.getTarget().distanceTo(this) >= 16
                    && canSkill()
                    && remoteCooldown <= 0) {
                if (this.getRandom().nextBoolean()) {
                    this.performSonicBoomSkill();
                } else {
                    this.performChargeSkill();
                }
                this.setHasSkill(true);
                remoteCooldown = 120;
            }
            if (remoteCooldown > 0) {
                remoteCooldown--;
            }
            if (this.spawnAnimTick < 10) {
                this.spawnAnimTick++;
            } else if (this.spawnAnimTick == 10){
                this.spawnAnimTick = 11;
                if (this.isSpawnAnimEnd()) {
                    this.setNoAi(false);
                }
            }

            if (!this.isSpawnAnimEnd()) {
                this.setNoAi(true);
                this.setInvulnerable(true);
            }
            if (this.tickCount > 220 && !this.isSpawnAnimEnd()) {
                this.setSpawnAnim(true);
                this.setNoAi(false);
                this.setInvulnerable(false);
            }
            if (this.isSpawnAnimEnd() && !hasSkill()) {
                // 冷却递减
                int cd = getSkillCooldown();
                if (cd > 0) setSkillCooldown(cd - 1);
            }
        }
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (!this.isDeadOrDying() && !this.isNoAi()) super.knockback(strength, x, z);
    }
    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!this.level().isClientSide() && canSkill()) {
            setHasSkill(true);
            int index = this.getRandom().nextInt(skills.size());
            skills.get(index).run();
        }
        return super.doHurtTarget(world, target);
    }
    public static AttributeSupplier.Builder createDeepCreatureAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4F)
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.ARMOR, 5.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.ARMOR_TOUGHNESS, 5.0)
                .add(Attributes.MAX_HEALTH, 13000.0) // 确保添加最大生命值属性
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
    }
    private final List<Runnable> skills = List.of(
            this::performRoarSkill,
            this::performEarthquakeSkill,
            this::performLeftSmashSkill,
            this::performRightSmashSkill,
            this::performLeftSideStrikeSkill,
            this::performRightSideStrikeSkill,
            this::performSonicBoomSkill,
            this::performChargeSkill,
            this::performJumpSkill
    );
    private void performRoarSkill() {
        setSkillCooldown(20);
        this.setHasSkill(true);
        this.setNoAi(true);
        this.triggerAnim("skill_controller", "roar");
    }
    private void performEarthquakeSkill() {
        setSkillCooldown(20);
        this.setNoAi(true);
        this.setHasSkill(true);
        this.triggerAnim("skill_controller", "earthquake");
    }
    public void performLeftSmashSkill() {
        setSkillCooldown(20);
        this.setHasSkill(true);
        this.setNoAi(true);
        this.triggerAnim("skill_controller", "lefts_smash");
    }
    public void performRightSmashSkill() {
        setSkillCooldown(20);
        this.setHasSkill(true);
        this.setNoAi(true);
        this.triggerAnim("skill_controller", "rights_smash");
    }
    public void performLeftSideStrikeSkill() {
        setSkillCooldown(20);
        this.setHasSkill(true);
        this.setNoAi(true);
        this.triggerAnim("skill_controller", "left_side_strike");
    }
    public void performRightSideStrikeSkill() {
        setSkillCooldown(20);
        this.setHasSkill(true);
        this.setNoAi(true);
        this.triggerAnim("skill_controller", "right_side_strike");
    }
    public void performSonicBoomSkill() {
        setSkillCooldown(20);
        this.setHasSkill(true);
        this.setNoAi(true);
        this.triggerAnim("skill_controller", "sonic_boom");
    }
    public void performChargeSkill() {
        setSkillCooldown(20);
        this.setHasSkill(true);
        this.setNoAi(true);
        this.triggerAnim("skill_controller", "charge");
    }
    public void performJumpSkill() {
        setSkillCooldown(20);
        this.setHasSkill(true);
        this.triggerAnim("skill_controller", "jump");
    }
    public void performCatchSkill() {
        setSkillCooldown(20);
        this.setNoAi(true);
        this.setHasSkill(true);
        this.triggerAnim("skill_controller", "catch");
    }

    public boolean hasSkill() {
        return getEntityData().get(HAS_SKILL);
    }
    public boolean isSpawnAnimEnd() {
        return getEntityData().get(SPAWN_ANIM_BOOLEAN);
    }
    public int getSkillCooldown() {
        return getEntityData().get(SKILL_COOLDOWN);
    }
    public int getGrabTargetId() {
        return getEntityData().get(GRAB_TARGET_ID);
    }
    public void setGrabTargetId(int id) {
        getEntityData().set(GRAB_TARGET_ID, id);
    }
    public void setHasSkill(boolean hasSkill) {
        getEntityData().set(HAS_SKILL, hasSkill);
    }
    public void setSpawnAnim(boolean spawnAnim) {
        getEntityData().set(SPAWN_ANIM_BOOLEAN, spawnAnim);
    }
    public void setSkillCooldown(int cooldown) {
        getEntityData().set(SKILL_COOLDOWN, cooldown);
    }

    @Override
    public boolean canSkill() {
        if (!ModSkillEntityType.canSkill(this)) return false;
        return this.isSpawnAnimEnd() && !hasSkill() && getSkillCooldown() == 0;
    }
}
