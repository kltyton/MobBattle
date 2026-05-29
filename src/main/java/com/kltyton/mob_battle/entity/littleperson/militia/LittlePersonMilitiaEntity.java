package com.kltyton.mob_battle.entity.littleperson.militia;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.OwnedSummon;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillager;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class LittlePersonMilitiaEntity extends Monster implements LittlePersonEntity, OwnedSummon {
    public String[] attackVariants;
    @Nullable
    private LivingEntity summonOwner;
    public LittlePersonMilitiaEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false)); // 添加僵尸攻击目标
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0)); // 添加远距离游荡目标
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F)); // 添加看向玩家的目标
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this)); // 添加环顾四周的目标
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, 10, true, false, this::canTargetAsSummon)); // 添加攻击傀儡目标
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, WarriorVillager.class, 10, true, false, this::canTargetAsSummon));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::canTargetAsSummon)); // 添加主动攻击玩家目标
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false,
                (entity, world) -> entity instanceof Enemy && !(entity instanceof LittlePersonEntity) && canTargetAsSummon(entity, world)));
    }

    private boolean canTargetAsSummon(LivingEntity target, ServerLevel world) {
        return EntityUtil.isValidSummonCombatTarget(this, this.summonOwner, target);
    }

    public void setSummonOwner(@Nullable LivingEntity summonOwner) {
        this.summonOwner = summonOwner;
        if (summonOwner != null) {
            EntityUtil.joinSameTeam(this, summonOwner);
        }
    }

    @Nullable
    @Override
    public LivingEntity getSummonOwner() {
        return this.summonOwner;
    }

    protected boolean isValidSummonTarget(LivingEntity target) {
        return EntityUtil.isValidSummonCombatTarget(this, this.summonOwner, target);
    }
    public static AttributeSupplier.Builder createLittlePersonMilitiaAttributes() {
        return LittlePersonEntity.createLittlePersonAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0);
    }

    @Override
    public void heal() {
        this.heal(1.0F);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.tickCount % 20 == 0) this.heal();
        }
    }
    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (target instanceof LivingEntity living && !isValidSummonTarget(living)) {
            return false;
        }
        if (!canSkill()) return false;
        if (attackVariants != null) {

            int randomIndex = (int) (Math.random() * attackVariants.length);
            this.triggerAnim("skill_controller", attackVariants[randomIndex]);

        } else this.triggerAnim("attack_controller", "attack");
        boolean bl = super.doHurtTarget(world, target);
        if (bl && target instanceof LivingEntity livingEntity) {
            attackAdditional(livingEntity);
        }
        return bl;
    }
    @Override
    public boolean canSkill() {
        return ModSkillEntityType.canSkill(this);
    }
    @Override
    public int blockProbability() {
        return 20;
    }
    @Override
    public float maxBlockDamage() {
        return 35f;
    }
    @Override
    public boolean blockAttack(@NotNull DamageSource source, float amount) {
        // 检查伤害是否来自实体直接攻击
        if (source.getDirectEntity() instanceof Entity &&
                !source.is(DamageTypeTags.IS_FALL) &&
                !source.is(DamageTypeTags.IS_FIRE) &&
                !source.is(DamageTypeTags.IS_EXPLOSION) &&
                !source.is(DamageTypeTags.IS_DROWNING) &&
                !source.is(DamageTypeTags.IS_FREEZING) &&
                !source.is(DamageTypeTags.IS_LIGHTNING) &&
                !source.is(DamageTypeTags.BURN_FROM_STEPPING) &&
                !source.is(DamageTypeTags.WITCH_RESISTANT_TO) &&
                amount <= maxBlockDamage()) {

            // 20%概率免疫伤害
            if (this.random.nextInt(100) < blockProbability()) {
                this.playSound(SoundEvents.SHIELD_BLOCK.value(), 1.0F, 1.0F);
                this.triggerAnim("attack_controller", "block");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if (blockAttack(source, amount)) return false;
        return super.hurtServer(world, source, amount);
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation IDLE_SOMETIME_ANIM = RawAnimation.begin().thenPlay("idle_sometimes");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation ATTACK_ANIM_VARIANT_1 = RawAnimation.begin().thenPlay("attack_1");
    protected static final RawAnimation ATTACK_ANIM_VARIANT_2 = RawAnimation.begin().thenPlay("attack_2");
    protected static final RawAnimation ATTACK_ANIM_VARIANT_3 = RawAnimation.begin().thenPlay("attack_3");
    protected static final RawAnimation BLOCK_ANIM = RawAnimation.begin().thenPlay("block");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 主控制器：负责所有常规状态
        controllers.add(new AnimationController<>("main_controller", 5, this::mainController));
        controllers.add(new AnimationController<>( "attack_controller",animTest -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK_ANIM)
                .triggerableAnim("attack_1", ATTACK_ANIM_VARIANT_1)
                .triggerableAnim("attack_2", ATTACK_ANIM_VARIANT_2)
                .triggerableAnim("attack_3", ATTACK_ANIM_VARIANT_3)
                .triggerableAnim("idle_sometimes", IDLE_SOMETIME_ANIM)
                .triggerableAnim("block", BLOCK_ANIM));
    }
    public PlayState mainController(final AnimationTest<LittlePersonMilitiaEntity> event) {
        return event.isMoving() ? event.setAndContinue(WALK_ANIM) : event.setAndContinue(IDLE_ANIM);
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
