package com.kltyton.mob_battle.entity.skull.mage;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.bullet.ITrueDamageProjectile;
import com.kltyton.mob_battle.entity.skull.IModSkullEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.EntityUtil;
import com.kltyton.mob_battle.utils.ModTrackedDataHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.entity.UniquelyIdentifyable;
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

public class SkullMageEntity extends Skeleton implements GeoEntity, IModSkullEntity {
    public SkullMageEntity(EntityType<? extends SkullMageEntity> entityType, Level world) {
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
                if (canSummonSkull()) performSummonSkull();
                int cd = getSkillCooldown();
                if (cd > 0) setSkillCooldown(cd - 1);
                int summonSkullCooldown = getSummonSkullCooldown();
                if (summonSkullCooldown > 0) setSummonSkullCooldown(summonSkullCooldown - 1);
            }
            this.entityData.get(MAGIC_PROJECTILE_ID).ifPresent(ref -> {
                Entity projectile = ((EntityReference<Entity>) (Object) ref).getEntity(this.level(), Entity.class);
                if (projectile == null || !projectile.isAlive() || projectile.onGround()) {
                    this.entityData.set(MAGIC_PROJECTILE_ID, Optional.empty());
                }
            });
        } else {
            // 客户端渲染
            if (this.tickCount % 2 == 0) { // 可以通过模运算控制粒子密度
                renderMagicLink();
            }
        }
    }
    public void performSummonSkull() {
        setHasSkill(true);
        this.setNoAi(true);
        setSummonSkullCooldown(140);
        this.triggerAnim("skill_controller", "summon_skull");
    }
    public boolean canSummonSkull() {
        return canSkill() && getSummonSkullCooldown() == 0;
    }
    private void renderMagicLink() {
        this.entityData.get(MAGIC_PROJECTILE_ID).ifPresent(ref -> {
            Entity projectile = ((EntityReference<Entity>) (Object) ref).getEntity(this.level(), Entity.class);
            if (projectile != null && projectile.isAlive()) {
                double startX = this.getX();
                double startY = this.getEyeY() - 0.2;
                double startZ = this.getZ();

                double endX = projectile.getX();
                double endY = projectile.getY();
                double endZ = projectile.getZ();

                double dx = endX - startX;
                double dy = endY - startY;
                double dz = endZ - startZ;
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

                // 优化：根据距离动态调整粒子密度
                int particles = (int) (distance * 3);

                for (int i = 0; i < particles; i++) {
                    double ratio = (double) i / particles;

                    // 优化点：加入随机偏移 (Jitter)，让连线有能量脉动感
                    double offsetX = (this.random.nextDouble() - 0.5) * 0.1;
                    double offsetY = (this.random.nextDouble() - 0.5) * 0.1;
                    double offsetZ = (this.random.nextDouble() - 0.5) * 0.1;

                    this.level().addParticle(
                            ParticleTypes.WITCH,
                            startX + dx * ratio + offsetX,
                            startY + dy * ratio + offsetY,
                            startZ + dz * ratio + offsetZ,
                            // 优化点：给粒子一点点向上的漂浮速度
                            0, 0.02, 0
                    );

                    // 进阶优化：如果是关键节点，添加额外的闪烁粒子
                    if (i % 5 == 0) {
                        this.level().addParticle(
                                ParticleTypes.INSTANT_EFFECT,
                                startX + dx * ratio, startY + dy * ratio, startZ + dz * ratio,
                                0, 0, 0
                        );
                    }
                }
            }
        });
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
        ((ITrueDamageProjectile) persistentProjectileEntity).setTrueDamage(true, true);
        persistentProjectileEntity.setBaseDamage(this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        persistentProjectileEntity.setInvisible(true);
        persistentProjectileEntity.setNoGravity(true);
        // 服务端：更新数据追踪器，记录当前射出的箭
        if (!this.level().isClientSide()) {
            this.entityData.set(MAGIC_PROJECTILE_ID, Optional.of(persistentProjectileEntity).map(EntityReference::new));
        }
        return persistentProjectileEntity;
    }

    public static AttributeSupplier.Builder addAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 900.0D)

                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 45.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
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
                .triggerableAnim("summon_skull", SUMMON_SKULL_ANIM)
                .setSoundKeyframeHandler(s -> {})
                .setCustomInstructionKeyframeHandler(s -> {
                    if ("runAttack".equals(s.keyframeData().getInstructions())) {
                        ClientPlayNetworking.send(new SkillPayload(
                                "attack", this.getId()
                        ));
                    }
                    if ("runSummonSkull".equals(s.keyframeData().getInstructions())) {
                        ClientPlayNetworking.send(new SkillPayload(
                                "summon_skull", this.getId()
                        ));
                    }
                }));
    }
    @Override
    public void knockback(double strength, double x, double z) {
        if (!this.isDeadOrDying() && !this.isNoAi()) super.knockback(strength, x, z);
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
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(SkullMageEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN = SynchedEntityData.defineId(SkullMageEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SUMMON_SKULL_COOLDOWN = SynchedEntityData.defineId(SkullMageEntity.class, EntityDataSerializers.INT);

    public boolean hasSkill() {
        return getEntityData().get(HAS_SKILL);
    }
    public int getSkillCooldown() {
        return getEntityData().get(SKILL_COOLDOWN);
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
    public void setSummonSkullCooldown(int cooldown) {
        getEntityData().set(SUMMON_SKULL_COOLDOWN, cooldown);
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
            SkullMageEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE
    );
    public static final EntityDataAccessor<Optional<EntityReference<UniquelyIdentifyable>>> MAGIC_PROJECTILE_ID =
            SynchedEntityData.defineId(SkullMageEntity.class, ModTrackedDataHandler.ANY_ENTITY_LAZY_REFERENCE);
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_SKILL, false);
        builder.define(SKILL_COOLDOWN, 0);
        builder.define(SUMMON_SKULL_COOLDOWN, 140);
        builder.define(OWNER_UUID, Optional.empty());
        builder.define(MAGIC_PROJECTILE_ID, Optional.empty());
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
