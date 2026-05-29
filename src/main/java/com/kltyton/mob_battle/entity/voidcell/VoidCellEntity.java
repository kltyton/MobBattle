package com.kltyton.mob_battle.entity.voidcell;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class VoidCellEntity extends PathfinderMob implements GeoEntity {
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

    public VoidCellEntity(EntityType<? extends VoidCellEntity> entityType, Level world) {
        super(entityType, world);
        this.setNoGravity(true);
        // 使用类似恼鬼的移动控制器，实现 3D 飞行
        this.moveControl = new VoidCellMoveControl(this);
        this.xpReward = 5;
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(PathType.DANGER_OTHER, -10.0F);
        this.setPathfindingMalus(PathType.WALKABLE, -10.0F); // 讨厌可站立方块
    }

    // --- 核心逻辑修改 ---

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.setNoGravity(true);
            if (this.getY() < -300) {
                this.kill((ServerLevel) this.level());
                return;
            }

            if (this.getY() <= -220) {
                this.getMoveControl().setWantedPosition(this.getX(), this.getY() + 10.0, this.getZ(), 0.5);
            }
        }

    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL) || source.is(DamageTypes.FELL_OUT_OF_WORLD)) return false;
        return super.hurtServer(world, source, amount);
    }
    @Override
    protected void registerGoals() {
        // 移除攻击性 Goal，只保留被动和游荡行为
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // 自定义的空中随机游荡目标
        this.goalSelector.addGoal(4, new RandomFlyGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createVoidCellAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0)
                .add(Attributes.FLYING_SPEED, 0.6)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }
}
