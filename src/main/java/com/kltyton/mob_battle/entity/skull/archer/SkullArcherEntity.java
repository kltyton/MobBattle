package com.kltyton.mob_battle.entity.skull.archer;

import com.kltyton.mob_battle.entity.bullet.ITrueDamageProjectile;
import com.kltyton.mob_battle.entity.skull.IModSkullEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
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

import java.util.Optional;

public class SkullArcherEntity extends SkeletonEntity implements GeoEntity, IModSkullEntity {
    public SkullArcherEntity(EntityType<? extends SkullArcherEntity> entityType, World world) {
        super(entityType, world);
        this.setHasSkill(false);
        this.setAiDisabled(false);
        this.setSkillCooldown(0);
    }
    @Override
    public boolean isConverting() {
        return false;
    }
    @Override
    public void setConverting(boolean converting) {
    }
    @Override
    public boolean isShaking() {
        return false;
    }
    @Override
    public void tick() {
        super.tick();   // 调用父类 AI、移动等更新
        if (!this.getWorld().isClient()) {
            if (!hasSkill()) {
                this.setAiDisabled(false);
                // 冷却递减
                int cd = getSkillCooldown();
                if (cd > 0) setSkillCooldown(cd - 1);
            }
        }
    }
    @Override
    protected void convertToStray() {
    }
    @Override
    public boolean canFreeze() {
        return true;
    }
    @Override
    protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier, @Nullable ItemStack shotFrom) {
        PersistentProjectileEntity persistentProjectileEntity = super.createArrowProjectile(arrow, damageModifier, shotFrom);
        ((ITrueDamageProjectile) persistentProjectileEntity).setTrueDamage(true, false);
        persistentProjectileEntity.setDamage(this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE));
        return persistentProjectileEntity;
    }
    public static DefaultAttributeContainer.Builder addAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 120.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.ATTACK_DAMAGE, 120.0D)
                .add(EntityAttributes.FOLLOW_RANGE, 24.0D);
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
        ItemStack bowStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
        ItemStack arrowStack = this.getProjectileType(bowStack);
        PersistentProjectileEntity projectile = this.createArrowProjectile(arrowStack, pullProgress, bowStack);

        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - projectile.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);  // 水平距离
        projectile.setVelocity(d, e + g * 0.2F, f, 1.6F, 0.0F);

        // 如果是服务器端，生成箭矢实体
        if (!this.getWorld().isClient) {
            this.getWorld().spawnEntity(projectile);
        }
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }
    @Override
    public void takeKnockback(double strength, double x, double z) {
        if (!this.isDead() && !this.isAiDisabled()) super.takeKnockback(strength, x, z);
    }
    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        if (canSkill()) {
            performAttack(target, pullProgress);
        }
    }
    public void performAttack(LivingEntity target, float pullProgress) {
        this.setHasSkill(true);
        this.setAiDisabled(true);
        this.setTarget(target);
        this.setSkillCooldown(10);
        this.triggerAnim("skill_controller", "attack");
    }
    public boolean canSkill() {
        return !this.getWorld().isClient() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(SkullArcherEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN = DataTracker.registerData(SkullArcherEntity.class, TrackedDataHandlerRegistry.INTEGER);
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


    protected static final TrackedData<Optional<LazyEntityReference<LivingEntity>>> OWNER_UUID = DataTracker.registerData(
            SkullArcherEntity.class, TrackedDataHandlerRegistry.LAZY_ENTITY_REFERENCE
    );
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SKILL, false);
        builder.add(SKILL_COOLDOWN, 0);
        builder.add(OWNER_UUID, Optional.empty());
    }
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
    public boolean canTarget(LivingEntity target) {
        return !this.isOwner(target) && super.canTarget(target);
    }
    @Override
    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }
}
