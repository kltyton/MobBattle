package com.kltyton.mob_battle.entity.vindicatorgeneral;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.accessor.BigBossLookControl;
import com.kltyton.mob_battle.entity.accessor.BigBossMoveControl;
import com.kltyton.mob_battle.entity.accessor.BigBossNavigation;
import com.kltyton.mob_battle.bossbar.CustomBossBarStyles;
import com.kltyton.mob_battle.bossbar.CustomBossBarSync;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.CombatEffectUtil;
import com.kltyton.mob_battle.utils.EnchantmentUtil;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
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
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

public class VindicatorGeneralEntity extends Vindicator implements GeoEntity, ModSkillEntityType {
    private final ServerBossEvent bossBar = new ServerBossEvent(
            Component.empty(),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS
    );
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(VindicatorGeneralEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN = SynchedEntityData.defineId(VindicatorGeneralEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SUPER_ATTACK_SKILL_COOLDOWN = SynchedEntityData.defineId(VindicatorGeneralEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MINI_ATTACK_SKILL_COOLDOWN = SynchedEntityData.defineId(VindicatorGeneralEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MAX_ATTACK_SKILL_COOLDOWN = SynchedEntityData.defineId(VindicatorGeneralEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> COLLISION_KILL_COOLDOWN = SynchedEntityData.defineId(VindicatorGeneralEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SPIN_CHOP_COOLDOWN = SynchedEntityData.defineId(VindicatorGeneralEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> THROW_AXE_COOLDOWN = SynchedEntityData.defineId(VindicatorGeneralEntity.class, EntityDataSerializers.INT);
    private static final int AXE_RECOVERY_TIMEOUT_MAX = 120;
    private boolean waitingForAxeRecovery;
    private int axeRecoveryTimeout;
    private int deathAnimationTicks;
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_SKILL, false);
        builder.define(SKILL_COOLDOWN, 20);
        builder.define(SUPER_ATTACK_SKILL_COOLDOWN, 15 * 20);
        builder.define(MINI_ATTACK_SKILL_COOLDOWN, 10 * 20);
        builder.define(MAX_ATTACK_SKILL_COOLDOWN, 30 * 20);
        builder.define(COLLISION_KILL_COOLDOWN, 25 * 20);
        builder.define(SPIN_CHOP_COOLDOWN, 20 * 20);
        builder.define(THROW_AXE_COOLDOWN, 45 * 20);
    }
    public VindicatorGeneralEntity(EntityType<? extends Vindicator> entityType, Level world) {
        super(entityType, world);
        this.setHasSkill(false);
        this.setNoAi(false);
        this.setSkillCooldown(20);
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
            // 冷却递减
            int cd = getSkillCooldown();
            if (cd > 0) setSkillCooldown(cd - 1);
            int superAttackCd = getSuperAttackSkillCooldown();
            if (superAttackCd > 0) setSuperAttackSkillCooldown(superAttackCd - 1);
            int miniAttackCd = getMiniAttackSkillCooldown();
            if (miniAttackCd > 0) setMiniAttackSkillCooldown(miniAttackCd - 1);
            int maxAttackCd = getMaxAttackSkillCooldown();
            if (maxAttackCd > 0) setMaxAttackSkillCooldown(maxAttackCd - 1);
            int collisionCd = getCollisionKillCooldown();
            if (collisionCd > 0) setCollisionKillCooldown(collisionCd - 1);
            int spinCd = getSpinChopCooldown();
            if (spinCd > 0) setSpinChopCooldown(spinCd - 1);
            int throwAxeCd = getThrowAxeCooldown();
            if (throwAxeCd > 0) setThrowAxeCooldown(throwAxeCd - 1);
            if (this.waitingForAxeRecovery && this.axeRecoveryTimeout > 0 && --this.axeRecoveryTimeout <= 0) {
                startAxeRecovery();
            }
            if (!hasSkill()) {
                tryUseTargetedSkill();
            }
            if (!hasSkill()) {
                this.setNoAi(false);
            }
            if (this.tickCount % 20 == 0) this.heal(1);
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
        CustomBossBarSync.add(player, this.bossBar.getId(), CustomBossBarStyles.VINDICATOR_GENERAL);
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
        AABB attackBox = this.getAttackBoundingBox().inflate(1.25);
        return attackBox.intersects(entity.getBoundingBox());
    }
    public boolean tryAttackBase(ServerLevel world, LivingEntity target) {
        return tryAttackBaseDamage(world, target, (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
    }
    public boolean tryAttackBaseDamage(ServerLevel world, Entity target, float damage) {
        if (!ModSkillEntityType.canSkill(this)) return false;
        float f = damage;
        ItemStack itemStack = this.getWeaponItem();

        EnchantmentUtil.addEnchantment(world, itemStack, Enchantments.BREACH, 1);

        DamageSource damageSource = Optional.ofNullable(itemStack.getItem().getDamageSource(this)).orElse(this.damageSources().mobAttack(this));
        f = EnchantmentHelper.modifyDamage(world, itemStack, target, damageSource, f);
        f += itemStack.getItem().getAttackDamageBonus(target, f, damageSource);
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
    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (this.canSpinChop()) {
            performSpinChop();
            return true;
        } else if (this.canMaxAttack()) {
            performMaxAttack();
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
    private void tryUseTargetedSkill() {
        if (this.canThrowAxe()) {
            performThrowAxe();
        } else if (this.canCollisionKill()) {
            performCollisionKill();
        }
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
        setSuperAttackSkillCooldown(15 * 20);
        this.triggerAnim("skill_controller", "super_attack");
    }
    public void performMiniAttack() {
        setHasSkill(true);
        this.setNoAi(true);
        setSkillCooldown(20);
        setMiniAttackSkillCooldown(10 * 20);
        this.triggerAnim("skill_controller", "mini_attack");
    }
    public void performMaxAttack() {
        setHasSkill(true);
        this.setNoAi(true);
        setSkillCooldown(20);
        setMaxAttackSkillCooldown(30 * 20);
        this.triggerAnim("skill_controller", "max_attack");
    }
    public void performCollisionKill() {
        setHasSkill(true);
        this.setNoAi(true);
        setSkillCooldown(20);
        setCollisionKillCooldown(25 * 20);
        this.triggerAnim("skill_controller", "collision_kill");
    }
    public void performSpinChop() {
        setHasSkill(true);
        this.setNoAi(true);
        setSkillCooldown(20);
        setSpinChopCooldown(20 * 20);
        this.triggerAnim("skill_controller", "spin_chop");
    }
    public void performThrowAxe() {
        setHasSkill(true);
        this.setNoAi(true);
        setSkillCooldown(20);
        setThrowAxeCooldown(45 * 20);
        this.waitingForAxeRecovery = true;
        this.axeRecoveryTimeout = AXE_RECOVERY_TIMEOUT_MAX;
        this.triggerAnim("skill_controller", "throw_axe");
    }
    public void startAxeRecovery() {
        this.waitingForAxeRecovery = false;
        this.axeRecoveryTimeout = 0;
        if (this.deathAnimationTicks > 0) {
            return;
        }
        this.triggerAnim("skill_controller", "recovery_axe");
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
    public boolean canCollisionKill() {
        LivingEntity target = this.getTarget();
        if (target == null) return false;
        double distance = this.distanceTo(target);
        return canSkill()
                && EntityUtil.isValidCombatTarget(this, target)
                && getCollisionKillCooldown() == 0
                && distance > 4.0D
                && distance <= 20.0D;
    }
    public boolean canSpinChop() {
        return canSkill() && getSpinChopCooldown() == 0;
    }
    public boolean canThrowAxe() {
        LivingEntity target = this.getTarget();
        return canSkill()
                && getThrowAxeCooldown() == 0
                && target != null
                && EntityUtil.isValidCombatTarget(this, target);
    }
    public boolean canSkill() {
        if (!ModSkillEntityType.canSkill(this)) return false;
        return !this.level().isClientSide() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }
    public boolean hasSkill() {
        return getEntityData().get(HAS_SKILL);
    }
    public boolean isWaitingForAxeRecovery() {
        return this.waitingForAxeRecovery;
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
    public int getCollisionKillCooldown() {
        return getEntityData().get(COLLISION_KILL_COOLDOWN);
    }
    public int getSpinChopCooldown() {
        return getEntityData().get(SPIN_CHOP_COOLDOWN);
    }
    public int getThrowAxeCooldown() {
        return getEntityData().get(THROW_AXE_COOLDOWN);
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
    public void setCollisionKillCooldown(int cooldown) {
        getEntityData().set(COLLISION_KILL_COOLDOWN, cooldown);
    }
    public void setSpinChopCooldown(int cooldown) {
        getEntityData().set(SPIN_CHOP_COOLDOWN, cooldown);
    }
    public void setThrowAxeCooldown(int cooldown) {
        getEntityData().set(THROW_AXE_COOLDOWN, cooldown);
    }
    public static AttributeSupplier.Builder addAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 80.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ARMOR, 30.0D)
                .add(Attributes.STEP_HEIGHT, 3.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 20.0D);
    }
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenPlay("walk2idle").thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenPlay("idlk2walk").thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation SUPER_ATTACK_ANIM = RawAnimation.begin().thenPlay("super_attack");
    protected static final RawAnimation MINI_ATTACK_ANIM = RawAnimation.begin().thenPlay("mini_attack");
    protected static final RawAnimation MAX_ATTACK_ANIM = RawAnimation.begin().thenPlay("max_attack");
    protected static final RawAnimation COLLISION_KILL_ANIM = RawAnimation.begin().thenPlay("collision_kill");
    protected static final RawAnimation SPIN_CHOP_ANIM = RawAnimation.begin().thenPlay("spin_chop");
    protected static final RawAnimation THROW_AXE_ANIM = RawAnimation.begin().thenPlay("throw_axe");
    protected static final RawAnimation RECOVERY_AXE_ANIM = RawAnimation.begin().thenPlay("recovery_axe");
    protected static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlay("death");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", this::animationController));
        controllers.add(new AnimationController<>("skill_controller", animTest -> {
            if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED && this.hasSkill()) {
                if (!this.isWaitingForAxeRecovery()) {
                    ClientPlayNetworking.send(new SkillPayload(
                            "stop", this.getId()
                    ));
                }
            }
            return PlayState.STOP;
        })
                .triggerableAnim("attack", ATTACK_ANIM)
                .triggerableAnim("super_attack", SUPER_ATTACK_ANIM)
                .triggerableAnim("mini_attack", MINI_ATTACK_ANIM)
                .triggerableAnim("max_attack", MAX_ATTACK_ANIM)
                .triggerableAnim("collision_kill", COLLISION_KILL_ANIM)
                .triggerableAnim("spin_chop", SPIN_CHOP_ANIM)
                .triggerableAnim("throw_axe", THROW_AXE_ANIM)
                .triggerableAnim("recovery_axe", RECOVERY_AXE_ANIM)
                .triggerableAnim("death", DEATH_ANIM)
                .setSoundKeyframeHandler(s -> {})
                .setCustomInstructionKeyframeHandler(s -> {
                    String instruction = s.keyframeData().getInstructions().replaceAll("\\s+", "");
                    if ("runAttack".equals(instruction)) {
                        this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "attack", this.getId()
                        ));
                    }
                    if ("runSuperAttack".equals(instruction)) {
                        this.playSound(SoundEvents.PLAYER_SMALL_FALL, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "super_attack", this.getId()
                        ));
                    }
                    if ("runMiniAttack".equals(instruction)) {
                        this.playSound(SoundEvents.PLAYER_SMALL_FALL, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "mini_attack", this.getId()
                        ));
                    }
                    if ("runMaxAttack_1".equals(instruction)) {
                        this.playSound(SoundEvents.PLAYER_SMALL_FALL, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "max_attack_1", this.getId()
                        ));
                    }
                    if ("runMaxAttack_2".equals(instruction)) {
                        this.playSound(SoundEvents.PLAYER_SMALL_FALL, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "max_attack_2", this.getId()
                        ));
                    }
                    if ("runMaxAttack_3".equals(instruction)) {
                        this.playSound(SoundEvents.PLAYER_SMALL_FALL, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "max_attack_3", this.getId()
                        ));
                    }
                    if ("runCollisionKill;".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload("collision_kill", this.getId()));
                    }
                    if ("runCollisionKill_1;".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload("collision_kill_1", this.getId()));
                    }
                    if ("runSpinChop;".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload("spin_chop", this.getId()));
                    }
                    if ("runThrowAxe;".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload("throw_axe", this.getId()));
                    }
                }));
    }

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
    private PlayState animationController(final AnimationTest<VindicatorGeneralEntity> state) {
        if (state.isMoving()) {
            return state.setAndContinue(WALK_ANIM);
        }
        return state.setAndContinue(IDEA_ANIM);
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
        this.waitingForAxeRecovery = false;
        this.axeRecoveryTimeout = 0;
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
