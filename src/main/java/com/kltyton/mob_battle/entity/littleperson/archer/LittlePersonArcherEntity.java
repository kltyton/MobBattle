package com.kltyton.mob_battle.entity.littleperson.archer;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntity;
import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class LittlePersonArcherEntity extends Monster implements LittlePersonEntity, RangedAttackMob {

    public LittlePersonArcherEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 20, 6.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0)); // 添加远距离游荡目标
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F)); // 添加看向玩家的目标
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this)); // 添加环顾四周的目标
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true)); // 添加攻击傀儡目标
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, WarriorVillager.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true)); // 添加主动攻击玩家目标
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (entity, world) -> entity instanceof Enemy && !(entity instanceof LittlePersonEntity)));
    }
    public static AttributeSupplier.Builder createLittlePersonArcherAttributes() {
        return LittlePersonEntity.createLittlePersonAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.ATTACK_DAMAGE, 15.0);
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
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
/*        // 检查伤害是否来自实体直接攻击
        if (source.getSource() instanceof Entity &&
                !source.isIn(DamageTypeTags.IS_FALL) &&
                !source.isIn(DamageTypeTags.IS_FIRE) &&
                !source.isIn(DamageTypeTags.IS_EXPLOSION) &&
                !source.isIn(DamageTypeTags.IS_DROWNING) &&
                !source.isIn(DamageTypeTags.IS_FREEZING) &&
                !source.isIn(DamageTypeTags.IS_LIGHTNING) &&
                !source.isIn(DamageTypeTags.BURN_FROM_STEPPING) &&
                !source.isIn(DamageTypeTags.WITCH_RESISTANT_TO) &&
                amount <= 35.0F) {

            // 20%概率免疫伤害
            if (this.random.nextInt(100) < 20) {
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK.value(), 1.0F, 1.0F);
                this.triggerAnim("attack_controller", "block");
                return false;
            }
        }

        // 如果不满足条件或者未触发免疫，则正常处理伤害*/
        return super.hurtServer(world, source, amount);
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation BLOCK_ANIM = RawAnimation.begin().thenPlay("block");
    protected static final RawAnimation SKILL_ANIM = RawAnimation.begin().thenPlayAndHold("skill");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 主控制器：负责所有常规状态
        controllers.add(new AnimationController<>("main_controller", 5, this::mainController));
        controllers.add(new AnimationController<>( "attack_controller",animTest -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK_ANIM)
                .triggerableAnim("skill", SKILL_ANIM)
                .triggerableAnim("block", BLOCK_ANIM));
    }
    public PlayState mainController(final AnimationTest<LittlePersonArcherEntity> event) {
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
    @Override
    public boolean canSkill() {
        return ModSkillEntityType.canSkill(this);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        if (!canSkill()) return;
        double targetX = target.getX() - this.getX();
        double targetY = target.getEyeY() - this.getEyeY(); // 更精确的高度计算
        double targetZ = target.getZ() - this.getZ();
        double distance = Math.sqrt(targetX * targetX + targetZ * targetZ);

        // 预测目标移动（提高准确性）
        targetY += target.getDeltaMovement().y() * distance * 0.25; // 重力补偿

        Level world = this.level();

        if (world instanceof ServerLevel serverWorld) {
            this.triggerAnim("attack_controller", "attack");
            // 创建箭实体
            LittleArrowEntity arrowEntity = new LittleArrowEntity(world, this, new ItemStack(Items.ARROW), this.getMainHandItem().getItem() == Items.BOW ? this.getMainHandItem() : null);
            // 设置箭的伤害
            arrowEntity.setBaseDamage(this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            arrowEntity.setOwner(this);

            // 减小散布参数以提高精度（从0.1F改为0.01F）
            arrowEntity.shoot(targetX, targetY, targetZ, 1.6F, 0.01F);
            arrowEntity.setTrueDamage(true, false);
            // 发射箭
            serverWorld.addFreshEntity(arrowEntity);
        }

        // 播放攻击音效
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

}
