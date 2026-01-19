package com.kltyton.mob_battle.entity.skull.king;

import com.kltyton.mob_battle.entity.accessor.BigBossLookControl;
import com.kltyton.mob_battle.entity.accessor.BigBossMoveControl;
import com.kltyton.mob_battle.entity.accessor.BigBossNavigation;
import com.kltyton.mob_battle.entity.skull.IModSkullEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.EntityUtil;
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
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
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

public class SkullKingEntity extends WitherSkeletonEntity implements GeoEntity, IModSkullEntity {
    private final ServerBossBar bossBar = new ServerBossBar(
            this.getDisplayName(),
            BossBar.Color.PURPLE,
            BossBar.Style.PROGRESS
    );
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(SkullKingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN = DataTracker.registerData(SkullKingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SUPER_ATTACK_SKILL_COOLDOWN = DataTracker.registerData(SkullKingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SUMMON_SKULL_COOLDOWN = DataTracker.registerData(SkullKingEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SKILL, false);
        builder.add(SKILL_COOLDOWN, 20);
        builder.add(SUPER_ATTACK_SKILL_COOLDOWN, 300);
        builder.add(SUMMON_SKULL_COOLDOWN, 200);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            if (!hasSkill()) {
                this.setAiDisabled(false);
                if (canSummonSkull()) performSummonSkull();
                // 冷却递减
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
    @Nullable
    public EntityData initializeBase(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        Random random = world.getRandom();
        EntityAttributeInstance entityAttributeInstance = Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE));
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
        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE)).setBaseValue(90.0D);
        this.setAiDisabled(false);
        this.updateAttackType();
        return entityData2;
    }
    public SkullKingEntity(EntityType<? extends SkullKingEntity> entityType, World world) {
        super(entityType, world);
        this.setHasSkill(false);
        this.setAiDisabled(false);
        this.setSkillCooldown(20);
        this.lookControl = new BigBossLookControl(this);
        this.moveControl = new BigBossMoveControl(this);
        this.navigation = new BigBossNavigation(this, world);
    }
    public boolean tryAttackBase(ServerWorld world, Entity target) {
        float f = 90.0F;
        ItemStack itemStack = this.getWeaponStack();
        DamageSource damageSource = Optional.ofNullable(itemStack.getItem().getDamageSource(this)).orElse(this.getDamageSources().mobAttack(this));
        f = EnchantmentHelper.getDamage(world, itemStack, target, damageSource, f);
        f += itemStack.getItem().getBonusAttackDamage(target, f, damageSource);
        if (this.isTeammate(target)) return false;
        boolean bl = target.damage(world, damageSource, f);
        if (bl) {
            float g = this.getAttackKnockbackAgainst(target, damageSource);
            target.damage(world, this.getDamageSources().magic(), 10);
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
        setSkillCooldown(20);
        this.triggerAnim("skill_controller", "attack");
    }
    public void performSuperAttack() {
        setHasSkill(true);
        this.setAiDisabled(true);
        setSkillCooldown(20);
        setSuperAttackSkillCooldown(300);
        this.triggerAnim("skill_controller", "super_attack");
    }
    public void performSummonSkull() {
        setHasSkill(true);
        this.setAiDisabled(true);
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
        return !this.getWorld().isClient() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
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
    public static DefaultAttributeContainer.Builder addAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 3500.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.ATTACK_DAMAGE, 90.0D)
                .add(EntityAttributes.FOLLOW_RANGE, 24.0D);
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
    @Override
    public void takeKnockback(double strength, double x, double z) {
        if (!this.isDead() && !this.isAiDisabled()) super.takeKnockback(strength, x, z);
    }
    public int getSummonSkullCooldown() {
        return getDataTracker().get(SUMMON_SKULL_COOLDOWN);
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
    public void setSummonSkullCooldown(int cooldown) {
        getDataTracker().set(SUMMON_SKULL_COOLDOWN, cooldown);
    }
    @Override
    public boolean canPickupItem(ItemStack stack) {
        return false;
    }
    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
    }

    @Override
    public void setOwner(LivingEntity entity) {
    }

    @Override
    public @Nullable LazyEntityReference<LivingEntity> getOwnerReference() {
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
    public void setOwner(@Nullable LazyEntityReference<LivingEntity> owner) {

    }
    public boolean canTarget(LivingEntity target) {
        if (target instanceof IModSkullEntity iModSkullEntity) return !iModSkullEntity.isOwner(this) && super.canTarget(target);
        return super.canTarget(target);
    }
}
