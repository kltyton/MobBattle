package com.kltyton.mob_battle.mixin.client.render.entity.player;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.player.IClientPlayerEntityAccessor;
import com.kltyton.mob_battle.entity.player.IPlayerEntityAccessor;
import com.kltyton.mob_battle.entity.player.IPlayerSkillAccessor;
import com.kltyton.mob_battle.event.DataTrackersEvent;
import com.kltyton.mob_battle.network.packet.PlayerSkillUtilPayload;
import com.kltyton.mob_battle.sounds.ModSounds;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.GeckoLibUtil;

@Mixin(PlayerEntity.class)
@Implements(@Interface(iface = GeoEntity.class, prefix = "gecko$"))
public abstract class PlayerEntityMixin extends LivingEntity implements GeoEntity, IPlayerSkillAccessor {
    @Unique
    private static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    @Unique
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    @Unique
    private static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("run");
    @Unique
    private static final RawAnimation JUMP_ANIM = RawAnimation.begin().thenPlay("jump");
    @Unique
    private static final RawAnimation WAVE_ANIM = RawAnimation.begin().thenPlay("wave");
    @Unique
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    @Unique
    private static final RawAnimation ATTACK_ANIM2 = RawAnimation.begin().thenPlay("attack2");
    @Unique
    private static final RawAnimation LEFT_WHIP_ANIM = RawAnimation.begin().thenPlay("left_whip");
    @Unique
    private static final RawAnimation RETREAT_STEP_ANIM = RawAnimation.begin().thenPlay("retreat_step");
    @Unique
    private static final RawAnimation TOP_KNEE_ANIM = RawAnimation.begin().thenPlay("top_knee");
    @Unique
    private static final RawAnimation COLLISION_ANIM = RawAnimation.begin().thenPlay("collision");
    @Unique
    private static final RawAnimation RUN_COLLISION_ANIM = RawAnimation.begin().thenPlay("run_collision");
    @Unique
    private static final RawAnimation SMASHING_THE_GROUND_ANIM = RawAnimation.begin().thenPlay("smashing_the_ground");
    @Unique
    private static final RawAnimation SCRAPING_ANIM = RawAnimation.begin().thenPlay("scraping");
    @Unique
    private static final RawAnimation NO_SCRAPING_ANIM = RawAnimation.begin().thenPlay("no_scraping");
    @Unique
    private static final RawAnimation YES_SCRAPING_ANIM = RawAnimation.begin().thenPlay("yes_scraping");

    @Unique
    private static final TrackedData<Integer> ATTACK_COOLDOWN = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Unique
    private static final TrackedData<Integer> ATTACK_COOLDOWN2 = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Unique
    private static final TrackedData<Integer> LEFT_WHIP_COOLDOWN = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Unique
    private static final TrackedData<Integer> RETREAT_STEP_COOLDOWN = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Unique
    private static final TrackedData<Integer> TOP_KNEE_COOLDOWN = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Unique
    private static final TrackedData<Integer> COLLISION_COOLDOWN = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Unique
    private static final TrackedData<Integer> RUN_COLLISION_COOLDOWN = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Unique
    private static final TrackedData<Integer> SMASHING_THE_GROUND_COOLDOWN = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Unique
    private static final TrackedData<Integer> SCRAPING_COOLDOWN = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Unique
    private static final TrackedData<Boolean> ISCOLLIDING = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private LivingEntity collidingEntity = null;
    @Unique
    private LivingEntity grabbedEntity = null;
    @Unique
    private boolean isColliding() {
        return this.dataTracker.get(ISCOLLIDING);
    }
    @Unique
    @Override
    public void mobBattle$startCollision() {
        this.dataTracker.set(ISCOLLIDING, true);
    }

    @Unique
    @Override
    public void mobBattle$stopCollision() {
        this.dataTracker.set(ISCOLLIDING, false);
        this.collidingEntity = null;
    }
    @Unique
    @Override
    public void mobBattle$setGrabbedEntity(LivingEntity grabbedEntity) {
        this.grabbedEntity = grabbedEntity;
    }
    @Unique
    @Override
    public LivingEntity mobBattle$getGrabbedEntity() {
        return this.grabbedEntity;
    }

