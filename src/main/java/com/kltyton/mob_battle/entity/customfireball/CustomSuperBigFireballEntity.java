package com.kltyton.mob_battle.entity.customfireball;

import com.kltyton.mob_battle.entity.ModEntities;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public class CustomSuperBigFireballEntity extends CustomFireballEntity {
    public final Vec3d speedV3d;
    private Vec3d ownerEntityPos;
    private float lastOwnerHealth;
    private float frozenYaw;
    private float frozenPitch;
    public boolean SbBfbp;
    public boolean fuckOwner = true;
    public CustomSuperBigFireballEntity(EntityType<? extends CustomSuperBigFireballEntity> entityType, World world, LivingEntity owner, float power, boolean createFire, float damage, Vec3d speedV3d, boolean SbBfbp) {
        super(entityType, world, owner, power, createFire, damage);
        this.speedV3d = speedV3d;
        this.lastOwnerHealth = owner.getHealth();
        this.frozenYaw = owner.getYaw();
        this.frozenPitch = owner.getPitch();
        this.ownerEntityPos = owner.getPos();
        this.SbBfbp = SbBfbp;
    }
    public CustomSuperBigFireballEntity(EntityType<? extends CustomSuperBigFireballEntity> entityType, World world) {
        super(entityType, world);
        this.speedV3d = Vec3d.ZERO;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        // 检查当前世界是否为服务器世界，如果是则执行伤害处理逻辑
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            // 获取被击中的实体
            Entity entity = entityHitResult.getEntity();
            // 获取攻击者实体（拥有者）
            //Entity entity2 = this.getOwner();
            // 创建火球伤害源，指定攻击者和拥有者
            DamageSource damageSource = this.getDamageSources().magic();
            // 对目标实体造成伤害
            entity.damage(serverWorld, damageSource, damage);
            // 触发附魔相关的伤害后处理逻辑
            EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource);
        }

    }
    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            return;
        }
        // ---- 1. 动态缩放 ----
        int growTime = 40; // 40 tick 内完成放大
        Entity falseOwner = this.getOwner();
        // ---- 2. 冻结主人 ----
        if (this.age < growTime) {
            if (falseOwner instanceof LivingEntity trueOwner && this.fuckOwner) {
                trueOwner.setVelocity(Vec3d.ZERO); // 禁止移动
                trueOwner.setYaw(frozenYaw);       // 锁定朝向
                trueOwner.setPitch(frozenPitch);
                if (ownerEntityPos != null) trueOwner.setPos(ownerEntityPos.x, ownerEntityPos.y, ownerEntityPos.z);
                if (trueOwner.isPlayer())
                    trueOwner.teleportTo(
                            new TeleportTarget((ServerWorld) this.getWorld(), ownerEntityPos, Vec3d.ZERO, trueOwner.getYaw(), trueOwner.getPitch(), TeleportTarget.NO_OP));
                trueOwner.velocityModified = true; // 强制客户端同步
            }
        } else if (this.age == growTime) {
            this.setVelocity(speedV3d);
        }

        // ---- 3. 受伤销毁 ----
        if (falseOwner instanceof LivingEntity trueOwner && this.fuckOwner) {
            if (trueOwner.getHealth() < lastOwnerHealth) {
                this.discard(); // 主人受伤 -> 火球消失
                return;
            }
            lastOwnerHealth = trueOwner.getHealth();
        }
    }
    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient && this.SbBfbp) {
            Vec3d pos = hitResult.getPos();
            Entity ownerEntity = this.getOwner();
            if (!(ownerEntity instanceof LivingEntity owner)) {
                return;
            }

            for (int i = 0; i < 10; i++) {
                Vec3d speedVec = new Vec3d(
                        this.random.nextGaussian(),
                        this.random.nextGaussian(),
                        this.random.nextGaussian()
                ).normalize().multiply(3.0); // 3.0 为散射速度（越大越快）

                if (speedVec.lengthSquared() < 0.01) {
                    speedVec = new Vec3d(0, 1, 0).multiply(3.0);
                }

                CustomSuperBigFireballEntity fireball = new CustomSuperBigFireballEntity(
                        ModEntities.BIG_CUSTOM_FIREBALL,
                        this.getWorld(),
                        owner,
                        5.5F,
                        true,
                        70.0F,
                        speedVec,
                        false
                );
                fireball.fuckOwner = false;
                Vec3d offsetPos = pos.add(speedVec.normalize().multiply(0.1));
                fireball.setPosition(offsetPos);
                this.getWorld().spawnEntity(fireball);
            }
        }
    }
}
