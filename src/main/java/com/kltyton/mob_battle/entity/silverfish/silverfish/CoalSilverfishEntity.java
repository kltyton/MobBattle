package com.kltyton.mob_battle.entity.silverfish.silverfish;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.general.GeneralEntityOnlyOneSkill;
import com.kltyton.mob_battle.entity.irongolem.VillagerIronGolemEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class CoalSilverfishEntity extends Silverfish implements GeneralEntityOnlyOneSkill<CoalSilverfishEntity> {
    public int getCooldownTime() {
        return 400;
    }

    //skill
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(CoalSilverfishEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN = SynchedEntityData.defineId(CoalSilverfishEntity.class, EntityDataSerializers.INT);

    public CoalSilverfishEntity(EntityType<? extends Silverfish> entityType, Level world) {
        super(entityType, world);
        this.setNoAi(false);
        this.setHasSkill(false);
        this.setSkillCooldown(getCooldownTime());
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_SKILL, false);
        builder.define(SKILL_COOLDOWN, getCooldownTime());
    }
    @Override
    public void runSkill(CoalSilverfishEntity entity) {
        // 获取周围 7 格内的所有蠹虫（SilverfishEntity）
        double radius = 7.0;
        AABB box = this.getBoundingBox().inflate(radius);
        List<Silverfish> silverfishList = entity.level().getEntitiesOfClass(Silverfish.class, box);
        // 状态效果
        // 抗性提升 I (等级0), 15秒 (300 ticks)
        MobEffectInstance resistance = new MobEffectInstance(MobEffects.RESISTANCE, 15 * 20, 0);
        // 伤害吸收 III (等级2), 15秒 (300 ticks)
        MobEffectInstance absorption = new MobEffectInstance(MobEffects.ABSORPTION, 15 * 20, 2);
        for (Silverfish silverfish : silverfishList) {
            silverfish.addEffect(new MobEffectInstance(resistance));
            silverfish.addEffect(new MobEffectInstance(absorption));
        }

    }
    @Override
    public boolean hasSkill() {
        return getEntityData().get(HAS_SKILL);
    }
    public int getSkillCooldown() {
        return getEntityData().get(SKILL_COOLDOWN);
    }
    @Override
    public void setHasSkill(boolean hasSkill) {
        getEntityData().set(HAS_SKILL, hasSkill);
    }
    public void setSkillCooldown(int cooldown) {
        getEntityData().set(SKILL_COOLDOWN, cooldown);
    }
    @Override
    public boolean canSkill() {
        if (!ModSkillEntityType.canSkill(this)) return false;
        return !this.level().isClientSide() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }
    public void performSkill() {
        this.setHasSkill(true);
        this.setNoAi(true);
        this.setSkillCooldown(getCooldownTime());
        this.triggerAnim("attack_controller", "attack");
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (!hasSkill()) {
                this.setNoAi(false);
                // 冷却递减
                int cd = getSkillCooldown();
                if (cd > 0) setSkillCooldown(cd - 1);
                if (canSkill()) performSkill();
            }
        }
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation ATTACK_ANIM_2 = RawAnimation.begin().thenPlay("attack2");
    @Override
    public void knockback(double strength, double x, double z) {
        if (!hasSkill() || !this.isNoAi()) {
            super.knockback(strength, x, z);
        }
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>("main_controller", 5 ,this::animationController));
        controllerRegistrar.add(new AnimationController<>( "attack_controller",animTest -> {
                    if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED && this.hasSkill()) {
                        ClientPlayNetworking.send(new SkillPayload(
                                "stop", this.getId()
                        ));
                    }
                    return PlayState.STOP;
                })
                        .triggerableAnim("attack", ATTACK_ANIM)
                        .triggerableAnim("attack2", ATTACK_ANIM_2)
                        .setSoundKeyframeHandler(s -> {})
                        .setCustomInstructionKeyframeHandler(s -> {
                            if ("runAttack;".equals(s.keyframeData().getInstructions())) {
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack", this.getId()
                                ));
                            }
                        })
        );
    }
    private PlayState animationController(final AnimationTest<VillagerIronGolemEntity> state) {
        if (state.isMoving()) {
            return state.setAndContinue(WALK_ANIM);
        } else {
            return state.setAndContinue(IDEA_ANIM);
        }
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Silverfish.createAttributes()
                .add(Attributes.MAX_HEALTH, 700.0D)
                .add(Attributes.ATTACK_DAMAGE, 60.0D)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.68);
    }
    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        // 检查伤害是否小于 300，且触发 20% 的概率 (0.2f)
        if (canBlock() && amount < getBlockMaxDamage() && this.random.nextFloat() < getBlockProbability()) {
            this.triggerAnim("attack_controller", "attack2");
            // 播放盾牌格挡音效
            world.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, 1.0f, 0.8f + this.random.nextFloat() * 0.4f);
            // 返回 false
            return false;
        }
        return super.hurtServer(world, source, amount);
    }
    public boolean canBlock() {
        return true;
    }
    public int getBlockMaxDamage() {
        return 300;
    }
    public float getBlockProbability() {
        return 0.05f;
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
