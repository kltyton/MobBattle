package com.kltyton.mob_battle.entity.witherskeletonking;

import com.kltyton.mob_battle.bossbar.CustomBossBarStyles;
import com.kltyton.mob_battle.bossbar.CustomBossBarSync;
import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.accessor.BigBossLookControl;
import com.kltyton.mob_battle.entity.accessor.BigBossMoveControl;
import com.kltyton.mob_battle.entity.accessor.BigBossNavigation;
import com.kltyton.mob_battle.entity.witherskeletonking.skill.WitherSkeletonKingEntitySkill;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.CombatEffectUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
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

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Objects;
import java.util.Optional;

public class WitherSkeletonKingEntity extends WitherSkeleton implements GeoEntity, ModSkillEntityType {

    private final ServerBossEvent bossBar = new ServerBossEvent(
            Component.empty(),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS
    );
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(WitherSkeletonKingEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN = SynchedEntityData.defineId(WitherSkeletonKingEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SUPER_ATTACK_SKILL_COOLDOWN = SynchedEntityData.defineId(WitherSkeletonKingEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SHOT_WITHER_SKULL_COOLDOWN = SynchedEntityData.defineId(WitherSkeletonKingEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SUPER_SHOT_WITHER_SKULL_COOLDOWN = SynchedEntityData.defineId(WitherSkeletonKingEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SHOT_ALL_WITHER_SKULL_COOLDOWN = SynchedEntityData.defineId(WitherSkeletonKingEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> THORN_COOLDOWN = SynchedEntityData.defineId(WitherSkeletonKingEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ENHANCE_WITHER_CALL_COOLDOWN = SynchedEntityData.defineId(WitherSkeletonKingEntity.class, EntityDataSerializers.INT);
    private int deathAnimationTicks;
    private boolean summonDogsAfterSuperShot;
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_SKILL, false);
        builder.define(SKILL_COOLDOWN, 20);
        builder.define(SUPER_ATTACK_SKILL_COOLDOWN, 15 * 20);
        builder.define(SHOT_WITHER_SKULL_COOLDOWN, 16 * 20);
        builder.define(SUPER_SHOT_WITHER_SKULL_COOLDOWN, 42 * 20);
        builder.define(SHOT_ALL_WITHER_SKULL_COOLDOWN, 50 * 20);
        builder.define(THORN_COOLDOWN, 25 * 20);
        builder.define(ENHANCE_WITHER_CALL_COOLDOWN, 80 * 20);
    }
    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        Holder<MobEffect> effectType = effect.getEffect();
        if (effectType.equals(MobEffects.SLOWNESS)) {
            return false;
        }
        return super.canBeAffected(effect);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (this.tickCount % 20 == 0) this.heal(1.0F);
            if (this.deathAnimationTicks > 0) {
                tickDeathAnimation();
                return;
            }
            if (!hasSkill()) {
                this.setNoAi(false);
                if (canEnhanceWitherCall()) performEnhanceWitherCall();
                if (canThorn()) performThorn();
                if (canShotWitherSkull()) performShotWitherSkull();
                if (canSuperShotWitherSkull()) performSuperShotWitherSkull();
                if (canShotAllWitherSkull()) performShotAllWitherSkull();

                // 鍐峰嵈閫掑噺
                int cd = getSkillCooldown();
                if (cd > 0) setSkillCooldown(cd - 1);
                int superAttackCd = getSuperAttackSkillCooldown();
                if (superAttackCd > 0) setSuperAttackSkillCooldown(superAttackCd - 1);
                int shotWitherSkullCd = getShotWitherSkullCooldown();
                if (shotWitherSkullCd > 0) setShotWitherSkullCooldown(shotWitherSkullCd - 1);
                int superShotWitherSkullCd = getSuperShotWitherSkullCooldown();
                if (superShotWitherSkullCd > 0) setSuperShotWitherSkullCooldown(superShotWitherSkullCd - 1);
                int shotAllWitherSkullCd = getShotAllWitherSkullCooldown();
                if (shotAllWitherSkullCd > 0) setShotAllWitherSkullCooldown(shotAllWitherSkullCd - 1);
                int thornCd = getThornCooldown();
                if (thornCd > 0) setThornCooldown(thornCd - 1);
                int enhanceCd = getEnhanceWitherCallCooldown();
                if (enhanceCd > 0) setEnhanceWitherCallCooldown(enhanceCd - 1);
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
        CustomBossBarSync.add(player, this.bossBar.getId(), CustomBossBarStyles.WITHER_SKELETON_KING);
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
    public boolean isWithinMeleeAttackRange(LivingEntity entity) {
        AABB attackBox = this.getAttackBoundingBox().inflate(1.25);
        return attackBox.intersects(entity.getBoundingBox());
    }
    @Nullable
    public SpawnGroupData initializeBase(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        RandomSource random = world.getRandom();
        AttributeInstance entityAttributeInstance = (AttributeInstance)Objects.requireNonNull(this.getAttribute(Attributes.FOLLOW_RANGE));
        if (!entityAttributeInstance.hasModifier(RANDOM_SPAWN_BONUS_ID)) {
            entityAttributeInstance.addPermanentModifier(
                    new AttributeModifier(RANDOM_SPAWN_BONUS_ID, random.triangle(0.0, 0.11485000000000001), AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            );
        }

        this.setLeftHanded(random.nextFloat() < 0.05F);
        return entityData;
    }
    @Nullable
    public SpawnGroupData initializeBase2(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        entityData = this.initializeBase(world, difficulty, spawnReason, entityData);
        RandomSource random = world.getRandom();
        this.populateDefaultEquipmentSlots(random, difficulty);
        this.populateDefaultEquipmentEnchantments(world, random, difficulty);
        this.reassessWeaponGoal();
        this.setCanPickUpLoot(random.nextFloat() < 0.55F * difficulty.getSpecialMultiplier());
        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localDate = LocalDate.now();
            int i = localDate.get(ChronoField.DAY_OF_MONTH);
            int j = localDate.get(ChronoField.MONTH_OF_YEAR);
            if (j == 10 && i == 31 && random.nextFloat() < 0.25F) {
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.setDropChance(EquipmentSlot.HEAD, 0.0F);
            }
        }

        return entityData;
    }
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        SpawnGroupData entityData2 = this.initializeBase2(world, difficulty, spawnReason, entityData);
        Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(100.0D);
        this.setNoAi(false);
        this.reassessWeaponGoal();
        return entityData2;
    }
    public WitherSkeletonKingEntity(EntityType<? extends WitherSkeleton> entityType, Level world) {
        super(entityType, world);
        this.setHasSkill(false);
        this.setNoAi(false);
        this.setSkillCooldown(20);
        this.lookControl = new BigBossLookControl(this);
        this.moveControl = new BigBossMoveControl(this);
        this.navigation = new BigBossNavigation(this, world);
        addEffect(new MobEffectInstance(
                MobEffects.FIRE_RESISTANCE,
                -1,
                0,
                true, true, true));
    }
    @Override
    public boolean canHoldItem(ItemStack stack) {
        return false;
    }
    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance localDifficulty) {
    }
    private boolean tryAttackBase(ServerLevel world, Entity target) {
        if (!ModSkillEntityType.canSkill(this)) return false;
        float f = 100.0F;
        ItemStack itemStack = this.getWeaponItem();
        DamageSource damageSource = Optional.ofNullable(itemStack.getItem().getDamageSource(this)).orElse(this.damageSources().mobAttack(this));
        f = EnchantmentHelper.modifyDamage(world, itemStack, target, damageSource, f);
        f += itemStack.getItem().getAttackDamageBonus(target, f, damageSource);
        if (this.isAlliedTo(target)) return false;
        boolean bl = target.hurtServer(world, damageSource, f);
        if (bl) {
            if (target instanceof LivingEntity livingEntity) {
                CombatEffectUtil.addStackingArmorPiercing(livingEntity, this);
            }
            float g = this.getKnockback(target, damageSource);
            if (g > 0.0F && target instanceof LivingEntity livingEntity) {
                livingEntity.knockback(g * 0.5F, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            if (target instanceof LivingEntity livingEntity) {
                itemStack.hurtEnemy(livingEntity, this);
            }

            EnchantmentHelper.doPostAttackEffects(world, target, damageSource);
            this.setLastHurtMob(target);
            this.playAttackSound();
        }
        return bl;
    }
    public boolean tryAttackBase2(ServerLevel world, Entity target) {
        if (!ModSkillEntityType.canSkill(this)) return false;
        if (!this.tryAttackBase(world, target)) {
            return false;
        } else {
            if (target instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 5 * 20, 4), this);
                if (livingEntity.isDeadOrDying()) this.heal(5.0F);
                if (!(target instanceof Player)) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 1), this);
                }
            }
            return true;
        }
    }
    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!ModSkillEntityType.canSkill(this)) return false;
        if (this.canEnhanceWitherCall()) {
            performEnhanceWitherCall();
            return true;
        } else if (this.canThorn()) {
            performThorn();
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
        setSkillCooldown(20);
        this.triggerAnim("skill_controller", "attack");
    }
    public boolean isHealthy(double health) {
        return this.getHealth() <= this.getMaxHealth() * health;
    }
    public void performSuperAttack() {
        setHasSkill(true);
        setSkillCooldown(20);
        this.setNoAi(true);
        int cd = 300;
        if (this.isHealthy(0.35)) cd = 220;
        else if (this.isHealthy(0.75)) cd = 260;
        setSuperAttackSkillCooldown(cd);
        this.triggerAnim("skill_controller", "super_attack");
    }
    public void performShotWitherSkull() {
        setHasSkill(true);
        setSkillCooldown(20);
        this.setNoAi(true);
        setShotWitherSkullCooldown(this.isHealthy(0.35) ? 160 : 320);
        this.triggerAnim("skill_controller", "shot_wither_skull");
    }
    public void performSuperShotWitherSkull() {
        setHasSkill(true);
        setSkillCooldown(20);
        this.setNoAi(true);
        this.summonDogsAfterSuperShot = true;
        setSuperShotWitherSkullCooldown(42 * 20);
        this.triggerAnim("skill_controller", "super_shot_wither_skull");
    }
    public void performShotAllWitherSkull() {
        setHasSkill(true);
        setSkillCooldown(20);
        this.setNoAi(true);
        setShotAllWitherSkullCooldown(isHealthy(0.35) ? 600 : 1000);
        this.triggerAnim("skill_controller", "shot_all_wither_skull");
    }
    public void performThorn() {
        setHasSkill(true);
        setSkillCooldown(20);
        this.setNoAi(true);
        setThornCooldown(25 * 20);
        this.triggerAnim("skill_controller", "thorn");
    }
    public void performEnhanceWitherCall() {
        setHasSkill(true);
        setSkillCooldown(20);
        this.setNoAi(true);
        setEnhanceWitherCallCooldown(80 * 20);
        this.triggerAnim("skill_controller", "enhance_wither_call");
    }
    public boolean canSuperAttack() {
        return canSkill() && getSuperAttackSkillCooldown() == 0;
    }
    public boolean canShotWitherSkull() {
        return this.getHealth() <= this.getMaxHealth() * 0.75 && canSkill() && getShotWitherSkullCooldown() == 0;
    }
    public boolean canSuperShotWitherSkull() {
        return this.getHealth() <= this.getMaxHealth() * 0.75 && canSkill() && getSuperShotWitherSkullCooldown() == 0;
    }
    public boolean canShotAllWitherSkull() {
        return this.getHealth() <= this.getMaxHealth() * 0.35 && canSkill() && getShotAllWitherSkullCooldown() == 0;
    }
    public boolean canThorn() {
        LivingEntity target = this.getTarget();
        if (target == null) return false;
        double distance = this.distanceTo(target);
        return canSkill() && getThornCooldown() == 0 && distance > 4.0D && distance <= 20.0D;
    }
    public boolean canEnhanceWitherCall() {
        return canSkill() && getEnhanceWitherCallCooldown() == 0;
    }
    public boolean canSkill() {
        if (!ModSkillEntityType.canSkill(this)) return false;
        return !this.level().isClientSide() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("new3");
    protected static final RawAnimation SUPER_ATTACK_ANIM = RawAnimation.begin().thenPlay("new4");
    protected static final RawAnimation SHOT_WITHER_SKULL = RawAnimation.begin().thenPlay("new6");
    protected static final RawAnimation SUPER_SHOT_WITHER_SKULL = RawAnimation.begin().thenPlay("new5");
    protected static final RawAnimation SHOT_ALL_WITHER_SKULL = RawAnimation.begin().thenPlay("new2");
    protected static final RawAnimation THORN = RawAnimation.begin().thenPlay("new7");
    protected static final RawAnimation ENHANCE_WITHER_CALL = RawAnimation.begin().thenPlay("new8");
    protected static final RawAnimation DEATH = RawAnimation.begin().thenPlay("death");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 5 ,this::animationController));
        controllers.add(new AnimationController<>("skill_controller",animTest -> {
            if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED && this.hasSkill()) {
                ClientPlayNetworking.send(new SkillPayload(
                        "stop", this.getId()
                ));
            }
            return PlayState.STOP;
        })
                .triggerableAnim("attack", ATTACK_ANIM)
                .triggerableAnim("super_attack", SUPER_ATTACK_ANIM)
                .triggerableAnim("shot_wither_skull", SHOT_WITHER_SKULL)
                .triggerableAnim("super_shot_wither_skull", SUPER_SHOT_WITHER_SKULL)
                .triggerableAnim("shot_all_wither_skull", SHOT_ALL_WITHER_SKULL)
                .triggerableAnim("thorn", THORN)
                .triggerableAnim("enhance_wither_call", ENHANCE_WITHER_CALL)
                .triggerableAnim("death", DEATH)
                .setSoundKeyframeHandler(s -> {})
                .setCustomInstructionKeyframeHandler(s -> {
                    Player player = ClientUtil.getClientPlayer();
                    String instruction = s.keyframeData().getInstructions().replaceAll("\\s+", "");
                    if ("canHalo".equals(instruction)) {
                        s.animationState().renderState().addGeckolibData(WitherSkeletonKingRenderer.CAN_HALO, true);
                    }
                    if ("cancelHalo".equals(instruction)) {
                        s.animationState().renderState().addGeckolibData(WitherSkeletonKingRenderer.CAN_HALO, false);
                    }
                    if ("runAttack".equals(instruction)) {
                        player.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "attack", this.getId()
                        ));
                    }
                    if ("runSuperAttack".equals(instruction)) {
                        player.playSound(SoundEvents.PLAYER_SMALL_FALL, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "super_attack", this.getId()
                        ));
                    }
                    if ("runWitherSkull".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload(
                                "shot_wither_skull", this.getId()
                        ));
                    }
                    if ("runSuperWitherSkull".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload(
                                "super_shot_wither_skull", this.getId()
                        ));
                    }
                    if ("runShotAllWitherSkull".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload(
                                "shot_all_wither_skull", this.getId()
                        ));
                    }
                    if ("runThorn;".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload("thorn", this.getId()));
                    }
                    if ("runEnhanceWitherCall;".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload("enhance_wither_call", this.getId()));
                    }
                }));
    }

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
    private PlayState animationController(final AnimationTest<WitherSkeletonKingEntity> state) {
        if (state.isMoving()) {
            return state.setAndContinue(WALK_ANIM);
        }
        return state.setAndContinue(IDEA_ANIM);
    }
    public static AttributeSupplier.Builder addAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D)
                .add(Attributes.ATTACK_DAMAGE, 100.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.STEP_HEIGHT, 3)
                .add(Attributes.ARMOR, 25.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 20.0D);
    }
    boolean isPlaySound = false;
    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        if (!this.level().isClientSide() && this.isHealthy(0.35)) {
            this.addEffect(new MobEffectInstance(
                    MobEffects.SPEED,
                    -1,
                    0,
                    true, true, true));
            this.addEffect(new MobEffectInstance(
                    MobEffects.RESISTANCE,
                    -1,
                    0,
                    true, true, true));
        }
        if (!this.level().isClientSide() && this.getHealth() == this.getMaxHealth() * 0.35 && !isPlaySound) {
            this.level().playSound(this, this.getX(), this.getY(), this.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.HOSTILE, 3.0F, 1.0F);
            isPlaySound = true;
        }
        if (this.bossBar != null) {
            updateBossBar();
        }
    }

    private void updateBossBar() {
        this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());
        this.bossBar.setName(this.getDisplayName().copy().append(" | " + (int) Math.ceil(this.getHealth()) + "/" + (int) this.getMaxHealth()));
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
    public int getShotWitherSkullCooldown() {
        return getEntityData().get(SHOT_WITHER_SKULL_COOLDOWN);
    }
    public int getSuperShotWitherSkullCooldown() {
        return getEntityData().get(SUPER_SHOT_WITHER_SKULL_COOLDOWN);
    }
    public int getShotAllWitherSkullCooldown() {
        return getEntityData().get(SHOT_ALL_WITHER_SKULL_COOLDOWN);
    }
    public int getThornCooldown() {
        return getEntityData().get(THORN_COOLDOWN);
    }
    public int getEnhanceWitherCallCooldown() {
        return getEntityData().get(ENHANCE_WITHER_CALL_COOLDOWN);
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
    public void setShotWitherSkullCooldown(int cooldown) {
        getEntityData().set(SHOT_WITHER_SKULL_COOLDOWN, cooldown);
    }
    public void setSuperShotWitherSkullCooldown(int cooldown) {
        getEntityData().set(SUPER_SHOT_WITHER_SKULL_COOLDOWN, cooldown);
    }
    public void setShotAllWitherSkullCooldown(int cooldown) {
        getEntityData().set(SHOT_ALL_WITHER_SKULL_COOLDOWN, cooldown);
    }
    public void setThornCooldown(int cooldown) {
        getEntityData().set(THORN_COOLDOWN, cooldown);
    }
    public void setEnhanceWitherCallCooldown(int cooldown) {
        getEntityData().set(ENHANCE_WITHER_CALL_COOLDOWN, cooldown);
    }

    public void finishSkill() {
        if (!this.level().isClientSide() && this.summonDogsAfterSuperShot) {
            WitherSkeletonKingEntitySkill.spawnWitherSkeletonDogs(this);
        }
        this.summonDogsAfterSuperShot = false;
        this.setHasSkill(false);
        this.setNoAi(false);
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
        this.summonDogsAfterSuperShot = false;
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
