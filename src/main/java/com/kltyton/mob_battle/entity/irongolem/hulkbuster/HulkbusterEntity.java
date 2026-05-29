package com.kltyton.mob_battle.entity.irongolem.hulkbuster;

import com.kltyton.mob_battle.bossbar.CustomBossBarStyles;
import com.kltyton.mob_battle.bossbar.CustomBossBarSync;
import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.accessor.BigBossLookControl;
import com.kltyton.mob_battle.entity.accessor.BigBossMoveControl;
import com.kltyton.mob_battle.entity.accessor.BigBossNavigation;
import com.kltyton.mob_battle.entity.irongolem.ModBaseIronGolemEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.CombatEffectUtil;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.GolemRandomStrollInVillageGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveBackToVillageGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.OfferFlowerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.DefendVillageTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class HulkbusterEntity extends IronGolem implements GeoEntity, ModBaseIronGolemEntity, ModSkillEntityType {
    public static final int SKILL_COOLDOWN_MAX = 20;
    public static final int SUPER_ATTACK_COOLDOWN_MAX = 8 * 20;
    public static final int MINI_ATTACK_COOLDOWN_MAX = 18 * 20;
    public static final int MAX_ATTACK_COOLDOWN_MAX = 25 * 20;
    public static final int CLAP_HANDS_COOLDOWN_MAX = 20 * 20;
    public static final int PUNCH_COOLDOWN_MAX = 25 * 20;

    private final ServerBossEvent bossBar = new ServerBossEvent(
            Component.empty(),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS
    );
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(HulkbusterEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN = SynchedEntityData.defineId(HulkbusterEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SUPER_ATTACK_SKILL_COOLDOWN = SynchedEntityData.defineId(HulkbusterEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MINI_ATTACK_SKILL_COOLDOWN = SynchedEntityData.defineId(HulkbusterEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MAX_ATTACK_SKILL_COOLDOWN = SynchedEntityData.defineId(HulkbusterEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> CLAP_HANDS_COOLDOWN = SynchedEntityData.defineId(HulkbusterEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> PUNCH_COOLDOWN = SynchedEntityData.defineId(HulkbusterEntity.class, EntityDataSerializers.INT);
    private final Set<Integer> punchHitEntities = new HashSet<>();
    private boolean punching;
    private int deathAnimationTicks;
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_SKILL, false);
        builder.define(SKILL_COOLDOWN, SKILL_COOLDOWN_MAX);
        builder.define(SUPER_ATTACK_SKILL_COOLDOWN, SUPER_ATTACK_COOLDOWN_MAX);
        builder.define(MINI_ATTACK_SKILL_COOLDOWN, MINI_ATTACK_COOLDOWN_MAX);
        builder.define(MAX_ATTACK_SKILL_COOLDOWN, MAX_ATTACK_COOLDOWN_MAX);
        builder.define(CLAP_HANDS_COOLDOWN, CLAP_HANDS_COOLDOWN_MAX);
        builder.define(PUNCH_COOLDOWN, PUNCH_COOLDOWN_MAX);
    }
    public HulkbusterEntity(EntityType<? extends IronGolem> entityType, Level world) {
        super(entityType, world);
        this.setHasSkill(false);
        this.setNoAi(false);
        this.setSkillCooldown(SKILL_COOLDOWN_MAX);
        this.lookControl = new BigBossLookControl(this);
        this.moveControl = new BigBossMoveControl(this);
        this.navigation = new BigBossNavigation(this, world);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (this.deathAnimationTicks > 0) {
                tickDeathAnimation();
                return;
            }
            if(this.tickCount % 20 == 0) this.heal(1);
            // 冷却递减
            int cd = getSkillCooldown();
            if (cd > 0) setSkillCooldown(cd - 1);
            int superAttackCd = getSuperAttackSkillCooldown();
            if (superAttackCd > 0) setSuperAttackSkillCooldown(superAttackCd - 1);
            int miniAttackCd = getMiniAttackSkillCooldown();
            if (miniAttackCd > 0) setMiniAttackSkillCooldown(miniAttackCd - 1);
            int maxAttackCd = getMaxAttackSkillCooldown();
            if (maxAttackCd > 0) setMaxAttackSkillCooldown(maxAttackCd - 1);
            int clapCd = getClapHandsCooldown();
            if (clapCd > 0) setClapHandsCooldown(clapCd - 1);
            int punchCd = getPunchCooldown();
            if (punchCd > 0) setPunchCooldown(punchCd - 1);
            if (this.punching) {
                tickPunch((ServerLevel) this.level());
            }
            if (this.canMaxAttack()) {
                performMaxAttack();
            }
            if (!hasSkill()) {
                this.setNoAi(false);
            }
        }
    }
    @Override
    public void knockback(double strength, double x, double z) {
    }
    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        updateBossBar();
    }
    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        CustomBossBarSync.add(player, this.bossBar.getId(), CustomBossBarStyles.HULKBUSTER);
        this.bossBar.addPlayer(player);
    }
    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossBar.removePlayer(player);
        CustomBossBarSync.remove(player, this.bossBar.getId());
    }
    @Override
    protected void customServerAiStep(ServerLevel world) {
        super.customServerAiStep(world);
        updateBossBar();
    }
    @Override
    public void readAdditionalSaveData(ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        if (this.hasCustomName()) {
            updateBossBar();
        }
    }
    @Override
    public void addAdditionalSaveData(ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
    }
    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        if (this.bossBar != null) {
            updateBossBar();
        }
    }

    private void updateBossBar() {
        this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());
        this.bossBar.setName(this.getDisplayName().copy().append(" | " + (int) Math.ceil(this.getHealth()) + "/" + (int) this.getMaxHealth()));
    }
    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity entity) {
        AABB attackBox = this.getAttackBoundingBox().inflate(1.05);
        return attackBox.intersects(entity.getBoundingBox());
    }
    public boolean tryAttackBase(ServerLevel world, LivingEntity target) {
        if (!ModSkillEntityType.canSkill(this)) return false;
        return tryAttackBaseDamage(world, target, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
    }
    public boolean tryAttackBaseDamage(ServerLevel world, Entity target, float damage) {
        if (!ModSkillEntityType.canSkill(this)) return false;
        float f = damage;
        ItemStack itemStack = this.getWeaponItem();
        DamageSource damageSource = Optional.ofNullable(itemStack.getItem().getDamageSource(this)).orElse(this.damageSources().mobAttack(this));
        f = EnchantmentHelper.modifyDamage(world, itemStack, target, damageSource, f);
        f += itemStack.getItem().getAttackDamageBonus(target, f, damageSource);
        boolean bl = target.hurtServer(world, damageSource, f);
        if (bl) {
            float g = this.getKnockback(target, damageSource);
            if (g > 0.0F && target instanceof LivingEntity livingEntity) {
                livingEntity.knockback(g * 0.5F, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            if (target instanceof LivingEntity livingEntity) {
                itemStack.hurtEnemy(livingEntity, this);
                CombatEffectUtil.addStackingArmorPiercing(livingEntity, this);
            }

            EnchantmentHelper.doPostAttackEffects(world, target, damageSource);
            this.setLastHurtMob(target);
            this.playAttackSound();
        }

        return bl;
    }
    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!ModSkillEntityType.canSkill(this)) return false;
        if (this.canPunch()) {
            performPunch();
            return true;
        } else if (this.canClapHands()) {
            performClapHands();
            return true;
        } else if (this.canMiniAttack()) {
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
        this.setNoAi(true);
        setSkillCooldown(SKILL_COOLDOWN_MAX);
        this.triggerAnim("skill_controller", "attack");
    }
    public void performSuperAttack() {
        setHasSkill(true);
        this.setNoAi(true);
        setSkillCooldown(SKILL_COOLDOWN_MAX);
        setSuperAttackSkillCooldown(SUPER_ATTACK_COOLDOWN_MAX);
        this.triggerAnim("skill_controller", "super_attack");
    }
    public void performMiniAttack() {
        setHasSkill(true);
        this.setNoAi(true);
        setSkillCooldown(SKILL_COOLDOWN_MAX);
        setMiniAttackSkillCooldown(MINI_ATTACK_COOLDOWN_MAX);
        this.triggerAnim("skill_controller", "mini_attack");
    }
    public void performMaxAttack() {
        setHasSkill(true);
        this.setNoAi(true);
        setSkillCooldown(SKILL_COOLDOWN_MAX);
        setMaxAttackSkillCooldown(MAX_ATTACK_COOLDOWN_MAX);
        this.triggerAnim("skill_controller", "max_attack");
    }
    public void performClapHands() {
        setHasSkill(true);
        this.setNoAi(true);
        setSkillCooldown(SKILL_COOLDOWN_MAX);
        setClapHandsCooldown(CLAP_HANDS_COOLDOWN_MAX);
        this.triggerAnim("skill_controller", "clap_hands");
    }
    public void performPunch() {
        setHasSkill(true);
        this.setNoAi(true);
        setSkillCooldown(SKILL_COOLDOWN_MAX);
        setPunchCooldown(PUNCH_COOLDOWN_MAX);
        this.triggerAnim("skill_controller", "punch");
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
    public boolean canClapHands() {
        return canSkill() && getClapHandsCooldown() == 0;
    }
    public boolean canPunch() {
        return canSkill() && getPunchCooldown() == 0;
    }
    public boolean canSkill() {
        if (!ModSkillEntityType.canSkill(this)) return false;
        return !this.level().isClientSide() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }
    public boolean hasSkill() {
        return getEntityData().get(HAS_SKILL);
    }
    public int getSkillCooldown() {
        return getEntityData().get(SKILL_COOLDOWN);
    }
    public int getSuperAttackSkillCooldown() {
        return getEntityData().get(SUPER_ATTACK_SKILL_COOLDOWN);
    }
    public int getMiniAttackSkillCooldown() {
        return getEntityData().get(MINI_ATTACK_SKILL_COOLDOWN);
    }
    public int getMaxAttackSkillCooldown() {
        return getEntityData().get(MAX_ATTACK_SKILL_COOLDOWN);
    }
    public int getClapHandsCooldown() {
        return getEntityData().get(CLAP_HANDS_COOLDOWN);
    }
    public int getPunchCooldown() {
        return getEntityData().get(PUNCH_COOLDOWN);
    }
    public void setHasSkill(boolean hasSkill) {
        getEntityData().set(HAS_SKILL, hasSkill);
    }
    public void setSkillCooldown(int cooldown) {
        getEntityData().set(SKILL_COOLDOWN, cooldown);
    }
    public void setSuperAttackSkillCooldown(int cooldown) {
        getEntityData().set(SUPER_ATTACK_SKILL_COOLDOWN, cooldown);
    }
    public void setMiniAttackSkillCooldown(int cooldown) {
        getEntityData().set(MINI_ATTACK_SKILL_COOLDOWN, cooldown);
    }
    public void setMaxAttackSkillCooldown(int cooldown) {
        getEntityData().set(MAX_ATTACK_SKILL_COOLDOWN, cooldown);
    }
    public void setClapHandsCooldown(int cooldown) {
        getEntityData().set(CLAP_HANDS_COOLDOWN, cooldown);
    }
    public void setPunchCooldown(int cooldown) {
        getEntityData().set(PUNCH_COOLDOWN, cooldown);
    }
    public static AttributeSupplier.Builder addAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 150.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ARMOR, 25.0D)
                .add(Attributes.STEP_HEIGHT, 3)
                .add(Attributes.ARMOR_TOUGHNESS, 20.0D);
    }
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9, 32.0F));
        this.goalSelector.addGoal(2, new MoveBackToVillageGoal(this, 0.6, false));
        this.goalSelector.addGoal(4, new GolemRandomStrollInVillageGoal(this, 0.6));
        this.goalSelector.addGoal(5, new OfferFlowerGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new DefendVillageTargetGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (entity, world) -> entity instanceof Enemy));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
    }
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation SUPER_ATTACK_ANIM = RawAnimation.begin().thenPlay("super_attack");
    protected static final RawAnimation MINI_ATTACK_ANIM = RawAnimation.begin().thenPlay("mini_attack");
    protected static final RawAnimation MAX_ATTACK_ANIM = RawAnimation.begin().thenPlay("max_attack");
    protected static final RawAnimation CLAP_HANDS_ANIM = RawAnimation.begin().thenPlay("clap_hands");
    protected static final RawAnimation PUNCH_ANIM = RawAnimation.begin().thenPlay("punch");
    protected static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlay("death");
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
                .triggerableAnim("clap_hands", CLAP_HANDS_ANIM)
                .triggerableAnim("punch", PUNCH_ANIM)
                .triggerableAnim("death", DEATH_ANIM)
                .setSoundKeyframeHandler(s -> {})
                .setCustomInstructionKeyframeHandler(s -> {
                    String instruction = s.keyframeData().getInstructions().replaceAll("\\s+", "");
                    if ("run;".equals(s.keyframeData().getInstructions())) {
                        s.getRenderState().addGeckolibData(HulkbusterEntityRenderer.SYNC_CATCH, true);
                    }
                    if ("end;".equals(s.keyframeData().getInstructions())) {
                        s.getRenderState().addGeckolibData(HulkbusterEntityRenderer.SYNC_CATCH, false);
                    }
                    if ("runAttack;".equals(instruction)) {
                        this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "attack", this.getId()
                        ));
                    }
                    if ("runSuperAttack;".equals(instruction)) {
                        this.playSound(SoundEvents.PLAYER_SMALL_FALL, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "super_attack", this.getId()
                        ));
                    }
                    if ("runMiniAttack;".equals(instruction)) {
                        this.playSound(SoundEvents.PLAYER_SMALL_FALL, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "mini_attack", this.getId()
                        ));
                    }
                    if ("runMaxAttack;".equals(instruction)) {
                        this.playSound(SoundEvents.PLAYER_SMALL_FALL, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "max_attack", this.getId()
                        ));
                    }
                    if ("runClapHands;".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload("clap_hands", this.getId()));
                    }
                    if ("runPunch;".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload("punch", this.getId()));
                    }
                    if ("runPunch_1;".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload("punch_1", this.getId()));
                    }
                }));
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
    private PlayState animationController(final AnimationTest<HulkbusterEntity> state) {
        state.renderState().addGeckolibData(HulkbusterEntityRenderer.ENTITY_ID, this.getUUID());
        if (state.isMoving()) {
            return state.setAndContinue(WALK_ANIM);
        }
        return state.setAndContinue(IDEA_ANIM);
    }
    public Vec3 leftMuzzle = Vec3.ZERO;
    public Vec3 rightMuzzle = Vec3.ZERO;

    public void startPunch() {
        this.punching = true;
        this.punchHitEntities.clear();
        this.setNoAi(false);
    }

    public void stopPunch() {
        this.punching = false;
        this.setDeltaMovement(Vec3.ZERO);
        this.hurtMarked = true;
        HulkbusterEntitySkill.runPunchEndSkill(this);
    }

    private void tickPunch(ServerLevel world) {
        Vec3 look = this.getViewVector(1.0F);
        Vec3 horizontal = new Vec3(look.x, 0.0D, look.z);
        if (horizontal.lengthSqr() < 1.0E-4D) {
            horizontal = Vec3.directionFromRotation(0.0F, this.getYRot());
        }
        Vec3 velocity = horizontal.normalize().scale(0.55D);
        this.setDeltaMovement(velocity.x, this.getDeltaMovement().y, velocity.z);
        this.hurtMarked = true;
        AABB box = this.getBoundingBox().inflate(1.0D);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, box,
                living -> EntityUtil.isValidCombatTarget(this, living))) {
            if (this.punchHitEntities.add(target.getId())) {
                this.tryAttackBaseDamage(world, target, 300.0F);
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if (this.deathAnimationTicks > 0) {
            return false;
        }
        if (this.getHealth() - amount <= 0.0F) {
            startDeathAnimation();
            return true;
        }
        return super.hurtServer(world, source, amount);
    }

    private void startDeathAnimation() {
        this.setHealth(1.0F);
        this.setNoAi(true);
        this.setHasSkill(true);
        this.deathAnimationTicks = 90;
        this.triggerAnim("skill_controller", "death");
    }

    private void tickDeathAnimation() {
        this.setHealth(1.0F);
        this.deathAnimationTicks--;
        if (this.deathAnimationTicks <= 0) {
            this.remove(Entity.RemovalReason.KILLED);
        }
    }
}

