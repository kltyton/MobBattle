package com.kltyton.mob_battle.entity.irongolem.hulkbuster;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.accessor.BigBossLookControl;
import com.kltyton.mob_battle.entity.accessor.BigBossMoveControl;
import com.kltyton.mob_battle.entity.accessor.BigBossNavigation;
import com.kltyton.mob_battle.entity.irongolem.ModBaseIronGolemEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
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
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;
import java.util.Optional;

public class HulkbusterEntity extends IronGolemEntity implements GeoEntity, ModBaseIronGolemEntity {
    public static final int SKILL_COOLDOWN_MAX = 20;
    public static final int SUPER_ATTACK_COOLDOWN_MAX = 8 * 20;
    public static final int MINI_ATTACK_COOLDOWN_MAX = 18 * 20;
    public static final int MAX_ATTACK_COOLDOWN_MAX = 35 * 20;

    private final ServerBossBar bossBar = new ServerBossBar(
            this.getDisplayName(),
            BossBar.Color.PURPLE,
            BossBar.Style.PROGRESS
    );
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(HulkbusterEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN = DataTracker.registerData(HulkbusterEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SUPER_ATTACK_SKILL_COOLDOWN = DataTracker.registerData(HulkbusterEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> MINI_ATTACK_SKILL_COOLDOWN = DataTracker.registerData(HulkbusterEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> MAX_ATTACK_SKILL_COOLDOWN = DataTracker.registerData(HulkbusterEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SKILL, false);
        builder.add(SKILL_COOLDOWN, SKILL_COOLDOWN_MAX);
        builder.add(SUPER_ATTACK_SKILL_COOLDOWN, SUPER_ATTACK_COOLDOWN_MAX);
        builder.add(MINI_ATTACK_SKILL_COOLDOWN, MINI_ATTACK_COOLDOWN_MAX);
        builder.add(MAX_ATTACK_SKILL_COOLDOWN, MAX_ATTACK_COOLDOWN_MAX);
    }
    public HulkbusterEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
        this.setHasSkill(false);
        this.setAiDisabled(false);
        this.setSkillCooldown(SKILL_COOLDOWN_MAX);
        this.lookControl = new BigBossLookControl(this);
        this.moveControl = new BigBossMoveControl(this);
        this.navigation = new BigBossNavigation(this, world);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            // 冷却递减
            int cd = getSkillCooldown();
            if (cd > 0) setSkillCooldown(cd - 1);
            int superAttackCd = getSuperAttackSkillCooldown();
            if (superAttackCd > 0) setSuperAttackSkillCooldown(superAttackCd - 1);
            int miniAttackCd = getMiniAttackSkillCooldown();
            if (miniAttackCd > 0) setMiniAttackSkillCooldown(miniAttackCd - 1);
            int maxAttackCd = getMaxAttackSkillCooldown();
            if (maxAttackCd > 0) setMaxAttackSkillCooldown(maxAttackCd - 1);
            if (this.canMaxAttack()) {
                performMaxAttack();
            }
            if (!hasSkill()) {
                this.setAiDisabled(false);
            }
        }
    }
    @Override
    public void takeKnockback(double strength, double x, double z) {
    }
    @Override
    public void setCustomName(@Nullable Text name) {
        super.setCustomName(name);
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
    @Override
    protected void mobTick(ServerWorld world) {
        super.mobTick(world);
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
    }
    @Override
    public void readCustomData(ReadView nbt) {
        super.readCustomData(nbt);
        if (this.hasCustomName()) {
            this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
        }
    }
    @Override
    public void writeCustomData(WriteView nbt) {
        super.writeCustomData(nbt);
    }
    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        if (this.bossBar != null) {
            this.bossBar.setPercent(health / this.getMaxHealth());
            this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
        }
    }
    @Override
    public boolean isInAttackRange(LivingEntity entity) {
        Box attackBox = this.getAttackBox().expand(1.25);
        return attackBox.intersects(entity.getHitbox());
    }
    public boolean tryAttackBase(ServerWorld world, LivingEntity target) {
        return tryAttackBaseDamage(world, target, (float) this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE));
    }
    public boolean tryAttackBaseDamage(ServerWorld world, Entity target, float damage) {
        float f = damage;
        ItemStack itemStack = this.getWeaponStack();
        DamageSource damageSource = Optional.ofNullable(itemStack.getItem().getDamageSource(this)).orElse(this.getDamageSources().mobAttack(this));
        f = EnchantmentHelper.getDamage(world, itemStack, target, damageSource, f);
        f += itemStack.getItem().getBonusAttackDamage(target, f, damageSource);
        boolean bl = target.damage(world, damageSource, f);
        if (bl) {
            float g = this.getAttackKnockbackAgainst(target, damageSource);
            if (g > 0.0F && target instanceof LivingEntity livingEntity) {
                livingEntity.takeKnockback(g * 0.5F, MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)), -MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)));
                this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
            }

            if (target instanceof LivingEntity livingEntity) {
                itemStack.postHit(livingEntity, this);
                livingEntity.addStatusEffect(
                        new StatusEffectInstance(
                                ModEffects.ARMOR_PIERCING_ENTRY,
                                20,
                                0
                        ),
                        this
                );
            }

            EnchantmentHelper.onTargetDamaged(world, target, damageSource);
            this.onAttacking(target);
            this.playAttackSound();
        }

        return bl;
    }
    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        if (this.canMiniAttack()) {
            performMiniAttack();
            return true;
        } else if (this.canSuperAttack()) {
            performSuperAttack();
            return true;
        } else if (canSkill()) {
            performAttack();
            return true;
        }
        return true;
    }
    public void performAttack() {
        setHasSkill(true);
        this.setAiDisabled(true);
        setSkillCooldown(SKILL_COOLDOWN_MAX);
        this.triggerAnim("skill_controller", "attack");
    }
    public void performSuperAttack() {
        setHasSkill(true);
        this.setAiDisabled(true);
        setSkillCooldown(SKILL_COOLDOWN_MAX);
        setSuperAttackSkillCooldown(SUPER_ATTACK_COOLDOWN_MAX);
        this.triggerAnim("skill_controller", "super_attack");
    }
    public void performMiniAttack() {
        setHasSkill(true);
        this.setAiDisabled(true);
        setSkillCooldown(SKILL_COOLDOWN_MAX);
        setMiniAttackSkillCooldown(MINI_ATTACK_COOLDOWN_MAX);
        this.triggerAnim("skill_controller", "mini_attack");
    }
    public void performMaxAttack() {
        setHasSkill(true);
        this.setAiDisabled(true);
        setSkillCooldown(SKILL_COOLDOWN_MAX);
        setMaxAttackSkillCooldown(MAX_ATTACK_COOLDOWN_MAX);
        this.triggerAnim("skill_controller", "max_attack");
    }
    public boolean canSuperAttack() {
        return canSkill() && getSuperAttackSkillCooldown() == 0;
    }
    public boolean canMiniAttack() {
        return canSkill() && getMiniAttackSkillCooldown() == 0;
    }
    public boolean canMaxAttack() {
        return canSkill() && getMaxAttackSkillCooldown() == 0;
    }
    public boolean canSkill() {
        return !this.getWorld().isClient() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }
    public boolean hasSkill() {
        return getDataTracker().get(HAS_SKILL);
    }
    public int getSkillCooldown() {
        return getDataTracker().get(SKILL_COOLDOWN);
    }
    public int getSuperAttackSkillCooldown() {
        return getDataTracker().get(SUPER_ATTACK_SKILL_COOLDOWN);
    }
    public int getMiniAttackSkillCooldown() {
        return getDataTracker().get(MINI_ATTACK_SKILL_COOLDOWN);
    }
    public int getMaxAttackSkillCooldown() {
        return getDataTracker().get(MAX_ATTACK_SKILL_COOLDOWN);
    }
    public void setHasSkill(boolean hasSkill) {
        getDataTracker().set(HAS_SKILL, hasSkill);
    }
    public void setSkillCooldown(int cooldown) {
        getDataTracker().set(SKILL_COOLDOWN, cooldown);
    }
    public void setSuperAttackSkillCooldown(int cooldown) {
        getDataTracker().set(SUPER_ATTACK_SKILL_COOLDOWN, cooldown);
    }
    public void setMiniAttackSkillCooldown(int cooldown) {
        getDataTracker().set(MINI_ATTACK_SKILL_COOLDOWN, cooldown);
    }
    public void setMaxAttackSkillCooldown(int cooldown) {
        getDataTracker().set(MAX_ATTACK_SKILL_COOLDOWN, cooldown);
    }
    public static DefaultAttributeContainer.Builder addAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20000.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(EntityAttributes.ATTACK_DAMAGE, 150.0D)
                .add(EntityAttributes.FOLLOW_RANGE, 24.0D)
                .add(EntityAttributes.ARMOR, 25.0D)
                .add(EntityAttributes.ARMOR_TOUGHNESS, 20.0D);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(2, new WanderNearTargetGoal(this, 0.9, 32.0F));
        this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.6, false));
        this.goalSelector.add(4, new IronGolemWanderAroundGoal(this, 0.6));
        this.goalSelector.add(5, new IronGolemLookGoal(this));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackIronGolemTargetGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MobEntity.class, 5, false, false, (entity, world) -> entity instanceof Monster));
        this.targetSelector.add(4, new UniversalAngerGoal<>(this, false));
    }
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation SUPER_ATTACK_ANIM = RawAnimation.begin().thenPlay("super_attack");
    protected static final RawAnimation MINI_ATTACK_ANIM = RawAnimation.begin().thenPlay("mini_attack");
    protected static final RawAnimation MAX_ATTACK_ANIM = RawAnimation.begin().thenPlay("max_attack");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", this::animationController));
        controllers.add(new AnimationController<>("skill_controller", animTest -> {
            if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED && this.hasSkill()) {
                ClientPlayNetworking.send(new SkillPayload(
                        "stop", this.getId()
                ));
                animTest.renderState().addGeckolibData(HulkbusterEntityRenderer.SYNC_CATCH, false);
            }
            return PlayState.STOP;
        })
                .triggerableAnim("attack", ATTACK_ANIM)
                .triggerableAnim("super_attack", SUPER_ATTACK_ANIM)
                .triggerableAnim("mini_attack", MINI_ATTACK_ANIM)
                .triggerableAnim("max_attack", MAX_ATTACK_ANIM)
                .setSoundKeyframeHandler(s -> {})
                .setCustomInstructionKeyframeHandler(s -> {
                    if ("run;".equals(s.keyframeData().getInstructions())) {
                        s.getRenderState().addGeckolibData(HulkbusterEntityRenderer.SYNC_CATCH, true);
                    }
                    if ("end;".equals(s.keyframeData().getInstructions())) {
                        s.getRenderState().addGeckolibData(HulkbusterEntityRenderer.SYNC_CATCH, false);
                    }
                    if ("runAttack;".equals(s.keyframeData().getInstructions())) {
                        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "attack", this.getId()
                        ));
                    }
                    if ("runSuperAttack;".equals(s.keyframeData().getInstructions())) {
                        this.playSound(SoundEvents.ENTITY_PLAYER_SMALL_FALL, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "super_attack", this.getId()
                        ));
                    }
                    if ("runMiniAttack;".equals(s.keyframeData().getInstructions())) {
                        this.playSound(SoundEvents.ENTITY_PLAYER_SMALL_FALL, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "mini_attack", this.getId()
                        ));
                    }
                    if ("runMaxAttack;".equals(s.keyframeData().getInstructions())) {
                        this.playSound(SoundEvents.ENTITY_PLAYER_SMALL_FALL, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "max_attack", this.getId()
                        ));
                    }
                }));
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
    private PlayState animationController(final AnimationTest<HulkbusterEntity> state) {
        state.renderState().addGeckolibData(HulkbusterEntityRenderer.ENTITY_ID, this.getUuid());
        if (state.isMoving()) {
            return state.setAndContinue(WALK_ANIM);
        }
        return state.setAndContinue(IDEA_ANIM);
    }
    public Vec3d leftMuzzle = Vec3d.ZERO;
    public Vec3d rightMuzzle = Vec3d.ZERO;
}

