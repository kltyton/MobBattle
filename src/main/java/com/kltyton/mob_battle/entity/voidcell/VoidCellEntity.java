package com.kltyton.mob_battle.entity.voidcell;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class VoidCellEntity extends PathAwareEntity implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>("main_controller", 5 ,state -> state.setAndContinue(IDEA_ANIM)));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public VoidCellEntity(EntityType<? extends VoidCellEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
        // 使用类似恼鬼的移动控制器，实现 3D 飞行
        this.moveControl = new VoidCellMoveControl(this);
        this.experiencePoints = 5;
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 16.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_OTHER, -10.0F);
        this.setPathfindingPenalty(PathNodeType.WALKABLE, -10.0F); // 讨厌可站立方块
    }

    // --- 核心逻辑修改 ---

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            this.setNoGravity(true);
            if (this.getY() < -300) {
                this.kill((ServerWorld) this.getWorld());
                return;
            }

            if (this.getY() <= -220) {
                this.getMoveControl().moveTo(this.getX(), this.getY() + 10.0, this.getZ(), 0.5);
            }
        }

    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (source.isOf(DamageTypes.FALL) || source.isOf(DamageTypes.OUT_OF_WORLD)) return false;
        return super.damage(world, source, amount);
    }
    @Override
    protected void initGoals() {
        // 移除攻击性 Goal，只保留被动和游荡行为
        this.goalSelector.add(0, new SwimGoal(this));
        // 自定义的空中随机游荡目标
        this.goalSelector.add(4, new RandomFlyGoal(this));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(9, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createVoidCellAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 5.0)
                .add(EntityAttributes.FLYING_SPEED, 0.6)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3);
    }
}
