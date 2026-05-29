package com.kltyton.mob_battle.entity.skull.archer;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.bullet.ITrueDamageProjectile;
import com.kltyton.mob_battle.entity.skull.IModSkullEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
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
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

public class SkullArcherEntity extends Skeleton implements GeoEntity, IModSkullEntity {
    public SkullArcherEntity(EntityType<? extends SkullArcherEntity> entityType, Level world) {
        super(entityType, world);
        this.setHasSkill(false);
        this.setNoAi(false);
        this.setSkillCooldown(0);
    }
    @Override
    public boolean isFreezeConverting() {
        return false;
    }
    @Override
    public void setFreezeConverting(boolean converting) {
    }
    @Override
    public boolean isShaking() {
        return false;
    }
    @Override
    public void tick() {
        super.tick();   // 调用父类 AI、移动等更新
        if (!this.level().isClientSide()) {
            killSlave();
            if (!hasSkill()) {
                this.setNoAi(false);
                // 冷却递减
                int cd = getSkillCooldown();
                if (cd > 0) setSkillCooldown(cd - 1);
            }
        }
    }
    @Override
    protected void doFreezeConversion() {
    }
    @Override
    public boolean canFreeze() {
        return true;
    }
    @Override
    protected AbstractArrow getArrow(ItemStack arrow, float damageModifier, @Nullable ItemStack shotFrom) {
        AbstractArrow persistentProjectileEntity = super.getArrow(arrow, damageModifier, shotFrom);
        ((ITrueDamageProjectile) persistentProjectileEntity).setTrueDamage(true, false);
        persistentProjectileEntity.setBaseDamage(this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        return persistentProjectileEntity;
    }
    public static AttributeSupplier.Builder addAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 120.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 120.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
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
                    if ("runAttack".equals(s.keyframeData().getInstructions())) {
                        ClientPlayNetworking.send(new SkillPayload(
                                "attack", this.getId()
                        ));
                    }
                }));
    }
    public void shootAtBase(LivingEntity target, float pullProgress) {
        if (!ModSkillEntityType.canSkill(this)) return;
        ItemStack bowStack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
        ItemStack arrowStack = this.getProjectile(bowStack);
        AbstractArrow projectile = this.getArrow(arrowStack, pullProgress, bowStack);

        double d = target.getX() - this.getX();
        double e = target.getY(0.3333333333333333) - projectile.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);  // 水平距离
        projectile.shoot(d, e + g * 0.2F, f, 1.6F, 0.0F);

        // 如果是服务器端，生成箭矢实体
        if (!this.level().isClientSide) {
            this.level().addFreshEntity(projectile);
        }
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }
    @Override
    public void knockback(double strength, double x, double z) {
        if (!this.isDeadOrDying() && !this.isNoAi()) super.knockback(strength, x, z);
    }
    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        if (!EntityUtil.isValidSummonCombatTarget(this, this.getOwner(), target)) {
            return;
        }
        if (canSkill()) {
            performAttack(target, pullProgress);
        }
    }
    public void performAttack(LivingEntity target, float pullProgress) {
        this.setHasSkill(true);
        this.setNoAi(true);
        this.setTarget(target);
        this.setSkillCooldown(10);
        this.triggerAnim("skill_controller", "attack");
    }
    public boolean canSkill() {
        if (!ModSkillEntityType.canSkill(this)) return false;
        return !this.level().isClientSide() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(SkullArcherEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN = SynchedEntityData.defineId(SkullArcherEntity.class, EntityDataSerializers.INT);
    public boolean hasSkill() {
        return getEntityData().get(HAS_SKILL);
    }
    public int getSkillCooldown() {
        return getEntityData().get(SKILL_COOLDOWN);
    }
    public void setHasSkill(boolean hasSkill) {
        getEntityData().set(HAS_SKILL, hasSkill);
    }
    public void setSkillCooldown(int cooldown) {
        getEntityData().set(SKILL_COOLDOWN, cooldown);
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


    protected static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> OWNER_UUID = SynchedEntityData.defineId(
            SkullArcherEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE
    );
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_SKILL, false);
        builder.define(SKILL_COOLDOWN, 0);
        builder.define(OWNER_UUID, Optional.empty());
    }
    @Nullable
    public EntityReference<LivingEntity> getOwnerReference() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }
    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        EntityReference<LivingEntity> lazyEntityReference = this.getOwnerReference();
        EntityReference.store(lazyEntityReference, view, "Owner");
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        EntityReference<LivingEntity> lazyEntityReference = EntityReference.readWithOldOwnerConversion(view, "Owner", this.level());
        if (lazyEntityReference != null) {
            this.entityData.set(OWNER_UUID, Optional.of(lazyEntityReference));
        } else {
            this.entityData.set(OWNER_UUID, Optional.empty());
        }
    }
    @Override
    public void setOwner(@Nullable LivingEntity owner) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(owner).map(EntityReference::new));
    }
    @Override
    public void setOwner(@Nullable EntityReference<LivingEntity> owner) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(owner));
    }
    @Override
    public boolean canAttack(LivingEntity target) {
        return EntityUtil.isValidSummonCombatTarget(this, this.getOwner(), target) && super.canAttack(target);
    }
    @Override
    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }
}
