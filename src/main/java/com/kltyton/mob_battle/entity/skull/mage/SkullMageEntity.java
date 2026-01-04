package com.kltyton.mob_battle.entity.skull.mage;

import com.kltyton.mob_battle.entity.bullet.ITrueDamageProjectile;
import com.kltyton.mob_battle.entity.skull.IModSkullEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.ModTrackedDataHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import net.minecraft.world.entity.UniquelyIdentifiable;
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

public class SkullMageEntity extends SkeletonEntity implements GeoEntity, IModSkullEntity {
    public SkullMageEntity(EntityType<? extends SkullMageEntity> entityType, World world) {
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
                if (canSummonSkull()) performSummonSkull();
                int cd = getSkillCooldown();
                if (cd > 0) setSkillCooldown(cd - 1);
                int summonSkullCooldown = getSummonSkullCooldown();
                if (summonSkullCooldown > 0) setSummonSkullCooldown(summonSkullCooldown - 1);
            }
            this.dataTracker.get(MAGIC_PROJECTILE_ID).ifPresent(ref -> {
                Entity projectile = ((LazyEntityReference<Entity>) (Object) ref).resolve(this.getWorld(), Entity.class);
                if (projectile == null || !projectile.isAlive() || projectile.isOnGround()) {
                    this.dataTracker.set(MAGIC_PROJECTILE_ID, Optional.empty());
                }
            });
        } else {
            // 客户端渲染
            if (this.age % 2 == 0) { // 可以通过模运算控制粒子密度
                renderMagicLink();
            }
        }
    }
    public void performSummonSkull() {
        setHasSkill(true);
        this.setAiDisabled(true);
        setSummonSkullCooldown(140);
        this.triggerAnim("skill_controller", "summon_skull");
    }
    public boolean canSummonSkull() {
        return canSkill() && getSummonSkullCooldown() == 0;
    }
    private void renderMagicLink() {
        this.dataTracker.get(MAGIC_PROJECTILE_ID).ifPresent(ref -> {
            Entity projectile = ((LazyEntityReference<Entity>) (Object) ref).resolve(this.getWorld(), Entity.class);
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

                    this.getWorld().addParticleClient(
                            ParticleTypes.WITCH,
                            startX + dx * ratio + offsetX,
                            startY + dy * ratio + offsetY,
                            startZ + dz * ratio + offsetZ,
                            // 优化点：给粒子一点点向上的漂浮速度
                            0, 0.02, 0
                    );

                    // 进阶优化：如果是关键节点，添加额外的闪烁粒子
                    if (i % 5 == 0) {
                        this.getWorld().addParticleClient(
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
    protected void convertToStray() {
    }
    @Override
    public boolean canFreeze() {
        return true;
    }
    @Override
    protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier, @Nullable ItemStack shotFrom) {
        PersistentProjectileEntity persistentProjectileEntity = super.createArrowProjectile(arrow, damageModifier, shotFrom);
        ((ITrueDamageProjectile) persistentProjectileEntity).setTrueDamage(true, true);
        persistentProjectileEntity.setDamage(this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE));
        persistentProjectileEntity.setInvisible(true);
        persistentProjectileEntity.setNoGravity(true);
        // 服务端：更新数据追踪器，记录当前射出的箭
        if (!this.getWorld().isClient()) {
            this.dataTracker.set(MAGIC_PROJECTILE_ID, Optional.of(persistentProjectileEntity).map(LazyEntityReference::new));
        }
        return persistentProjectileEntity;
    }

    public static DefaultAttributeContainer.Builder addAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 900.0D)

                .add(EntityAttributes.MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.ATTACK_DAMAGE, 45.0D)
                .add(EntityAttributes.FOLLOW_RANGE, 24.0D);
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
    public void takeKnockback(double strength, double x, double z) {
        if (!this.isDead() && !this.isAiDisabled()) super.takeKnockback(strength, x, z);
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
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(SkullMageEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN = DataTracker.registerData(SkullMageEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SUMMON_SKULL_COOLDOWN = DataTracker.registerData(SkullMageEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public boolean hasSkill() {
        return getDataTracker().get(HAS_SKILL);
    }
    public int getSkillCooldown() {
        return getDataTracker().get(SKILL_COOLDOWN);
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
    public void setSummonSkullCooldown(int cooldown) {
        getDataTracker().set(SUMMON_SKULL_COOLDOWN, cooldown);
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
            SkullMageEntity.class, TrackedDataHandlerRegistry.LAZY_ENTITY_REFERENCE
    );
    public static final TrackedData<Optional<LazyEntityReference<UniquelyIdentifiable>>> MAGIC_PROJECTILE_ID =
            DataTracker.registerData(SkullMageEntity.class, ModTrackedDataHandler.ANY_ENTITY_LAZY_REFERENCE);
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SKILL, false);
        builder.add(SKILL_COOLDOWN, 0);
        builder.add(SUMMON_SKULL_COOLDOWN, 140);
        builder.add(OWNER_UUID, Optional.empty());
        builder.add(MAGIC_PROJECTILE_ID, Optional.empty());
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
