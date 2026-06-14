package com.kltyton.mob_battle.entity.evoker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class SuperEvokerEntity extends Evoker implements ModEvokerOwner {
    private Sheep wololoTarget;

    public SuperEvokerEntity(EntityType<? extends Evoker> entityType, Level world) {
        super(entityType, world);
    }

    public Sheep getWololoTarget() {
        return this.wololoTarget;
    }

    public void setWololoTarget(Sheep wololoTarget) {
        this.wololoTarget = wololoTarget;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.removeAllGoals(goal -> {
            String className = goal.getClass().getName();
            return className.endsWith("Evoker$EvokerSummonSpellGoal")
                    || className.endsWith("Evoker$EvokerAttackSpellGoal");
        });
        this.goalSelector.addGoal(4, new SuperSummonVexGoal());
        this.goalSelector.addGoal(5, new SuperConjureFangsGoal());
    }

    public static AttributeSupplier.Builder createSuperEvokerAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0) // 100点血量
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.MAX_ABSORPTION, 20.0);
    }

    @Override
    public int getEvokerDamage() {
        return 25;
    }

    // --- 加强版：召唤恼鬼 ---
    class SuperSummonVexGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
        private final TargetingConditions closeVexPredicate = TargetingConditions.forNonCombat()
                .range(16.0)
                .ignoreLineOfSight()
                .ignoreInvisibilityTesting();
        @Override
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else {
                int i = getServerLevel(SuperEvokerEntity.this.level())
                        .getNearbyEntities(Vex.class, this.closeVexPredicate, SuperEvokerEntity.this, SuperEvokerEntity.this.getBoundingBox().inflate(16.0))
                        .size();
                return SuperEvokerEntity.this.random.nextInt(8) + 1 > i;
            }
        }
        @Override
        protected int getCastingTime() { return 40; }
        @Override
        protected int getCastingInterval() { return 60; }

        @Override
        protected void performSpellCasting() {
            ServerLevel serverWorld = (ServerLevel) SuperEvokerEntity.this.level();
            for (int i = 0; i < 10; i++) {
                BlockPos blockPos = SuperEvokerEntity.this.blockPosition().offset(-3 + random.nextInt(7), 1, -3 + random.nextInt(7));
                Vex vex = EntityType.VEX.create(level(), EntitySpawnReason.MOB_SUMMONED);
                if (vex != null) {
                    vex.snapTo(blockPos, 0.0F, 0.0F);
                    vex.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(blockPos), EntitySpawnReason.MOB_SUMMONED, null);
                    vex.setOwner(SuperEvokerEntity.this);
                    vex.getAttribute(Attributes.MAX_HEALTH).setBaseValue(50.0);
                    vex.setHealth(50.0F);
                    double baseDamage = vex.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    vex.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(baseDamage + 5.0);
                    vex.getAttribute(Attributes.SCALE).setBaseValue(1.2);
                    serverWorld.addFreshEntityWithPassengers(vex);
                }
            }
        }
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return SpellcasterIllager.IllagerSpell.SUMMON_VEX;
        }
    }

    // --- 加强版：尖牙法术 ---
    class SuperConjureFangsGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
        @Override
        public boolean canUse() {
            return SuperEvokerEntity.this.getTarget() != null && super.canUse();
        }
        @Override
        protected int getCastWarmupTime() {
            return 20;
        }
        @Override
        protected int getCastingTime() { return 40; }
        @Override
        protected int getCastingInterval() { return 100; }
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected SpellcasterIllager.IllagerSpell getSpell() {
            return SpellcasterIllager.IllagerSpell.FANGS;
        }

        @Override
        protected void performSpellCasting() {
            LivingEntity target = SuperEvokerEntity.this.getTarget();
            if (target == null) return;

            double d = Math.min(target.getY(), SuperEvokerEntity.this.getY());
            double e = Math.max(target.getY(), SuperEvokerEntity.this.getY()) + 1.0;
            float angle = (float) Mth.atan2(target.getZ() - SuperEvokerEntity.this.getZ(), target.getX() - SuperEvokerEntity.this.getX());

            createDoubleRing(d, e, angle);
            createTripleLine(d, e, angle);
        }

        private void createDoubleRing(double minY, double maxY, float angleTowardsTarget) {
            for (int i = 0; i < 5; i++) {
                float angle = angleTowardsTarget + i * Mth.PI * 0.4F;
                createEnhancedFang(
                        SuperEvokerEntity.this.getX() + Mth.cos(angle) * 1.5D,
                        SuperEvokerEntity.this.getZ() + Mth.sin(angle) * 1.5D,
                        minY, maxY, angle, 0);
            }
            for (int i = 0; i < 8; i++) {
                float angle = angleTowardsTarget + i * Mth.PI * 2.0F / 8.0F + 1.2566371F;
                createEnhancedFang(
                        SuperEvokerEntity.this.getX() + Mth.cos(angle) * 2.5D,
                        SuperEvokerEntity.this.getZ() + Mth.sin(angle) * 2.5D,
                        minY, maxY, angle, 3);
            }
        }

        private void createTripleLine(double minY, double maxY, float angle) {
            double forwardX = Mth.cos(angle);
            double forwardZ = Mth.sin(angle);
            double sideX = -forwardZ;
            double sideZ = forwardX;

            for (double sideOffset : new double[]{-1.5D, 0.0D, 1.5D}) {
                for (int i = 0; i < 20; i++) {
                    double reach = 1.25D * (i + 1);
                    createEnhancedFang(
                            SuperEvokerEntity.this.getX() + forwardX * reach + sideX * sideOffset,
                            SuperEvokerEntity.this.getZ() + forwardZ * reach + sideZ * sideOffset,
                            minY, maxY, angle, i);
                }
            }
        }

        private void createEnhancedFang(double x, double z, double minY, double maxY, float yaw, int warmup) {
            // 从上方开始向下检测
            BlockPos blockPos = BlockPos.containing(x, maxY, z);
            boolean foundGround = false;
            double yOffset = 0.0;

            do {
                BlockPos belowPos = blockPos.below();
                BlockState belowState = SuperEvokerEntity.this.level().getBlockState(belowPos);

                // 如果下方是实心方块的上表面
                if (belowState.isFaceSturdy(SuperEvokerEntity.this.level(), belowPos, Direction.UP)) {
                    if (!SuperEvokerEntity.this.level().isEmptyBlock(blockPos)) {
                        BlockState currentState = SuperEvokerEntity.this.level().getBlockState(blockPos);
                        VoxelShape shape = currentState.getCollisionShape(SuperEvokerEntity.this.level(), blockPos);
                        if (!shape.isEmpty()) {
                            yOffset = shape.max(Direction.Axis.Y);
                        }
                    }
                    foundGround = true;
                    break;
                }
                blockPos = blockPos.below();
                // 修正：循环应该持续直到到达 minY（最低检测高度）
            } while (blockPos.getY() >= Mth.floor(minY) - 1);

            if (foundGround) {
                // 注意：这里需要替换为你自定义的 EnhancedFangsEntity 以实现 +25 伤害
                // 如果你还没写自定义类，暂时用原版，但伤害会是默认的
                EvokerFangs fangs = new EvokerFangs(
                        SuperEvokerEntity.this.level(),
                        x,
                        (double)blockPos.getY() + yOffset,
                        z,
                        yaw,
                        warmup,
                        SuperEvokerEntity.this
                );

                SuperEvokerEntity.this.level().addFreshEntity(fangs);
                SuperEvokerEntity.this.level().gameEvent(
                        GameEvent.ENTITY_PLACE,
                        new Vec3(x, (double)blockPos.getY() + yOffset, z),
                        GameEvent.Context.of(SuperEvokerEntity.this)
                );
            }
        }
    }
}
