package com.kltyton.mob_battle.entity.deepcreature;

import com.kltyton.mob_battle.entity.deepcreature.goal.DeepCreatureEntityNavigation;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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

public class DeepCreatureEntity extends HostileEntity implements GeoEntity {

    private final ServerBossBar bossBar = new ServerBossBar(
            this.getDisplayName(),
            BossBar.Color.PURPLE,
            BossBar.Style.PROGRESS
    );
    public static final TrackedData<Boolean> SPAWN_ANIM_BOOLEAN = DataTracker.registerData(DeepCreatureEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(DeepCreatureEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN = DataTracker.registerData(DeepCreatureEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> GRAB_TARGET_ID = DataTracker.registerData(DeepCreatureEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public DeepCreatureEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setHasSkill(false);
        this.setSkillCooldown(0);
    }

    @Override
    public void setCustomName(@Nullable Text name) {
        super.setCustomName(name);
        this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
    }
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SPAWN_ANIM_BOOLEAN, false);
        builder.add(HAS_SKILL, false);
        builder.add(SKILL_COOLDOWN, 0);
        builder.add(GRAB_TARGET_ID, -1);
    }
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }
    @Override
    public boolean isInAttackRange(LivingEntity entity) {
        Box attackBox = this.getAttackBox().expand(5.0); // 扩大3格攻击距离
        return attackBox.intersects(entity.getHitbox());
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
    protected void initGoals() {
        this.targetSelector.add(1, new RevengeGoal(this));// 添加复仇目标
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true)); // 添加主动攻击玩家目标
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, true)); // 添加僵尸攻击目标
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0)); // 添加远距离游荡目标
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 64.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
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
    protected void mobTick(ServerWorld world) {
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
    }
    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }
    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 5 ,this::animationController)
                .setSoundKeyframeHandler(s -> {
            PlayerEntity player = ClientUtil.getClientPlayer();
            if ("minecraft:entity.polar_bear.warning".equals(s.keyframeData().getSound())) {
                player.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 5.0F, 0.85F);
            }
            if ("minecraft:entity.ender_dragon.growl".equals(s.keyframeData().getSound())) {
                player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 5.0F, 0.85F);
            }
        }).triggerableAnim("death", DEAD_ANIM));
        controllers.add(new AnimationController<>("skill_controller",animTest -> {
            animTest.renderState().addGeckolibData(DeepCreatureEntityRenderer.ENTITY_ID, this.getId());
            if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED && this.hasSkill()) {
                ClientPlayNetworking.send(new SkillPayload(
                        "stop", this.getId()
                ));
            }
            return PlayState.STOP;
        }).setSoundKeyframeHandler(s -> {
            PlayerEntity player = ClientUtil.getClientPlayer();
            if ("minecraft:entity.polar_bear.warning".equals(s.keyframeData().getSound())) {
                player.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 5.0F, 0.85F);
            }
            if ("minecraft:entity.ender_dragon.growl".equals(s.keyframeData().getSound())) {
                player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 5.0F, 0.85F);
            }
            if ("runSideSound".equals(s.keyframeData().getSound())) {
                player.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.0F, 0.5F);
                player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 0.2F);
                if (this.getGrabTargetId() != -1) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "damage", this.getId()
                    ));
                }
            }
            if ("runLaunch".equals(s.keyframeData().getSound())) {
                player.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0F, 0.5F);
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
                PlayerEntity player = ClientUtil.getClientPlayer();
                player.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.0F, 0.7F);
                player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 0.7F);
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
                PlayerEntity player = ClientUtil.getClientPlayer();
                player.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE.value(), 1.0F, 0.7F);
            }
            if ("runSide".equals(s.keyframeData().getInstructions())) {
                ClientPlayNetworking.send(new SkillPayload(
                        "side", this.getId()
                ));
                PlayerEntity player = ClientUtil.getClientPlayer();
                player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER.value(), 1.0F, 0.7F);
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
                PlayerEntity player = ClientUtil.getClientPlayer();
                player.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE.value(), 1.0F, 0.7F);
            }
            if ("runSmashGround_S".equals(s.keyframeData().getInstructions())) {
                ClientPlayNetworking.send(new SkillPayload(
                        "smash_ground_s", this.getId()
                ));
                PlayerEntity player = ClientUtil.getClientPlayer();
                player.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE.value(), 1.0F, 0.7F);
            }
            if ("runSmashGround_XL".equals(s.keyframeData().getInstructions())) {
                ClientPlayNetworking.send(new SkillPayload(
                        "smash_ground_xl", this.getId()
                ));
                PlayerEntity player = ClientUtil.getClientPlayer();
                player.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE.value(), 1.0F, 0.7F);
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
    public void readCustomData(ReadView nbt) {
        super.readCustomData(nbt);
        this.setSpawnAnim(nbt.getBoolean("SpawnAnimBoolean", false));
        if (this.hasCustomName()) {
            this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
        }
    }
    @Override
    public void writeCustomData(WriteView nbt) {
        super.writeCustomData(nbt);
        nbt.putBoolean("SpawnAnimBoolean", this.isSpawnAnimEnd());
    }

    @Override
    public void setHealth(float health) {
        if (this.bossBar != null) {
            this.bossBar.setPercent(health / this.getMaxHealth());
            this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
        }
        if (health <= 0.0F) {
            super.setHealth(0.1F);
            this.setAiDisabled(true);
            this.triggerAnim("main_controller", "death");
        } else {
            super.setHealth(health);
        }
    }
    @Override
    protected EntityNavigation createNavigation(World world) {
        return new DeepCreatureEntityNavigation(this, world);
    }

    private PlayState animationController(final AnimationTest<DeepCreatureEntity> state) {
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
        if (this.age < 220 && !this.isSpawnAnimEnd()) {
            return state.setAndContinue(SPAWN_ANIM);
        }
        // 否则，如果在移动，播放walk动画，否则idle动画
        if (this.getVelocity().lengthSquared() > 0.015) {
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
    public Vec3d chargeDir = null;
    // 剩余冲刺 tick
    public int chargeTicksLeft = 0;
    public int stuckCooldown = 200;
    public int catchCooldown = 600;
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            if (getGrabTargetId() != -1 && this.age % 20 == 0) {
                this.getTarget().damage((ServerWorld) this.getWorld(), this.getTarget().getDamageSources().magic(), 150F);
            }
            if (isAiDisabled()) {
                this.setInvulnerable(true);
                if (stuckCooldown > 0) {
                    stuckCooldown--;
                } else {
                    this.setAiDisabled(false);
                    stuckCooldown = 200;
                }
            } else {
                this.setInvulnerable(false);
                stuckCooldown = 200;
            }
            if (catchCooldown > 0) {
                catchCooldown--;
            }
            if (catchCooldown == 0 && this.isSpawnAnimEnd() && !hasSkill() && getSkillCooldown() == 0) {
                performCatchSkill();
                catchCooldown = 600;
            }
            if (chargeTicksLeft > 0 && this.isSpawnAnimEnd() && !hasSkill() && getSkillCooldown() == 0) {
                // 1. 倒计时
                chargeTicksLeft--;
                // 2. 保持朝向
                float yaw = (float) Math.toDegrees(Math.atan2(-chargeDir.x, chargeDir.z));
                this.setYaw(yaw);
                this.headYaw = yaw;

                // 3. 位移：每 gt 走 0.55 格（可调）
                Vec3d step = chargeDir.multiply(0.65);
                this.move(MovementType.SELF, step);
                // 4. 撞击检测：以当前碰撞盒稍扩大一点
                Box hitBox = this.getBoundingBox().expand(8);
                List<LivingEntity> list = getWorld().getOtherEntities(this, hitBox,
                                e -> e instanceof LivingEntity && e.isAlive() && e != this)
                        .stream()
                        .map(e -> (LivingEntity) e)
                        .toList();


                for (LivingEntity e : list) {
                    // 给自己 2 tick 无敌帧，避免同一帧多次伤害
                    if (e.timeUntilRegen <= 0) {
                        e.damage((ServerWorld)this.getWorld(), this.getDamageSources().mobAttack(this), 10.0F);
                        e.takeKnockback(1.5, chargeDir.x, chargeDir.z);
                        e.timeUntilRegen = 2;
                    }
                }

                // 5. 结束处理
                if (chargeTicksLeft == 0) {
                    chargeDir = null;
                }
            }
            if (this.getTarget() != null
                    && this.getTarget().distanceTo(this) >= 16
                    && this.isSpawnAnimEnd()
                    && !hasSkill()
                    && getSkillCooldown() == 0
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
                    this.setAiDisabled(false);
                }
            }

            if (!this.isSpawnAnimEnd()) {
                this.setAiDisabled(true);
                this.setInvulnerable(true);
            }
            if (this.age > 220 && !this.isSpawnAnimEnd()) {
                this.setSpawnAnim(true);
                this.setAiDisabled(false);
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
    public void takeKnockback(double strength, double x, double z) {
        if (!this.isDead() && !this.isAiDisabled()) super.takeKnockback(strength, x, z);
    }
    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        if (!this.getWorld().isClient() && !hasSkill() && getSkillCooldown() == 0) {
            setHasSkill(true);
            int index = this.getRandom().nextInt(skills.size());
            skills.get(index).run();
        }
        return super.tryAttack(world, target);
    }
    public static DefaultAttributeContainer.Builder createDeepCreatureAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.FOLLOW_RANGE, 64.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.4F)
                .add(EntityAttributes.ATTACK_DAMAGE, 0.0)
                .add(EntityAttributes.ARMOR, 5.0)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 1)
                .add(EntityAttributes.ARMOR_TOUGHNESS, 5.0)
                .add(EntityAttributes.MAX_HEALTH, 13000.0) // 确保添加最大生命值属性
                .add(EntityAttributes.SPAWN_REINFORCEMENTS);
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
        this.setAiDisabled(true);
        this.triggerAnim("skill_controller", "roar");
    }
    private void performEarthquakeSkill() {
        setSkillCooldown(20);
        this.setAiDisabled(true);
        this.setHasSkill(true);
        this.triggerAnim("skill_controller", "earthquake");
    }
    public void performLeftSmashSkill() {
        setSkillCooldown(20);
        this.setHasSkill(true);
        this.setAiDisabled(true);
        this.triggerAnim("skill_controller", "lefts_smash");
    }
    public void performRightSmashSkill() {
        setSkillCooldown(20);
        this.setHasSkill(true);
        this.setAiDisabled(true);
        this.triggerAnim("skill_controller", "rights_smash");
    }
    public void performLeftSideStrikeSkill() {
        setSkillCooldown(20);
        this.setHasSkill(true);
        this.setAiDisabled(true);
        this.triggerAnim("skill_controller", "left_side_strike");
    }
    public void performRightSideStrikeSkill() {
        setSkillCooldown(20);
        this.setHasSkill(true);
        this.setAiDisabled(true);
        this.triggerAnim("skill_controller", "right_side_strike");
    }
    public void performSonicBoomSkill() {
        setSkillCooldown(20);
        this.setHasSkill(true);
        this.setAiDisabled(true);
        this.triggerAnim("skill_controller", "sonic_boom");
    }
    public void performChargeSkill() {
        setSkillCooldown(20);
        this.setHasSkill(true);
        this.setAiDisabled(true);
        this.triggerAnim("skill_controller", "charge");
    }
    public void performJumpSkill() {
        setSkillCooldown(20);
        this.setHasSkill(true);
        this.triggerAnim("skill_controller", "jump");
    }
    public void performCatchSkill() {
        setSkillCooldown(20);
        this.setAiDisabled(true);
        this.setHasSkill(true);
        this.triggerAnim("skill_controller", "catch");
    }

    public boolean hasSkill() {
        return getDataTracker().get(HAS_SKILL);
    }
    public boolean isSpawnAnimEnd() {
        return getDataTracker().get(SPAWN_ANIM_BOOLEAN);
    }
    public int getSkillCooldown() {
        return getDataTracker().get(SKILL_COOLDOWN);
    }
    public int getGrabTargetId() {
        return getDataTracker().get(GRAB_TARGET_ID);
    }
    public void setGrabTargetId(int id) {
        getDataTracker().set(GRAB_TARGET_ID, id);
    }
    public void setHasSkill(boolean hasSkill) {
        getDataTracker().set(HAS_SKILL, hasSkill);
    }
    public void setSpawnAnim(boolean spawnAnim) {
        getDataTracker().set(SPAWN_ANIM_BOOLEAN, spawnAnim);
    }
    public void setSkillCooldown(int cooldown) {
        getDataTracker().set(SKILL_COOLDOWN, cooldown);
    }
}
