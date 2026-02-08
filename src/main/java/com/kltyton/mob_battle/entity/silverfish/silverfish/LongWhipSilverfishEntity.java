package com.kltyton.mob_battle.entity.silverfish.silverfish;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.general.GeneralEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.sounds.ModSounds;
import com.kltyton.mob_battle.utils.EntityUtil;
import com.kltyton.mob_battle.utils.TaskSchedulerUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.Set;

public class LongWhipSilverfishEntity extends SilverfishEntity implements GeneralEntity<LongWhipSilverfishEntity> {
    private static final TrackedData<Integer> GRABBED_ENTITY_ID = DataTracker.registerData(LongWhipSilverfishEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public LongWhipSilverfishEntity(EntityType<? extends LongWhipSilverfishEntity> entityType, World world) {
        super(entityType, world);
        this.setHasSkill(false);
        this.setAiDisabled(false);
    }

    @Override
    public void setBodyYaw(float bodyYaw) {
        this.bodyYaw = bodyYaw;
    }
    @Override
    public void takeKnockback(double strength, double x, double z) {
        if (!hasSkill() && !this.isAiDisabled()) super.takeKnockback(strength, x, z);
    }
    @Override
    public MobEntity getEntity() {
        return this;
    }
    @Override
    public int getSkillCount() {
        return 5;
    }
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(LongWhipSilverfishEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN_1 = DataTracker.registerData(LongWhipSilverfishEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_2 = DataTracker.registerData(LongWhipSilverfishEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_3 = DataTracker.registerData(LongWhipSilverfishEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_4 = DataTracker.registerData(LongWhipSilverfishEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_5 = DataTracker.registerData(LongWhipSilverfishEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Override
    public TrackedData<Boolean> getHasSkillKey() {
        return HAS_SKILL;
    }

    @Override
    public TrackedData<Integer> getCooldownKey1() {
        return SKILL_COOLDOWN_1;
    }

    @Override
    public TrackedData<Integer> getCooldownKey2() {
        return SKILL_COOLDOWN_2;
    }

    @Override
    public TrackedData<Integer> getCooldownKey3() {
        return SKILL_COOLDOWN_3;
    }

    @Override
    public TrackedData<Integer> getCooldownKey4() {
        return SKILL_COOLDOWN_4;
    }

    @Override
    public TrackedData<Integer> getCooldownKey5() {
        return SKILL_COOLDOWN_5;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(GRABBED_ENTITY_ID, -1);
        entityInitDataTracker(builder);
    }
    @Override
    public void performSkill(String skill) {
        this.setHasSkill(true);
        this.setAiDisabled(true);
        this.setSkillCooldown(skill);
        if (skill.equals("attack2")) {
            // 随机选择 attack2_1, attack2_2, attack2_3 中的一个
            String[] attack2Animations = {"attack2_1", "attack2_2", "attack2_3"};
            int randomIndex = (int) (Math.random() * attack2Animations.length);
            this.triggerAnim("skill_controller", attack2Animations[randomIndex]);

        } else if (skill.equals("attack6")) {
            if (this.getGrabbedEntityId() != -1) this.triggerAnim("skill_controller", "yes");
            else this.triggerAnim("skill_controller", "no");
        } else this.triggerAnim("skill_controller", skill);
    }
    @Override
    public int getMaxSkillCooldown_1() {
        return 20;
    }
    @Override
    public int getMaxSkillCooldown_2() {
        return 20 * 25;
    }
    @Override
    public int getMaxSkillCooldown_3() {
        return 20 * 20;
    }
    @Override
    public int getMaxSkillCooldown_4() {
        return 20 * 30;
    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        return doSkill();
    }
    public boolean tryBaseAttack(ServerWorld world, Entity target) {
        float f = (float)this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
        ItemStack itemStack = this.getWeaponStack();
        DamageSource damageSource = Optional.ofNullable(itemStack.getItem().getDamageSource(this)).orElse(this.getDamageSources().mobAttack(this));
        f = EnchantmentHelper.getDamage(world, itemStack, target, damageSource, f);
        f += itemStack.getItem().getBonusAttackDamage(target, f, damageSource);
        boolean bl = target.damage(world, damageSource, f);
        if (bl) {
            float g = this.getAttackKnockbackAgainst(target, damageSource);
            if (g > 0.0F && target instanceof LivingEntity livingEntity) {
                livingEntity.takeKnockback(g * 0.5F, MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)), -MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)));
                this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
            }

            if (target instanceof LivingEntity livingEntity) {
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 2), this);
                itemStack.postHit(livingEntity, this);
            }

            EnchantmentHelper.onTargetDamaged(world, target, damageSource);
            this.onAttacking(target);
            this.playAttackSound();
        }

        return bl;
    }
    @Override
    public void tick() {
        super.tick();
        entityTick();
        // 处理抓取逻辑
        int grabbedId = this.getGrabbedEntityId();
        if (grabbedId != -1 && !this.getWorld().isClient) {
            Entity grabbedEntity = this.getWorld().getEntityById(grabbedId);

            // 如果技能结束、目标死亡或目标离得太远，释放
            if (!this.hasSkill() || grabbedEntity == null || !grabbedEntity.isAlive()) {
                this.setGrabbedEntityId(-1);
            } else {
                // 计算银鱼面前的位置 (例如面前 1.5 格)
                double distance = 1.5;
                double radians = Math.toRadians(this.getYaw());
                double targetX = this.getX() - Math.sin(radians) * distance;
                double targetZ = this.getZ() + Math.cos(radians) * distance;
                double targetY = this.getY();

                // 强制传送目标并清除动能
                if (grabbedEntity instanceof LivingEntity living) {
                    living.teleport((ServerWorld) this.getWorld(), targetX, targetY, targetZ, Set.of(), grabbedEntity.getYaw(), grabbedEntity.getPitch(), true);
                    living.setVelocity(0, 0, 0);
                    living.velocityDirty = true;
                }
            }
        }
    }
    protected static final RawAnimation RUN_ANIM = RawAnimation.begin().thenPlay("run");
    protected static final RawAnimation ATTACK_ANIM_2_1 = RawAnimation.begin().thenPlay("attack2_1");
    protected static final RawAnimation ATTACK_ANIM_2_2 = RawAnimation.begin().thenPlay("attack2_2");
    protected static final RawAnimation ATTACK_ANIM_2_3 = RawAnimation.begin().thenPlay("attack2_3");
    protected static final RawAnimation YES_ANIM = RawAnimation.begin().thenPlay("yes");
    protected static final RawAnimation NO_ANIM = RawAnimation.begin().thenPlay("no");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 5, this::mainController));
        controllers.add(new AnimationController<>( "skill_controller", animTest -> {
                    if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED) {
                        if (this.hasSkill()) ClientPlayNetworking.send(new SkillPayload("stop", this.getId()));
                    }
                    return PlayState.STOP;
                })
                        .triggerableAnim("attack2_1", ATTACK_ANIM_2_1)
                        .triggerableAnim("attack2_2", ATTACK_ANIM_2_2)
                        .triggerableAnim("attack2_3", ATTACK_ANIM_2_3)
                        .triggerableAnim("attack3", ATTACK_ANIM_3)
                        .triggerableAnim("attack4", ATTACK_ANIM_4)
                        .triggerableAnim("attack5", ATTACK_ANIM_5)
                        .triggerableAnim("attack6", ATTACK_ANIM_6)
                        .triggerableAnim("yes", YES_ANIM)
                        .triggerableAnim("no", NO_ANIM)
                        .setCustomInstructionKeyframeHandler(s -> {
                            if ("runAttack2;".equals(s.keyframeData().getInstructions())) {
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack2", this.getId()
                                ));
                            }
                            if ("runAttack3;".equals(s.keyframeData().getInstructions())) {
                                this.playSound(ModSounds.B_C_BELLOW_DOG_JIAO_SOUND_EVENT);
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack3", this.getId()
                                ));
                            }
                            if ("runAttack4;".equals(s.keyframeData().getInstructions())) {
                                this.playSound(ModSounds.B_C_DEBUFF_DOG_JIAO_SOUND_EVENT);
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack4", this.getId()
                                ));
                            }
                            if ("runAttack5;".equals(s.keyframeData().getInstructions())) {
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack5", this.getId()
                                ));
                            }
                            if ("runAttack6;".equals(s.keyframeData().getInstructions())) {
                                this.playSound(ModSounds.B_C_Z_DOG_JIAO_SOUND_EVENT);
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack6", this.getId()
                                ));
                            }
                        })
        );
    }
    @Override
    public PlayState mainController(AnimationTest<?> event) {
        if (this.hasSkill()) {
            return PlayState.CONTINUE;
        }
        if (event.isMoving()) {
            return this.getTarget() == null ? event.setAndContinue(WALK_ANIM) : event.setAndContinue(RUN_ANIM);
        } else return event.setAndContinue(IDLE_ANIM);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.B_C_DOG_JIAO_SOUND_EVENT;
    }
    @Override
    public void runSkill_2(LongWhipSilverfishEntity entity) {
        tryBaseAttack((ServerWorld) this.getWorld(), this.getTarget());
    }
    @Override
    public void runSkill_3(LongWhipSilverfishEntity entity) {
        EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 7, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity -> {
            livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.DISARM_ENTRY, 160, 0));
            livingEntity.damage((ServerWorld) this.getWorld(), this.getDamageSources().indirectMagic(this, this), 10);
        });
    }
    @Override
    public void runSkill_4(LongWhipSilverfishEntity entity) {
        EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 6, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity -> {
            livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.INFESTATION_ENTRY, 100, 0));
        });
    }
    @Override
    public void runSkill_5(LongWhipSilverfishEntity entity) {
        if (this.getWorld().isClient) return;
        LivingEntity target = EntityUtil.getClosestNearbyEntity(entity, LivingEntity.class, 5, EntityUtil.TeamFilter.EXCLUDE_TEAM);
        // 2. 如果找到了目标，设置 ID
        if (target != null) {
            this.setGrabbedEntityId(target.getId());
            performSkill("attack6");
        }
    }
    @Override
    public void runSkill_6(LongWhipSilverfishEntity entity) {
        if (this.getWorld().isClient) return;
        Entity target = entity.getWorld().getEntityById(this.getGrabbedEntityId());
        if (target instanceof LivingEntity livingEntity) {
            livingEntity.damage((ServerWorld) entity.getWorld(), entity.getDamageSources().indirectMagic(entity, entity), 50);
            entity.heal(100);
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 15 * 20, 29));

            TaskSchedulerUtil.runLater(24, () -> {
                entity.setGrabbedEntityId(-1);
            });
        }
    }
    public static DefaultAttributeContainer.Builder createAttributes() {
        return SilverfishEntity.createSilverfishAttributes()
                .add(EntityAttributes.MAX_HEALTH, 3500.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 150.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.72);
    }
    public void setGrabbedEntityId(int id) {
        this.dataTracker.set(GRABBED_ENTITY_ID, id);
    }

    public int getGrabbedEntityId() {
        return this.dataTracker.get(GRABBED_ENTITY_ID);
    }
}
