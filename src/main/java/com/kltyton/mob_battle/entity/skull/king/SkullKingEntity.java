package com.kltyton.mob_battle.entity.skull.king;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.accessor.BigBossLookControl;
import com.kltyton.mob_battle.entity.accessor.BigBossMoveControl;
import com.kltyton.mob_battle.entity.accessor.BigBossNavigation;
import com.kltyton.mob_battle.entity.skull.IModSkullEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
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

public class SkullKingEntity extends WitherSkeleton implements GeoEntity, IModSkullEntity {
    private final ServerBossEvent bossBar = new ServerBossEvent(
            this.getDisplayName(),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS
    );
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(SkullKingEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN = SynchedEntityData.defineId(SkullKingEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SUPER_ATTACK_SKILL_COOLDOWN = SynchedEntityData.defineId(SkullKingEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SUMMON_SKULL_COOLDOWN = SynchedEntityData.defineId(SkullKingEntity.class, EntityDataSerializers.INT);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_SKILL, false);
        builder.define(SKILL_COOLDOWN, 20);
        builder.define(SUPER_ATTACK_SKILL_COOLDOWN, 300);
        builder.define(SUMMON_SKULL_COOLDOWN, 200);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            killSlave();
            if (!hasSkill()) {
                this.setNoAi(false);
                if (canSummonSkull()) performSummonSkull();
                // 鍐峰嵈閫掑噺
                int cd = getSkillCooldown();
                if (cd > 0) setSkillCooldown(cd - 1);
                int superAttackCd = getSuperAttackSkillCooldown();
                if (superAttackCd > 0) setSuperAttackSkillCooldown(superAttackCd - 1);
                int summonSkullCooldown = getSummonSkullCooldown();
                if (summonSkullCooldown > 0) setSummonSkullCooldown(summonSkullCooldown - 1);
            }
        }
    }
    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
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
    @Override
    protected void customServerAiStep(ServerLevel world) {
        super.customServerAiStep(world);
        this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());
        this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
    }
    @Override
    public void readAdditionalSaveData(ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        if (this.hasCustomName()) {
            this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
        }
    }
    @Override
    public void addAdditionalSaveData(ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
    }
    @Nullable
    public SpawnGroupData initializeBase(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        RandomSource random = world.getRandom();
        AttributeInstance entityAttributeInstance = Objects.requireNonNull(this.getAttribute(Attributes.FOLLOW_RANGE));
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
        Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(90.0D);
        this.setNoAi(false);
        this.reassessWeaponGoal();
        return entityData2;
    }
    public SkullKingEntity(EntityType<? extends SkullKingEntity> entityType, Level world) {
        super(entityType, world);
        this.setHasSkill(false);
        this.setNoAi(false);
        this.setSkillCooldown(20);
        this.lookControl = new BigBossLookControl(this);
        this.moveControl = new BigBossMoveControl(this);
        this.navigation = new BigBossNavigation(this, world);
    }
    public boolean tryAttackBase(ServerLevel world, Entity target) {
        if (!ModSkillEntityType.canSkill(this)) return false;
        float f = 90.0F;
        ItemStack itemStack = this.getWeaponItem();
        DamageSource damageSource = Optional.ofNullable(itemStack.getItem().getDamageSource(this)).orElse(this.damageSources().mobAttack(this));
        f = EnchantmentHelper.modifyDamage(world, itemStack, target, damageSource, f);
        f += itemStack.getItem().getAttackDamageBonus(target, f, damageSource);
        if (this.isAlliedTo(target)) return false;
        boolean bl = target.hurtServer(world, damageSource, f);
        if (bl) {
            float g = this.getKnockback(target, damageSource);
            target.hurtServer(world, this.damageSources().magic(), 10);
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
    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!ModSkillEntityType.canSkill(this)) return false;
        if (this.canSuperAttack()) {
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
    public void performSuperAttack() {
        setHasSkill(true);
        this.setNoAi(true);
        setSkillCooldown(20);
        setSuperAttackSkillCooldown(300);
        this.triggerAnim("skill_controller", "super_attack");
    }
    public void performSummonSkull() {
        setHasSkill(true);
        this.setNoAi(true);
        setSkillCooldown(20);
        setSummonSkullCooldown(300);
        this.triggerAnim("skill_controller", "summon_skull");
    }
    public boolean canSuperAttack() {
        return canSkill() && getSuperAttackSkillCooldown() == 0;
    }
    public boolean canSummonSkull() {
        int count = EntityUtil.getNearbyEntityCount(this, LivingEntity.class, IModSkullEntity.class, 100);
        return count < 60 && canSkill() && getSummonSkullCooldown() == 0;
    }
    public boolean canSkill() {
        if (!ModSkillEntityType.canSkill(this)) return false;
        return !this.level().isClientSide() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation SUPER_ATTACK_ANIM = RawAnimation.begin().thenPlay("super_attack");
    protected static final RawAnimation SUMMON_SKULL_ANIM = RawAnimation.begin().thenPlay("summon_skull");
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
                .triggerableAnim("summon_skull", SUMMON_SKULL_ANIM)
                .setSoundKeyframeHandler(s -> {})
                .setCustomInstructionKeyframeHandler(s -> {
                    Player player = ClientUtil.getClientPlayer();
                    if ("runAttack".equals(s.keyframeData().getInstructions())) {
                        player.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "attack", this.getId()
                        ));
                    }
                    if ("runSuperAttack".equals(s.keyframeData().getInstructions())) {
                        player.playSound(SoundEvents.PLAYER_SMALL_FALL, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "super_attack", this.getId()
                        ));
                    }
                    if ("runSummonSkull".equals(s.keyframeData().getInstructions())) {
                        ClientPlayNetworking.send(new SkillPayload(
                                "summon_skull", this.getId()
                        ));
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
                .add(Attributes.MAX_HEALTH, 3500.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 90.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.STEP_HEIGHT, 3.0);
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
    @Override
    public void knockback(double strength, double x, double z) {
        if (!this.isDeadOrDying() && !this.isNoAi()) super.knockback(strength, x, z);
    }
    public int getSummonSkullCooldown() {
        return getEntityData().get(SUMMON_SKULL_COOLDOWN);
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
    public void setSummonSkullCooldown(int cooldown) {
        getEntityData().set(SUMMON_SKULL_COOLDOWN, cooldown);
    }
    @Override
    public boolean canHoldItem(ItemStack stack) {
        return false;
    }
    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance localDifficulty) {
    }

    @Override
    public void setOwner(LivingEntity entity) {
    }

    @Override
    public @Nullable EntityReference<LivingEntity> getOwnerReference() {
        return null;
    }

    @Override
    public LivingEntity getOwner() {
        return null;
    }

    @Override
    public boolean isOwner(LivingEntity entity) {
        return false;
    }

    @Override
    public void setOwner(@Nullable EntityReference<LivingEntity> owner) {

    }
    public boolean canAttack(LivingEntity target) {
        if (target instanceof IModSkullEntity iModSkullEntity) return !iModSkullEntity.isOwner(this) && super.canAttack(target);
        return super.canAttack(target);
    }
}
