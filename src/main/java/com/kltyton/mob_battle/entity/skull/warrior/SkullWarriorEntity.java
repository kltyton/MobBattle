package com.kltyton.mob_battle.entity.skull.warrior;

import com.kltyton.mob_battle.entity.skull.IModSkullEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
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

public class SkullWarriorEntity extends WitherSkeletonEntity implements GeoEntity, IModSkullEntity {
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(SkullWarriorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN = DataTracker.registerData(SkullWarriorEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SKILL, false);
        builder.add(SKILL_COOLDOWN, 0);
        builder.add(OWNER_UUID, Optional.empty());
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            if (!hasSkill()) {
                this.setAiDisabled(false);
                // 冷却递减
                int cd = getSkillCooldown();
                if (cd > 0) setSkillCooldown(cd - 1);
            }
        }
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
        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE)).setBaseValue(80.0D);
        this.setAiDisabled(false);
        this.updateAttackType();
        return entityData2;
    }
    public SkullWarriorEntity(EntityType<? extends SkullWarriorEntity> entityType, World world) {
        super(entityType, world);
        this.setHasSkill(false);
        this.setAiDisabled(false);
        this.setSkillCooldown(0);
    }
    public boolean tryAttackBase(ServerWorld world, Entity target) {
        float f = 80.0F;
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
    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        if (canSkill()) {
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
    public boolean canSkill() {
        return !this.getWorld().isClient() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
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
                .setSoundKeyframeHandler(s -> {})
                .setCustomInstructionKeyframeHandler(s -> {
                    PlayerEntity player = ClientUtil.getClientPlayer();
                    if ("runAttack".equals(s.keyframeData().getInstructions())) {
                        player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
                        ClientPlayNetworking.send(new SkillPayload(
                                "attack", this.getId()
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
                .add(EntityAttributes.MAX_HEALTH, 200.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.ATTACK_DAMAGE, 80.0D)
                .add(EntityAttributes.FOLLOW_RANGE, 24.0D);
    }
    public boolean hasSkill() {
        return getDataTracker().get(HAS_SKILL);
    }
    public int getSkillCooldown() {
        return getDataTracker().get(SKILL_COOLDOWN);
    }
    public void setHasSkill(boolean hasSkill) {
        getDataTracker().set(HAS_SKILL, hasSkill);
    }
    public void setSkillCooldown(int cooldown) {
        getDataTracker().set(SKILL_COOLDOWN, cooldown);
    }
    @Override
    public boolean canPickupItem(ItemStack stack) {
        return false;
    }
    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
    }
    protected static final TrackedData<Optional<LazyEntityReference<LivingEntity>>> OWNER_UUID = DataTracker.registerData(
            SkullWarriorEntity.class, TrackedDataHandlerRegistry.LAZY_ENTITY_REFERENCE
    );
    @Nullable
    public LazyEntityReference<LivingEntity> getOwnerReference() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }
    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        LazyEntityReference<LivingEntity> lazyEntityReference = this.getOwnerReference();
        LazyEntityReference.writeData(lazyEntityReference, view, "Owner");
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        LazyEntityReference<LivingEntity> lazyEntityReference = LazyEntityReference.fromDataOrPlayerName(view, "Owner", this.getWorld());
        if (lazyEntityReference != null) {
            this.dataTracker.set(OWNER_UUID, Optional.of(lazyEntityReference));
        } else {
            this.dataTracker.set(OWNER_UUID, Optional.empty());
        }
    }
    @Override
    public void setOwner(@Nullable LivingEntity owner) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(owner).map(LazyEntityReference::new));
    }
    @Override
    public void setOwner(@Nullable LazyEntityReference<LivingEntity> owner) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(owner));
    }
    @Override
    public void takeKnockback(double strength, double x, double z) {
        if (!this.isDead() && !this.isAiDisabled()) super.takeKnockback(strength, x, z);
    }
    @Override
    public boolean canTarget(LivingEntity target) {
        return !this.isOwner(target) && super.canTarget(target);
    }
    @Override
    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }
}
