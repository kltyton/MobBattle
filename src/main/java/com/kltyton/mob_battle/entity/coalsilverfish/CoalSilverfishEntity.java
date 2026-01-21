package com.kltyton.mob_battle.entity.coalsilverfish;

import com.kltyton.mob_battle.entity.general.GeneralEntityOnlyOneSkill;
import com.kltyton.mob_battle.entity.irongolem.VillagerIronGolemEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class CoalSilverfishEntity extends SilverfishEntity implements GeoEntity, GeneralEntityOnlyOneSkill<CoalSilverfishEntity> {
    public static int COOLDOWN_TIME = 400;
    //skill
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(CoalSilverfishEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN = DataTracker.registerData(CoalSilverfishEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public CoalSilverfishEntity(EntityType<? extends SilverfishEntity> entityType, World world) {
        super(entityType, world);
        this.setAiDisabled(false);
        this.setHasSkill(false);
        this.setSkillCooldown(COOLDOWN_TIME);
    }
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SKILL, false);
        builder.add(SKILL_COOLDOWN, COOLDOWN_TIME);
    }
    @Override
    public void runSkill(CoalSilverfishEntity entity) {
        // 获取周围 7 格内的所有蠹虫（SilverfishEntity）
        double radius = 7.0;
        Box box = this.getBoundingBox().expand(radius);
        List<SilverfishEntity> silverfishList = entity.getWorld().getNonSpectatingEntities(SilverfishEntity.class, box);
        // 状态效果
        // 抗性提升 I (等级0), 15秒 (300 ticks)
        StatusEffectInstance resistance = new StatusEffectInstance(StatusEffects.RESISTANCE, 15 * 20, 0);
        // 伤害吸收 III (等级2), 15秒 (300 ticks)
        StatusEffectInstance absorption = new StatusEffectInstance(StatusEffects.ABSORPTION, 15 * 20, 2);
        // 遍历并给予效果
        for (SilverfishEntity silverfish : silverfishList) {
            silverfish.addStatusEffect(new StatusEffectInstance(resistance));
            silverfish.addStatusEffect(new StatusEffectInstance(absorption));
        }
/*            // 确保自身也能获得效果（如果自身不是 SilverfishEntity 的子类，需要额外处理）
            if (!(this instanceof SilverfishEntity)) {
                this.addStatusEffect(new StatusEffectInstance(resistance));
                this.addStatusEffect(new StatusEffectInstance(absorption));
            }*/

    }
    @Override
    public boolean hasSkill() {
        return getDataTracker().get(HAS_SKILL);
    }
    public int getSkillCooldown() {
        return getDataTracker().get(SKILL_COOLDOWN);
    }
    @Override
    public void setHasSkill(boolean hasSkill) {
        getDataTracker().set(HAS_SKILL, hasSkill);
    }
    public void setSkillCooldown(int cooldown) {
        getDataTracker().set(SKILL_COOLDOWN, cooldown);
    }

    public boolean canSkill() {
        return !this.getWorld().isClient() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }
    public void performSkill() {
        this.setHasSkill(true);
        this.setAiDisabled(true);
        this.setSkillCooldown(COOLDOWN_TIME);
        this.triggerAnim("attack_controller", "attack");
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
    public void takeKnockback(double strength, double x, double z) {
        if (!hasSkill() || !this.isAiDisabled()) {
            super.takeKnockback(strength, x, z);
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
    public static DefaultAttributeContainer.Builder createAttributes() {
        return SilverfishEntity.createSilverfishAttributes()
                .add(EntityAttributes.MAX_HEALTH, 500.0D)
                .add(EntityAttributes.ARMOR, 30.0D)
                .add(EntityAttributes.ARMOR_TOUGHNESS, 20.0D);
    }
    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        // 检查伤害是否小于 300，且触发 20% 的概率 (0.2f)
        if (amount < 300 && this.random.nextFloat() < 0.20f) {
            this.triggerAnim("attack_controller", "attack2");
            // 播放盾牌格挡音效
            world.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.HOSTILE, 1.0f, 0.8f + this.random.nextFloat() * 0.4f);
            // 返回 false
            return false;
        }

        // 如果没触发格挡，则执行原有的伤害逻辑
        return super.damage(world, source, amount);
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
