package com.kltyton.mob_battle.entity.silverfish.silverfish;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.general.GeneralEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.sounds.ModSounds;
import com.kltyton.mob_battle.utils.EntityUtil;
import com.kltyton.mob_battle.utils.TaskSchedulerUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.Set;

public class LongWhipSilverfishEntity extends Silverfish implements GeneralEntity<LongWhipSilverfishEntity> {
    private static final EntityDataAccessor<Integer> GRABBED_ENTITY_ID = SynchedEntityData.defineId(LongWhipSilverfishEntity.class, EntityDataSerializers.INT);
    public LongWhipSilverfishEntity(EntityType<? extends LongWhipSilverfishEntity> entityType, Level world) {
        super(entityType, world);
        this.setHasSkill(false);
        this.setNoAi(false);
    }

    @Override
    public void setYBodyRot(float bodyYaw) {
        this.yBodyRot = bodyYaw;
    }
    @Override
    public void knockback(double strength, double x, double z) {
        if (!hasSkill() && !this.isNoAi()) super.knockback(strength, x, z);
    }
    @Override
    public Mob getEntity() {
        return this;
    }
    @Override
    public int getSkillCount() {
        return 5;
    }
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(LongWhipSilverfishEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_1 = SynchedEntityData.defineId(LongWhipSilverfishEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_2 = SynchedEntityData.defineId(LongWhipSilverfishEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_3 = SynchedEntityData.defineId(LongWhipSilverfishEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_4 = SynchedEntityData.defineId(LongWhipSilverfishEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_5 = SynchedEntityData.defineId(LongWhipSilverfishEntity.class, EntityDataSerializers.INT);
    @Override
    public EntityDataAccessor<Boolean> getHasSkillKey() {
        return HAS_SKILL;
    }

    @Override
    public EntityDataAccessor<Integer> getCooldownKey1() {
        return SKILL_COOLDOWN_1;
    }

    @Override
    public EntityDataAccessor<Integer> getCooldownKey2() {
        return SKILL_COOLDOWN_2;
    }

    @Override
    public EntityDataAccessor<Integer> getCooldownKey3() {
        return SKILL_COOLDOWN_3;
    }

    @Override
    public EntityDataAccessor<Integer> getCooldownKey4() {
        return SKILL_COOLDOWN_4;
    }

    @Override
    public EntityDataAccessor<Integer> getCooldownKey5() {
        return SKILL_COOLDOWN_5;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(GRABBED_ENTITY_ID, -1);
        entityInitDataTracker(builder);
    }
    @Override
    public void performSkill(String skill) {
        this.setHasSkill(true);
        this.setNoAi(true);
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
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        return doSkill();
    }
    public boolean tryBaseAttack(ServerLevel world, Entity target) {
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        ItemStack itemStack = this.getWeaponItem();
        DamageSource damageSource = Optional.ofNullable(itemStack.getItem().getDamageSource(this)).orElse(this.damageSources().mobAttack(this));
        f = EnchantmentHelper.modifyDamage(world, itemStack, target, damageSource, f);
        f += itemStack.getItem().getAttackDamageBonus(target, f, damageSource);
        boolean bl = target.hurtServer(world, damageSource, f);
        if (bl) {
            float g = this.getKnockback(target, damageSource);
            if (g > 0.0F && target instanceof LivingEntity livingEntity) {
                livingEntity.knockback(g * 0.5F, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            if (target instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 2), this);
                itemStack.hurtEnemy(livingEntity, this);
            }

            EnchantmentHelper.doPostAttackEffects(world, target, damageSource);
            this.setLastHurtMob(target);
            this.playAttackSound();
        }

        return bl;
    }
    @Override
    public void tick() {
        super.tick();
        entityTick();
        if (!this.level().isClientSide()) {
            this.setAggressive(this.getTarget() != null);
        }
        // 处理抓取逻辑
        int grabbedId = this.getGrabbedEntityId();
        if (grabbedId != -1 && !this.level().isClientSide) {
            Entity grabbedEntity = this.level().getEntity(grabbedId);

            // 如果技能结束、目标死亡或目标离得太远，释放
            if (!this.hasSkill() || grabbedEntity == null || !grabbedEntity.isAlive()) {
                this.setGrabbedEntityId(-1);
            } else {
                // 计算银鱼面前的位置 (例如面前 1.5 格)
                double distance = 1.5;
                double radians = Math.toRadians(this.getYRot());
                double targetX = this.getX() - Math.sin(radians) * distance;
                double targetZ = this.getZ() + Math.cos(radians) * distance;
                double targetY = this.getY();

                // 强制传送目标并清除动能
                if (grabbedEntity instanceof LivingEntity living) {
                    living.teleportTo((ServerLevel) this.level(), targetX, targetY, targetZ, Set.of(), grabbedEntity.getYRot(), grabbedEntity.getXRot(), true);
                    living.setDeltaMovement(0, 0, 0);
                    living.hasImpulse = true;
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
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack3", this.getId()
                                ));
                            }
                            if ("runAttack4;".equals(s.keyframeData().getInstructions())) {
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
            return this.isAggressive() ? event.setAndContinue(RUN_ANIM) : event.setAndContinue(WALK_ANIM);
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
        tryBaseAttack((ServerLevel) this.level(), this.getTarget());
    }
    @Override
    public void runSkill_3(LongWhipSilverfishEntity entity) {
        this.makeSound(ModSounds.B_C_BELLOW_DOG_JIAO_SOUND_EVENT);
        EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 7, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity -> {
            livingEntity.addEffect(new MobEffectInstance(ModEffects.DISARM_ENTRY, 160, 0));
            livingEntity.hurtServer((ServerLevel) this.level(), this.damageSources().indirectMagic(this, this), 10);
        });
    }
    @Override
    public void runSkill_4(LongWhipSilverfishEntity entity) {
        this.makeSound(ModSounds.B_C_DEBUFF_DOG_JIAO_SOUND_EVENT);
        EntityUtil.getNearbyEntity(entity, LivingEntity.class, Object.class, 6, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity -> {
            livingEntity.addEffect(new MobEffectInstance(ModEffects.INFESTATION_ENTRY, 100, 0));
        });
    }
    @Override
    public void runSkill_5(LongWhipSilverfishEntity entity) {
        if (this.level().isClientSide) return;
        LivingEntity target = EntityUtil.getClosestNearbyEntity(entity, LivingEntity.class, 5, EntityUtil.TeamFilter.EXCLUDE_TEAM);
        // 2. 如果找到了目标，设置 ID
        if (target != null) {
            this.setGrabbedEntityId(target.getId());
            performSkill("attack6");
        }
    }
    @Override
    public void runSkill_6(LongWhipSilverfishEntity entity) {
        if (this.level().isClientSide) return;
        Entity target = entity.level().getEntity(this.getGrabbedEntityId());
        if (target instanceof LivingEntity livingEntity) {
            this.makeSound(ModSounds.B_C_Z_DOG_JIAO_SOUND_EVENT);
            livingEntity.hurtServer((ServerLevel) entity.level(), entity.damageSources().indirectMagic(entity, entity), 50);
            entity.heal(100);
            entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 15 * 20, 29));
            TaskSchedulerUtil.runLater(24, () -> {
                entity.setGrabbedEntityId(-1);
            });
        }
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Silverfish.createAttributes()
                .add(Attributes.MAX_HEALTH, 3500.0)
                .add(Attributes.ATTACK_DAMAGE, 150.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.72);
    }
    public void setGrabbedEntityId(int id) {
        this.entityData.set(GRABBED_ENTITY_ID, id);
    }

    public int getGrabbedEntityId() {
        return this.entityData.get(GRABBED_ENTITY_ID);
    }
}
