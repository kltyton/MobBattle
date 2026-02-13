package com.kltyton.mob_battle.entity.evoker;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;

public class SuperEvokerEntity extends EvokerEntity implements ModEvokerOwner {
    public SuperEvokerEntity(EntityType<? extends EvokerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        // 移除原有的目标，添加我们自定义的加强版
        // 优先级越高，数字越小
        this.goalSelector.add(4, new SuperSummonVexGoal());
        this.goalSelector.add(5, new SuperConjureFangsGoal());
    }

    public static DefaultAttributeContainer.Builder createSuperEvokerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 100.0) // 100点血量
                .add(EntityAttributes.MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.FOLLOW_RANGE, 32.0)
                .add(EntityAttributes.MAX_ABSORPTION, 20.0);
    }

    @Override
    public int getEvokerDamage() {
        return 25;
    }

    // --- 加强版：召唤恼鬼 ---
    class SuperSummonVexGoal extends SpellcastingIllagerEntity.CastSpellGoal {
        private final TargetPredicate closeVexPredicate = TargetPredicate.createNonAttackable()
                .setBaseMaxDistance(16.0)
                .ignoreVisibility()
                .ignoreDistanceScalingFactor();
        @Override
        public boolean canStart() {
            if (!super.canStart()) {
                return false;
            } else {
                int i = castToServerWorld(SuperEvokerEntity.this.getWorld())
                        .getTargets(VexEntity.class, this.closeVexPredicate, SuperEvokerEntity.this, SuperEvokerEntity.this.getBoundingBox().expand(16.0))
                        .size();
                return SuperEvokerEntity.this.random.nextInt(8) + 1 > i;
            }
        }
        @Override
        protected int getSpellTicks() { return 40; }
        @Override
        protected int startTimeDelay() { return 60; }

        @Override
        protected void castSpell() {
            ServerWorld serverWorld = (ServerWorld) SuperEvokerEntity.this.getWorld();
            for (int i = 0; i < 10; i++) {
                BlockPos blockPos = SuperEvokerEntity.this.getBlockPos().add(-3 + random.nextInt(7), 1, -3 + random.nextInt(7));
                VexEntity vex = EntityType.VEX.create(getWorld(), SpawnReason.MOB_SUMMONED);
                if (vex != null) {
                    vex.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
                    vex.initialize(serverWorld, getWorld().getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, null);
                    vex.setOwner(SuperEvokerEntity.this);
                    vex.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(50.0);
                    vex.setHealth(50.0F);
                    double baseDamage = vex.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
                    vex.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(baseDamage + 5.0);
                    vex.getAttributeInstance(EntityAttributes.SCALE).setBaseValue(1.2);
                    serverWorld.spawnEntityAndPassengers(vex);
                }
            }
        }
        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return SpellcastingIllagerEntity.Spell.SUMMON_VEX;
        }
    }

    // --- 加强版：尖牙法术 ---
    class SuperConjureFangsGoal extends SpellcastingIllagerEntity.CastSpellGoal {
        private final TargetPredicate convertibleSheepPredicate = TargetPredicate.createNonAttackable()
                .setBaseMaxDistance(16.0)
                .setPredicate((sheep, world) -> ((SheepEntity)sheep).getColor() == DyeColor.BLUE);
        @Override
        public boolean canStart() {
            if (SuperEvokerEntity.this.getTarget() != null) {
                return false;
            } else if (SuperEvokerEntity.this.isSpellcasting()) {
                return false;
            } else if (SuperEvokerEntity.this.age < this.startTime) {
                return false;
            } else {
                ServerWorld serverWorld = castToServerWorld(SuperEvokerEntity.this.getWorld());
                if (!serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                    return false;
                } else {
                    List<SheepEntity> list = serverWorld.getTargets(
                            SheepEntity.class, this.convertibleSheepPredicate, SuperEvokerEntity.this, SuperEvokerEntity.this.getBoundingBox().expand(16.0, 4.0, 16.0)
                    );
                    if (list.isEmpty()) {
                        return false;
                    } else {
                        SuperEvokerEntity.this.setWololoTarget((SheepEntity)list.get(SuperEvokerEntity.this.random.nextInt(list.size())));
                        return true;
                    }
                }
            }
        }
        @Override
        public boolean shouldContinue() {
            return SuperEvokerEntity.this.getWololoTarget() != null && this.spellCooldown > 0;
        }
        @Override
        public void stop() {
            super.stop();
            SuperEvokerEntity.this.setWololoTarget(null);
        }
        @Override
        protected int getInitialCooldown() {
            return 40;
        }
        @Override
        protected int getSpellTicks() { return 20; }
        @Override
        protected int startTimeDelay() { return 40; } // 极快施法
        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return SpellcastingIllagerEntity.Spell.WOLOLO;
        }

        @Override
        protected void castSpell() {
            LivingEntity target = SuperEvokerEntity.this.getTarget();
            if (target == null) return;

            double d = Math.min(target.getY(), SuperEvokerEntity.this.getY());
            double e = Math.max(target.getY(), SuperEvokerEntity.this.getY()) + 1.0;
            float angle = (float) MathHelper.atan2(target.getZ() - SuperEvokerEntity.this.getZ(), target.getX() - SuperEvokerEntity.this.getX());

            // 逻辑选择阵型
            int pattern = SuperEvokerEntity.this.random.nextInt(4);
            if (pattern == 0) {
                // 阵型1：十字星阵 (Cross Pattern)
                for (int i = -8; i <= 8; i++) {
                    createEnhancedFang(SuperEvokerEntity.this.getX() + i, SuperEvokerEntity.this.getZ(), d, e, 0, i + 5);
                    createEnhancedFang(SuperEvokerEntity.this.getX(), SuperEvokerEntity.this.getZ() + i, d, e, 1.57f, i + 5);
                }
            } else if (pattern == 1) {
                // 阵型2：环形扩散 (Circular Expand)
                for (int ring = 1; ring <= 3; ring++) {
                    for (int i = 0; i < 8 * ring; i++) {
                        float f = (float) (i * Math.PI * 2.0 / (8 * ring));
                        createEnhancedFang(target.getX() + MathHelper.cos(f) * ring, target.getZ() + MathHelper.sin(f) * ring, d, e, f, ring * 3);
                    }
                }
            } else {
                for (int i = 0; i < 48; i++) {
                    double distance = 1.25 * (i + 1);
                    int warmup = i;
                    createEnhancedFang(
                            SuperEvokerEntity.this.getX() + MathHelper.cos(angle) * distance,
                            SuperEvokerEntity.this.getZ() + MathHelper.sin(angle) * distance,
                            d, e, angle, warmup
                    );
                }
            }
        }

        private void createEnhancedFang(double x, double z, double minY, double maxY, float yaw, int warmup) {
            // 从上方开始向下检测
            BlockPos blockPos = BlockPos.ofFloored(x, maxY, z);
            boolean foundGround = false;
            double yOffset = 0.0;

            do {
                BlockPos belowPos = blockPos.down();
                BlockState belowState = SuperEvokerEntity.this.getWorld().getBlockState(belowPos);

                // 如果下方是实心方块的上表面
                if (belowState.isSideSolidFullSquare(SuperEvokerEntity.this.getWorld(), belowPos, Direction.UP)) {
                    if (!SuperEvokerEntity.this.getWorld().isAir(blockPos)) {
                        BlockState currentState = SuperEvokerEntity.this.getWorld().getBlockState(blockPos);
                        VoxelShape shape = currentState.getCollisionShape(SuperEvokerEntity.this.getWorld(), blockPos);
                        if (!shape.isEmpty()) {
                            yOffset = shape.getMax(Direction.Axis.Y);
                        }
                    }
                    foundGround = true;
                    break;
                }
                blockPos = blockPos.down();
                // 修正：循环应该持续直到到达 minY（最低检测高度）
            } while (blockPos.getY() >= MathHelper.floor(minY) - 1);

            if (foundGround) {
                // 注意：这里需要替换为你自定义的 EnhancedFangsEntity 以实现 +25 伤害
                // 如果你还没写自定义类，暂时用原版，但伤害会是默认的
                EvokerFangsEntity fangs = new EvokerFangsEntity(
                        SuperEvokerEntity.this.getWorld(),
                        x,
                        (double)blockPos.getY() + yOffset,
                        z,
                        yaw,
                        warmup,
                        SuperEvokerEntity.this
                );

                SuperEvokerEntity.this.getWorld().spawnEntity(fangs);
                SuperEvokerEntity.this.getWorld().emitGameEvent(
                        GameEvent.ENTITY_PLACE,
                        new Vec3d(x, (double)blockPos.getY() + yOffset, z),
                        GameEvent.Emitter.of(SuperEvokerEntity.this)
                );
            }
        }
    }
}
