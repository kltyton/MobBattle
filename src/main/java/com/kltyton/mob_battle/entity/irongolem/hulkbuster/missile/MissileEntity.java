package com.kltyton.mob_battle.entity.irongolem.hulkbuster.missile;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.customfireball.CustomFireballEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MissileEntity extends CustomFireballEntity {
    public int splits = 0;
    public float extraDamage;
    // --- 新增字段 ---
    private Vec3d lastSplitPos; // 记录上次分裂的位置
    private boolean canSplit = true; // 标记该实体是否能分裂，防止子弹无限分裂
    public MissileEntity(EntityType<? extends CustomFireballEntity> entityType, World world) {
        super(entityType, world);
    }
    // --- 新增：Setter 用于关闭子弹的分裂功能 ---
    public void setCanSplit(boolean canSplit) {
        this.canSplit = canSplit;
    }
    public MissileEntity(EntityType<? extends CustomFireballEntity> entityType, World world, LivingEntity owner, float power, boolean createFire, float damage, float extraDamage) {
        super(entityType, world, owner, power, createFire, damage);
        this.extraDamage = extraDamage;
        this.lastSplitPos = this.getPos(); // 初始化位置
    }
    @Override
    protected boolean isBurning() {
        return false;
    }
    @Nullable
    @Override
    protected ParticleEffect getParticleType() {
        return null;
    }
    @Override
    protected void onCollision(HitResult hitResult) {
        if (!this.getWorld().isClient) {
            this.explodeAndApplyEffects();
            this.discard();
        }
    }
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
    }
    // 在 MissileEntity 类中加入
    @Override
    public void tick() {
        super.tick();
        // 仅在服务端处理逻辑
        if (!this.getWorld().isClient) {
            if (this.age >= 100 || splits >= 5) this.discard();
            // 2. 处理分裂逻辑
            if (this.canSplit) {
                handleSplitting();
            }
        }
    }
    @Override
    public void updateRotation() {
        Vec3d vec3d = this.getVelocity();
        if (vec3d.lengthSquared() > 0.01) {
            this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * (180f / Math.PI)));
            this.setPitch((float)(MathHelper.atan2(vec3d.y, vec3d.horizontalLength()) * (180f / Math.PI)));
        }
    }

    private void handleSplitting() {
        if (lastSplitPos == null) {
            lastSplitPos = this.getPos();
            return;
        }

        // 计算当前位置与上次分裂位置的距离
        double distance = this.getPos().distanceTo(lastSplitPos);

        if (distance >= 3.0) {
            spawnSubMissile();
            // 更新最后一次分裂的位置（重要：防止在一个 tick 内多次分裂）
            this.lastSplitPos = this.getPos();
        }
    }
    private void spawnSubMissile() {
        if (!this.isAlive() || !(this.getOwner() instanceof LivingEntity)) {
            return;
        }
        // 创建一个新的导弹实例
        // 注意：这里假设你有对应的 EntityType 引用。如果没有，可以使用 this.getType()
        MissileEntity subMissile = new MissileEntity(ModEntities.MISSILE, this.getWorld(), (LivingEntity) this.getOwner(), 5.0f, false, 0, 200);

        // 复制基本属性
        subMissile.setOwner(this.getOwner());
        subMissile.setPosition(this.getX(), this.getY() - 0.5, this.getZ()); // 在下方一点生成

        subMissile.extraDamage = this.extraDamage;
        // 关键：防止子弹再次分裂导致服务器崩溃（无限递归）
        subMissile.setCanSplit(false);
        // 设置向下飞行的速度 (0, -1, 0)
        subMissile.setVelocity(0, -0.8, 0);
        subMissile.splits++;
        // 生成实体
        this.getWorld().spawnEntity(subMissile);
    }
    private void explodeAndApplyEffects() {
        World world = this.getWorld();
        world.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        if (world instanceof ServerWorld serverWorld) {
            double radius = 10.0;
            Box box = this.getBoundingBox().expand(radius);
            List<LivingEntity> targets = world.getNonSpectatingEntities(LivingEntity.class, box);
            Entity owner = this.getOwner();
            for (LivingEntity target : targets) {
                if (target.isTeammate(owner) || target == owner) {
                    continue;
                }
                DamageSource physicalSource = world.getDamageSources().fireball(this, owner);
                target.damage(serverWorld, physicalSource, extraDamage);
            }
            serverWorld.spawnParticles(ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(), this.getY(), this.getZ(), 3, 1.0, 1.0, 1.0, 0.1);
        }
    }
}