    @Inject(method = "initDataTracker", at = @At("RETURN"))
    protected void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(DataTrackersEvent.HAS_SKILL, false);
        builder.add(DataTrackersEvent.CAN_MOVE, true);
        builder.add(ISCOLLIDING, false);
        builder.add(ATTACK_COOLDOWN, getMaxAttackCooldown("attack"));
        builder.add(ATTACK_COOLDOWN2, getMaxAttackCooldown("attack2"));
        builder.add(RETREAT_STEP_COOLDOWN, getMaxAttackCooldown("retreat_step"));
        builder.add(LEFT_WHIP_COOLDOWN, getMaxAttackCooldown("left_whip"));
        builder.add(TOP_KNEE_COOLDOWN, getMaxAttackCooldown("top_knee"));
        builder.add(COLLISION_COOLDOWN, getMaxAttackCooldown("collision"));
        builder.add(RUN_COLLISION_COOLDOWN, getMaxAttackCooldown("run_collision"));
        builder.add(SMASHING_THE_GROUND_COOLDOWN, getMaxAttackCooldown("smashing_the_ground"));
        builder.add(SCRAPING_COOLDOWN, getMaxAttackCooldown("scraping"));
    }
    // 在 PlayerEntityMixin.java 中添加
    @Override
    public void changeLookDirection(double cursorDeltaX, double cursorDeltaY) {
        if (this.isColliding()) {
            return;
        }
        super.changeLookDirection(cursorDeltaX, cursorDeltaY);
    }
    @Inject(method = "tick", at = @At("RETURN"))
    public void mobBattle$tickCollision(CallbackInfo ci) {
        if (!this.getWorld().isClient) {
            // 1. 冲锋位移逻辑
            Vec3d lookVec = this.getRotationVec(1.0F);
            Vec3d velocity = new Vec3d(lookVec.x, 0, lookVec.z).normalize().multiply(1.5); // 冲锋速度 0.5
            if (this.grabbedEntity != null) {
                if (!this.grabbedEntity.isAlive()) {
                    this.grabbedEntity = null;
                } else {
                    Vec3d targetPos = this.getPos().add(velocity.multiply(1.5));
                    this.grabbedEntity.requestTeleport(targetPos.x, targetPos.y, targetPos.z);
                }
            }
        }
        if (this.isColliding()) {
            if (this.getWorld().isClient) {
                // 1. 冲锋位移逻辑
                Vec3d lookVec = this.getRotationVec(1.0F);
                Vec3d velocity = new Vec3d(lookVec.x, 0, lookVec.z).normalize().multiply(1.5); // 冲锋速度 0.5
                this.setVelocity(velocity.x, this.getVelocity().y, velocity.z);
                this.velocityDirty = true;
            } else {
                // 1. 冲锋位移逻辑
                Vec3d lookVec = this.getRotationVec(1.0F);
                Vec3d velocity = new Vec3d(lookVec.x, 0, lookVec.z).normalize().multiply(1.5); // 冲锋速度 0.5
                this.setVelocity(velocity.x, this.getVelocity().y, velocity.z);
                this.velocityDirty = true;
                // 2. 抓取逻辑
                if (this.collidingEntity == null) {
                    // 探测前方 1.5 格内的生物
                    LivingEntity target = EntityUtil.getClosestNearbyEntity((PlayerEntity) (Object) this, LivingEntity.class, 5, EntityUtil.TeamFilter.EXCLUDE_TEAM);
                    if (target != null) {
                        this.collidingEntity = target;
                    }
                } else {
                    // 3. 维持被抓取生物的位置 & 伤害
                    if (!this.collidingEntity.isAlive()) {
                        this.collidingEntity = null;
                    } else {
                        // 将生物固定在玩家前方 1.2 格处
                        Vec3d targetPos = this.getPos().add(velocity.multiply(3.5));
                        this.collidingEntity.requestTeleport(targetPos.x, targetPos.y + 4, targetPos.z);
                        // 每 20 刻给予 70 点伤害
                        if (this.age % 20 == 0) {
                            this.collidingEntity.damage((ServerWorld) this.getWorld(), this.getDamageSources().playerAttack((PlayerEntity) (Object) this), 70f);
                            this.playSound(ModSounds.PLAYER_ATTACK_4_SOUND_EVENT);
                        }
                    }
                }
            }
        }
    }
    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (!this.getWorld().isClient) {
            if (!mobBattle$hasSkill()) {
                this.mobBattle$setAttackCooldown("attack", Math.max(0, this.mobBattle$getAttackCooldown("attack") - 1));
                this.mobBattle$setAttackCooldown("retreat_step", Math.max(0, this.mobBattle$getAttackCooldown("retreat_step") - 1));
                this.mobBattle$setAttackCooldown("attack2", Math.max(0, this.mobBattle$getAttackCooldown("attack2") - 1));
                this.mobBattle$setAttackCooldown("left_whip", Math.max(0, this.mobBattle$getAttackCooldown("left_whip") - 1));
                this.mobBattle$setAttackCooldown("top_knee", Math.max(0, this.mobBattle$getAttackCooldown("top_knee") - 1));
                this.mobBattle$setAttackCooldown("collision", Math.max(0, this.mobBattle$getAttackCooldown("collision") - 1));
                this.mobBattle$setAttackCooldown("run_collision", Math.max(0, this.mobBattle$getAttackCooldown("run_collision") - 1));
                this.mobBattle$setAttackCooldown("smashing_the_ground", Math.max(0, this.mobBattle$getAttackCooldown("smashing_the_ground") - 1));
                this.mobBattle$setAttackCooldown("scraping", Math.max(0, this.mobBattle$getAttackCooldown("scraping") - 1));
            }
        }
    }
    @Unique
    public int mobBattle$getAttackCooldown(String animationName) {
        return switch (animationName) {
            case "attack" -> this.getDataTracker().get(ATTACK_COOLDOWN);
            case "attack2" -> this.getDataTracker().get(ATTACK_COOLDOWN2);
            case "retreat_step" -> this.getDataTracker().get(RETREAT_STEP_COOLDOWN);
            case "left_whip" -> this.getDataTracker().get(LEFT_WHIP_COOLDOWN);
            case "top_knee" -> this.getDataTracker().get(TOP_KNEE_COOLDOWN);
            case "collision" -> this.getDataTracker().get(COLLISION_COOLDOWN);
            case "run_collision" -> this.getDataTracker().get(RUN_COLLISION_COOLDOWN);
            case "smashing_the_ground" -> this.getDataTracker().get(SMASHING_THE_GROUND_COOLDOWN);
            case "scraping" -> this.getDataTracker().get(SCRAPING_COOLDOWN);
            default -> 1;
        };
    }
    @Unique
    public void mobBattle$setAttackCooldown(String controllerName, int cooldown) {
        switch (controllerName) {
            case "attack":
                this.getDataTracker().set(ATTACK_COOLDOWN, cooldown);
                break;
            case "attack2":
                this.getDataTracker().set(ATTACK_COOLDOWN2, cooldown);
                break;
            case "retreat_step":
                this.getDataTracker().set(RETREAT_STEP_COOLDOWN, cooldown);
                break;
            case "left_whip":
                this.getDataTracker().set(LEFT_WHIP_COOLDOWN, cooldown);
                break;
            case "top_knee":
                this.getDataTracker().set(TOP_KNEE_COOLDOWN, cooldown);
                break;
            case "collision":
                this.getDataTracker().set(COLLISION_COOLDOWN, cooldown);
                break;
            case "run_collision":
                this.getDataTracker().set(RUN_COLLISION_COOLDOWN, cooldown);
                break;
            case "smashing_the_ground":
                this.getDataTracker().set(SMASHING_THE_GROUND_COOLDOWN, cooldown);
                break;
            case "scraping":
                this.getDataTracker().set(SCRAPING_COOLDOWN, cooldown);
                break;
        }
    }
    @Unique
    private static int getMaxAttackCooldown(String animationName) {
        return switch (animationName) {
            case "attack" -> 40;
            case "attack2" -> 20;
            case "retreat_step" -> 100;
            case "left_whip" -> 60;
            case "top_knee" -> 200;
            case "collision" -> 440;
            case "run_collision" -> 500;
            case "smashing_the_ground" -> 600;
            case "scraping" -> 1200;
            default -> 1;
        };
    }
    public void gecko$registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 5 ,this::animationController));
        controllers.add(new AnimationController<>("jump_controller", state -> PlayState.STOP)
                .triggerableAnim("jump", JUMP_ANIM));
        controllers.add(new AnimationController<>("wave_controller", state -> PlayState.STOP)
                .triggerableAnim("wave", WAVE_ANIM));
        controllers.add(new AnimationController<>("attack_controller", animTest -> {
                    if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED && animTest.controller().getCurrentRawAnimation() != SCRAPING_ANIM) {
                        if (this.mobBattle$hasSkill()) {
                            ((IClientPlayerEntityAccessor)this).clientSend("stop");
                            ((IClientPlayerEntityAccessor)this).clientSend("can_move");
                            ((IClientPlayerEntityAccessor)this).setPerson(1);
                        }
                    }
                    return PlayState.STOP;
                })
                .triggerableAnim("attack", ATTACK_ANIM)
                .triggerableAnim("attack2", ATTACK_ANIM2)
                .triggerableAnim("retreat_step", RETREAT_STEP_ANIM)
                .triggerableAnim("left_whip", LEFT_WHIP_ANIM)
                .triggerableAnim("top_knee", TOP_KNEE_ANIM)
                .triggerableAnim("collision", COLLISION_ANIM)
                .triggerableAnim("run_collision", RUN_COLLISION_ANIM)
                .triggerableAnim("smashing_the_ground", SMASHING_THE_GROUND_ANIM)
                .triggerableAnim("scraping", SCRAPING_ANIM)
                .triggerableAnim("no_scraping", NO_SCRAPING_ANIM)
                .triggerableAnim("yes_scraping", YES_SCRAPING_ANIM)
                .setSoundKeyframeHandler(s -> {
                    switch (s.keyframeData().getSound()) {
                        case "runAttack":
                            ClientUtil.getClientPlayer().playSound(
                                    ModSounds.PLAYER_ATTACK_SOUND_EVENT,
                                    1.0F, 1.0F
                            );
                            break;
                        case "runAttack2":
                            ClientUtil.getClientPlayer().playSound(
                                    ModSounds.PLAYER_ATTACK_4_SOUND_EVENT,
                                    1.0f, 1.0f
                            );
                            break;
                        case "runRetreat_step":
                            ClientUtil.getClientPlayer().playSound(
                                    SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                                    1.0f, 1.0f
                            );
                            break;
                        case "runLeftWhip":
                            ClientUtil.getClientPlayer().playSound(
                                    SoundEvents.ITEM_FIRECHARGE_USE,
                                    1.0f, 1.0f
                            );
                            break;
                        case "runRunCollision":
                            ClientUtil.getClientPlayer().playSound(
                                    SoundEvents.ITEM_MACE_SMASH_GROUND,
                                    1.0f, 1.0f
                            );
                            break;
                        case "runJump":
                            ClientUtil.getClientPlayer().playSound(
                                    SoundEvents.ENTITY_PLAYER_HURT,
                                    1.0f, 1.0f
                            );
                            break;
                    }
                })
                .setCustomInstructionKeyframeHandler(s -> {
                    if ("runAttack;".equals(s.keyframeData().getInstructions())) {
                        ((IClientPlayerEntityAccessor)this).clientSend("attack");
                    }
                    if ("runAttack2;".equals(s.keyframeData().getInstructions())) {
                        ((IClientPlayerEntityAccessor)this).clientSend("attack2");
                    }
                    if ("runLeftWhip;".equals(s.keyframeData().getInstructions())) {
                        ((IClientPlayerEntityAccessor)this).clientSend("left_whip");
                    }
                    if ("runTopKnee;".equals(s.keyframeData().getInstructions())) {
                        ((IClientPlayerEntityAccessor)this).clientSend("top_knee");
                    }
                    if ("runUpperHook;".equals(s.keyframeData().getInstructions())) {
                        ((IClientPlayerEntityAccessor)this).clientSend("upper_hook");
                    }
                    if ("runCollisionStart;".equals(s.keyframeData().getInstructions())) {
                        // 通知服务端开始冲锋物理计算
                        ((IClientPlayerEntityAccessor)this).clientSend("collision_start");
                    }
                    if ("runCollisionEnd;".equals(s.keyframeData().getInstructions())) {
                        // 通知服务端结束冲锋物理计算
                        ((IClientPlayerEntityAccessor)this).clientSend("collision_end");
                    }
                    if ("runRunCollision;".equals(s.keyframeData().getInstructions())) {
                        // 触发服务端开始冲锋物理计算
                        ((IClientPlayerEntityAccessor)this).clientSend("run_collision");
                    }
                    if ("runSmashingTheGround;".equals(s.keyframeData().getInstructions())) {
                        ((IClientPlayerEntityAccessor)this).clientSend("smashing_the_ground");
                    }
                    if ("runJump;".equals(s.keyframeData().getInstructions())) {
                        this.velocityDirty = true;
                        this.addVelocity(0, 1.3, 0);
                        ((IClientPlayerEntityAccessor)this).clientSend("run_jump");
                    }
                    if ("runScraping;".equals(s.keyframeData().getInstructions())) {
                        ((IClientPlayerEntityAccessor)this).clientSend("scraping");
                    }
                    if ("runScrapingAttack;".equals(s.keyframeData().getInstructions())) {
                        ((IClientPlayerEntityAccessor)this).clientSend("scraping_attack");
                    }
                    if ("runScrapingEnd;".equals(s.keyframeData().getInstructions())) {
                        ((IClientPlayerEntityAccessor)this).clientSend("scraping_end");
                    }
                })
        );
    }


    @Unique
    public boolean mobBattle$hasSkill() {
        return this.getDataTracker().get(DataTrackersEvent.HAS_SKILL);
    }
    @Unique
    public void mobBattle$setHasSkill(boolean hasSkill) {
        this.getDataTracker().set(DataTrackersEvent.HAS_SKILL, hasSkill);
    }
    @Unique
    public boolean mobBattle$canMove() {
        return this.getDataTracker().get(DataTrackersEvent.CAN_MOVE);
    }
    @Unique
    public void mobBattle$setCanMove(boolean canMove) {
        this.getDataTracker().set(DataTrackersEvent.CAN_MOVE, canMove);
    }
    @Unique
    public boolean mobBattle$canAttack(String animationName) {
        if (!ModSkillEntityType.canSkill(this)) return false;
        return this.mobBattle$getAttackCooldown(animationName) <= 0 && !mobBattle$hasSkill();
    }
    // 重写 getStackInHand(OFF_HAND) -> EMPTY
    @Override
    public ItemStack getStackInHand(Hand hand) {
        if (((IPlayerEntityAccessor)this).isUsingGeckoLib() && hand == Hand.OFF_HAND) {
            return ItemStack.EMPTY;
        }
        return super.getStackInHand(hand);
    }
    @Override
    public void setStackInHand(Hand hand, ItemStack stack) {
        if (((IPlayerEntityAccessor)this).isUsingGeckoLib()) {
            return;
        }
        super.setStackInHand(hand, stack);
    }
    // 使用物品时忽略副手
    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    public void interactEntity(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (((IPlayerEntityAccessor)this).isUsingGeckoLib() && hand == Hand.OFF_HAND) {
            cir.cancel();
            cir.setReturnValue(ActionResult.FAIL);
        }
    }
    @Unique
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    @Inject(
            method = "travel",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onTravel(Vec3d movementInput, CallbackInfo ci) {
        if (!this.getWorld().isClient) return;
        if (!mobBattle$canMove()) {
            super.travel(Vec3d.ZERO);
            ci.cancel();
        }
    }
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "attack", at = @At("HEAD"))
    public void attack(Entity target, CallbackInfo ci) {
        if (((IPlayerEntityAccessor)this).isUsingGeckoLib()) mobBattle$runAttack("attack", false);
    }

    @Unique
    public void mobBattle$runAttack(String controllerName, boolean canMove) {
        if (this.mobBattle$canAttack(controllerName)) {
            this.mobBattle$setHasSkill(true);
            this.mobBattle$setCanMove(canMove);
            this.triggerAnim("attack_controller", controllerName);
            this.mobBattle$setAttackCooldown(controllerName, getMaxAttackCooldown(controllerName));
            // 新增：切换到第三人称后视角（客户端专属）
            if (this.getWorld().isClient) {
                ((IClientPlayerEntityAccessor)this).setPerson(2);
            } else {
                ServerPlayNetworking.send((ServerPlayerEntity)(Object)this, new PlayerSkillUtilPayload("setPerson_2"));
            }
        }
    }


    @Unique
    private PlayState animationController(AnimationTest<?> state) {
        // 优先级最高的条件放前面
        if (this.isSprinting()) {  // 奔跑
            return state.setAndContinue(RUN_ANIM);
        }
        if (state.isMoving()) {  // 走路
            return state.setAndContinue(WALK_ANIM);
        }
        return state.setAndContinue(IDEA_ANIM);
    }
    @Override
    public void jump() {
        super.jump();
        this.triggerAnim("jump_controller", "jump");
    }
    @Override
    public void swingHand(Hand hand) {
        super.swingHand(hand);
        this.triggerAnim("wave_controller", "wave");
    }
    public AnimatableInstanceCache gecko$getAnimatableInstanceCache() {
        return geoCache;
    }
}
