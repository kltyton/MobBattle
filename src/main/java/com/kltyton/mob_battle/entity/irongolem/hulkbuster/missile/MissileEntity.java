package com.kltyton.mob_battle.entity.irongolem.hulkbuster.missile;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.customfireball.CustomFireballEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MissileEntity extends CustomFireballEntity {
    public int splits = 0;
    public float extraDamage;
    // --- 新增字段 ---
    private Vec3 lastSplitPos; // 记录上次分裂的位置
    private boolean canSplit = true; // 标记该实体是否能分裂，防止子弹无限分裂
    public MissileEntity(EntityType<? extends CustomFireballEntity> entityType, Level world) {
        super(entityType, world);
    }
    // --- 新增：Setter 用于关闭子弹的分裂功能 ---
    public void setCanSplit(boolean canSplit) {
        this.canSplit = canSplit;
    }
    public MissileEntity(EntityType<? extends CustomFireballEntity> entityType, Level world, LivingEntity owner, float power, boolean createFire, float damage, float extraDamage) {
        super(entityType, world, owner, power, createFire, damage);
        this.extraDamage = extraDamage;
        this.lastSplitPos = this.position(); // 初始化位置
    }
    @Override
    protected boolean shouldBurn() {
        return false;
    }
    @Nullable
    @Override
    protected ParticleOptions getTrailParticle() {
        return null;
    }
    @Override
    protected void onHit(HitResult hitResult) {
        if (!this.level().isClientSide) {
            this.explodeAndApplyEffects();
            this.discard();
        }
    }
    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
    }
    // 在 MissileEntity 类中加入
    @Override
    public void tick() {
        super.tick();
        // 仅在服务端处理逻辑
        if (!this.level().isClientSide) {
            if (this.tickCount >= 100 || splits >= 5) this.discard();
            // 2. 处理分裂逻辑
            if (this.canSplit) {
                handleSplitting();
            }
        }
    }
    @Override
    public void updateRotation() {
        Vec3 vec3d = this.getDeltaMovement();
        if (vec3d.lengthSqr() > 0.01) {
            this.setYRot((float)(Mth.atan2(vec3d.x, vec3d.z) * (180f / Math.PI)));
            this.setXRot((float)(Mth.atan2(vec3d.y, vec3d.horizontalDistance()) * (180f / Math.PI)));
        }
    }

    private void handleSplitting() {
        if (lastSplitPos == null) {
            lastSplitPos = this.position();
            return;
        }

        // 计算当前位置与上次分裂位置的距离
        double distance = this.position().distanceTo(lastSplitPos);

        if (distance >= 3.0) {
            spawnSubMissile();
            // 更新最后一次分裂的位置（重要：防止在一个 tick 内多次分裂）
            this.lastSplitPos = this.position();
        }
    }
    private void spawnSubMissile() {
        if (!this.isAlive() || !(this.getOwner() instanceof LivingEntity)) {
            return;
        }
        // 创建一个新的导弹实例
        // 注意：这里假设你有对应的 EntityType 引用。如果没有，可以使用 this.getType()
        MissileEntity subMissile = new MissileEntity(ModEntities.MISSILE, this.level(), (LivingEntity) this.getOwner(), 5.0f, false, 0, 200);

        // 复制基本属性
        subMissile.setOwner(this.getOwner());
        subMissile.setPos(this.getX(), this.getY() - 0.5, this.getZ()); // 在下方一点生成

        subMissile.extraDamage = this.extraDamage;
        // 关键：防止子弹再次分裂导致服务器崩溃（无限递归）
        subMissile.setCanSplit(false);
        // 设置向下飞行的速度 (0, -1, 0)
        subMissile.setDeltaMovement(0, -0.8, 0);
        subMissile.splits++;
        // 生成实体
        this.level().addFreshEntity(subMissile);
    }
    private void explodeAndApplyEffects() {
        Level world = this.level();
        world.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (world instanceof ServerLevel serverWorld) {
            double radius = 10.0;
            AABB box = this.getBoundingBox().inflate(radius);
            List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, box);
            Entity owner = this.getOwner();
            for (LivingEntity target : targets) {
                if (!EntityUtil.isValidSummonCombatTarget(this, owner, target)) {
                    continue;
                }
                DamageSource physicalSource = world.damageSources().fireball(this, owner);
                target.hurtServer(serverWorld, physicalSource, extraDamage);
            }
            serverWorld.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(), this.getY(), this.getZ(), 3, 1.0, 1.0, 1.0, 0.1);
        }
    }
}

