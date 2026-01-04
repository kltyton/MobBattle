package com.kltyton.mob_battle.entity.witherskeletonking;

import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
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

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Objects;
import java.util.Optional;

public class WitherSkeletonKingEntity extends WitherSkeletonEntity  implements GeoEntity {

    private final ServerBossBar bossBar = new ServerBossBar(
            this.getDisplayName(),
            BossBar.Color.PURPLE,
            BossBar.Style.PROGRESS
    );
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(WitherSkeletonKingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN = DataTracker.registerData(WitherSkeletonKingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SUPER_ATTACK_SKILL_COOLDOWN = DataTracker.registerData(WitherSkeletonKingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SHOT_WITHER_SKULL_COOLDOWN = DataTracker.registerData(WitherSkeletonKingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SHOT_ALL_WITHER_SKULL_COOLDOWN = DataTracker.registerData(WitherSkeletonKingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SKILL, false);
        builder.add(SKILL_COOLDOWN, 0);
        builder.add(SUPER_ATTACK_SKILL_COOLDOWN, 15 * 20);
        builder.add(SHOT_WITHER_SKULL_COOLDOWN, 16 * 20);
        builder.add(SHOT_ALL_WITHER_SKULL_COOLDOWN, 50 * 20);
    }
    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        RegistryEntry<StatusEffect> effectType = effect.getEffectType();
        if (effectType.equals(StatusEffects.SLOWNESS)) {
            return false;
        }
        return super.canHaveStatusEffect(effect);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            if (!hasSkill()) {
                this.setAiDisabled(false);
                if (canShotWitherSkull()) performShotWitherSkull();
                if (canShotAllWitherSkull()) performShotAllWitherSkull();

                // 冷却递减
                int cd = getSkillCooldown();
                if (cd > 0) setSkillCooldown(cd - 1);
                int superAttackCd = getSuperAttackSkillCooldown();
                if (superAttackCd > 0) setSuperAttackSkillCooldown(superAttackCd - 1);
                int shotWitherSkullCd = getShotWitherSkullCooldown();
                if (shotWitherSkullCd > 0) setShotWitherSkullCooldown(shotWitherSkullCd - 1);
                int shotAllWitherSkullCd = getShotAllWitherSkullCooldown();
                if (shotAllWitherSkullCd > 0) setShotAllWitherSkullCooldown(shotAllWitherSkullCd - 1);
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
    public boolean isInAttackRange(LivingEntity entity) {
        Box attackBox = this.getAttackBox().expand(1.25);
        return attackBox.intersects(entity.getHitbox());
    }
    @Nullable
    public EntityData initializeBase(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        Random random = world.getRandom();
        EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE));
        if (!entityAttributeInstance.hasModifier(RANDOM_SPAWN_BONUS_MODIFIER_ID)) {
            entityAttributeInstance.addPersistentModifier(
                    new EntityAttributeModifier(RANDOM_SPAWN_BONUS_MODIFIER_ID, random.nextTriangular(0.0, 0.11485000000000001), EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            );
        }

        this.setLeftHanded(random.nextFloat() < 0.05F);
        return entityData;
    }
    @Nullable
    public EntityData initializeBase2(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        entityData = this.initializeBase(world, difficulty, spawnReason, entityData);
        Random random = world.getRandom();
        this.initEquipment(random, difficulty);
        this.updateEnchantments(world, random, difficulty);
        this.updateAttackType();
        this.setCanPickUpLoot(random.nextFloat() < 0.55F * difficulty.getClampedLocalDifficulty());
        if (this.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localDate = LocalDate.now();
            int i = localDate.get(ChronoField.DAY_OF_MONTH);
            int j = localDate.get(ChronoField.MONTH_OF_YEAR);
            if (j == 10 && i == 31 && random.nextFloat() < 0.25F) {
                this.equipStack(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.setEquipmentDropChance(EquipmentSlot.HEAD, 0.0F);
            }
        }

        return entityData;
    }
    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        EntityData entityData2 = this.initializeBase2(world, difficulty, spawnReason, entityData);
        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE)).setBaseValue(85.0D);
        this.setAiDisabled(false);
        this.updateAttackType();
        return entityData2;
    }
    public WitherSkeletonKingEntity(EntityType<? extends WitherSkeletonEntity> entityType, World world) {
        super(entityType, world);
        this.setHasSkill(false);
        this.setAiDisabled(false);
        this.setSkillCooldown(0);
        addStatusEffect(new StatusEffectInstance(
                StatusEffects.FIRE_RESISTANCE,
                -1,
                0,
                true, true, true));
    }
    @Override
    public boolean canPickupItem(ItemStack stack) {
        return false;
    }
    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
    }
    private boolean tryAttackBase(ServerWorld world, Entity target) {
        float f = 85.0F;
        ItemStack itemStack = this.getWeaponStack();
        DamageSource damageSource = Optional.ofNullable(itemStack.getItem().getDamageSource(this)).orElse(this.getDamageSources().mobAttack(this));
        f = EnchantmentHelper.getDamage(world, itemStack, target, damageSource, f);
        f += itemStack.getItem().getBonusAttackDamage(target, f, damageSource);
        if (this.isTeammate(target)) return false;
        boolean bl = target.damage(world, damageSource, f);
        if (bl) {
            float g = this.getAttackKnockbackAgainst(target, damageSource);
            if (g > 0.0F && target instanceof LivingEntity livingEntity) {
                livingEntity.takeKnockback(g * 0.5F, MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)), -MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)));
                this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
            }

            if (target instanceof LivingEntity livingEntity) {
                itemStack.postHit(livingEntity, this);
            }

            EnchantmentHelper.onTargetDamaged(world, target, damageSource);
            this.onAttacking(target);
            this.playAttackSound();
        }
        return bl;
    }
    public boolean tryAttackBase2(ServerWorld world, Entity target) {
        if (!this.tryAttackBase(world, target)) {
            return false;
        } else {
            if (target instanceof LivingEntity livingEntity) {
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 200), this);
                if (livingEntity.isDead()) this.heal(20.0F);
                if (!(target instanceof PlayerEntity)) {
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 1), this);
                }
            }
            return true;
        }
    }
    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
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
        this.setAiDisabled(true);
        setSkillCooldown(10);
        this.triggerAnim("skill_controller", "attack");
    }
    public boolean isHealthy(double health) {
        return this.getHealth() <= this.getMaxHealth() * health;
    }
    public void performSuperAttack() {
        setHasSkill(true);
        this.setAiDisabled(true);
        int cd = 300;
        if (this.isHealthy(0.35)) cd = 220;
        else if (this.isHealthy(0.75)) cd = 260;
        setSuperAttackSkillCooldown(cd);
        this.triggerAnim("skill_controller", "super_attack");
    }
    public void performShotWitherSkull() {
        setHasSkill(true);
        this.setAiDisabled(true);
        setShotWitherSkullCooldown(this.isHealthy(0.35) ? 160 : 320);
        this.triggerAnim("skill_controller", "shot_wither_skull");
    }
    public void performShotAllWitherSkull() {
        setHasSkill(true);
        this.setAiDisabled(true);
        setShotAllWitherSkullCooldown(isHealthy(0.35) ? 600 : 1000);
        this.triggerAnim("skill_controller", "shot_all_wither_skull");
    }
    public boolean canSuperAttack() {
        return canSkill() && getSuperAttackSkillCooldown() == 0;
    }
    public boolean canShotWitherSkull() {
        return this.getHealth() <= this.getMaxHealth() * 0.75 && canSkill() && getShotWitherSkullCooldown() == 0;
    }
    public boolean canShotAllWitherSkull() {
        return this.getHealth() <= this.getMaxHealth() * 0.35 && canSkill() && getShotAllWitherSkullCooldown() == 0;
    }
    public boolean canSkill() {
        return !this.getWorld().isClient() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("new3");
    protected static final RawAnimation SUPER_ATTACK_ANIM = RawAnimation.begin().thenPlay("new4");
    protected static final RawAnimation SHOT_WITHER_SKULL = RawAnimation.begin().thenPlay("new6");
    protected static final RawAnimation SHOT_ALL_WITHER_SKULL = RawAnimation.begin().thenPlay("new2");

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
                .triggerableAnim("shot_all_wither_skull", SHOT_ALL_WITHER_SKULL)
                .setSoundKeyframeHandler(s -> {})
                .setCustomInstructionKeyframeHandler(s -> {
                    PlayerEntity player = ClientUtil.getClientPlayer();
                    if ("runAttack".equals(s.keyframeData().getInstructions())) {
                        player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "attack", this.getId()
                        ));
                    }
                    if ("runSuperAttack".equals(s.keyframeData().getInstructions())) {
                        player.playSound(SoundEvents.ENTITY_PLAYER_SMALL_FALL, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "super_attack", this.getId()
                        ));
                    }
                    if ("runWitherSkull".equals(s.keyframeData().getInstructions())) {
                        ClientPlayNetworking.send(new SkillPayload(
                                "shot_wither_skull", this.getId()
                        ));
                    }
                    if ("runShotAllWitherSkull".equals(s.keyframeData().getInstructions())) {
                        ClientPlayNetworking.send(new SkillPayload(
                                "shot_all_wither_skull", this.getId()
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
    public static DefaultAttributeContainer.Builder addAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 12000.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(EntityAttributes.ATTACK_KNOCKBACK, 1.5D)
                .add(EntityAttributes.ATTACK_DAMAGE, 85.0D)
                .add(EntityAttributes.FOLLOW_RANGE, 24.0D)
                .add(EntityAttributes.ARMOR, 25.0D)
                .add(EntityAttributes.ARMOR_TOUGHNESS, 20.0D);
    }
    boolean isPlaySound = false;
    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        if (!this.getWorld().isClient() && this.isHealthy(0.35)) {
            this.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SPEED,
                    -1,
                    0,
                    true, true, true));
            this.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.RESISTANCE,
                    -1,
                    0,
                    true, true, true));
        }
        if (!this.getWorld().isClient() && this.getHealth() == this.getMaxHealth() * 0.35 && !isPlaySound) {
            this.getWorld().playSound(this, this.getX(), this.getY(), this.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.HOSTILE, 3.0F, 1.0F);
            isPlaySound = true;
        }
        if (this.bossBar != null) {
            this.bossBar.setPercent(health / this.getMaxHealth());
            this.bossBar.setName(Objects.requireNonNull(this.getDisplayName()).copy().append(" | " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()));
        }
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
    public int getShotWitherSkullCooldown() {
        return getDataTracker().get(SHOT_WITHER_SKULL_COOLDOWN);
    }
    public int getShotAllWitherSkullCooldown() {
        return getDataTracker().get(SHOT_ALL_WITHER_SKULL_COOLDOWN);
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
    public void setShotWitherSkullCooldown(int cooldown) {
        getDataTracker().set(SHOT_WITHER_SKULL_COOLDOWN, cooldown);
    }
    public void setShotAllWitherSkullCooldown(int cooldown) {
        getDataTracker().set(SHOT_ALL_WITHER_SKULL_COOLDOWN, cooldown);
    }
}
