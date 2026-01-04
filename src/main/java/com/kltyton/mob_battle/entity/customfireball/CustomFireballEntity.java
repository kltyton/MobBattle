package com.kltyton.mob_battle.entity.customfireball;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CustomFireballEntity extends FireballEntity {
    public float damage;
    public float power;
    public boolean isExplosive;
    public CustomFireballEntity(EntityType<? extends CustomFireballEntity> entityType, World world) {
        super(entityType, world);
    }
    public CustomFireballEntity(EntityType<? extends CustomFireballEntity> entityType, World world, LivingEntity owner, float power, boolean createFire, float damage) {
        super(entityType, world);

        this.refreshPositionAndAngles(owner.getX(), owner.getY(), owner.getZ(), this.getYaw(), this.getPitch());
        this.refreshPosition();

        this.setOwner(owner);
        this.setRotation(owner.getYaw(), owner.getPitch());

        this.explosionPower = 0;

        this.power = power;
        this.isExplosive = createFire;
        this.damage = damage;
        this.setNoGravity(true);
    }
    public CustomFireballEntity(World world, LivingEntity owner, float power, boolean createFire, float damage) {
        super(world, owner, Vec3d.ZERO, 0);
        this.power = power;
        this.isExplosive = createFire;
        this.damage = damage;
        this.setNoGravity(true);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.ENTITY) {
            // 处理命中实体的情况
            EntityHitResult entityHitResult = (EntityHitResult)hitResult;
            Entity entity = entityHitResult.getEntity();
            // 如果命中的是可重定向的投射物实体，则进行重定向
            if (entity.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE) && entity instanceof ProjectileEntity projectileEntity) {
                projectileEntity.deflect(ProjectileDeflection.REDIRECTED, this.getOwner(), this.getOwner(), true);
            }

            // 调用实体命中处理方法
            this.onEntityHit(entityHitResult);
            // 发射投射物着陆的游戏事件
            this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, hitResult.getPos(), GameEvent.Emitter.of(this, null));
        } else if (type == HitResult.Type.BLOCK) {
            // 处理命中方块的情况
            BlockHitResult blockHitResult = (BlockHitResult)hitResult;
            // 调用方块命中处理方法
            this.onBlockHit(blockHitResult);
            BlockPos blockPos = blockHitResult.getBlockPos();
            // 发射投射物着陆的游戏事件，包含方块状态信息
            this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Emitter.of(this, this.getWorld().getBlockState(blockPos)));
        }

        // 在服务端创建爆炸效果并移除当前实体
        if (!this.getWorld().isClient) {
            this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), power, isExplosive, World.ExplosionSourceType.NONE);
            this.discard(); // 移除实体
        }

    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        // 检查当前世界是否为服务器世界，如果是则执行伤害处理逻辑
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            // 获取被击中的实体
            Entity entity = entityHitResult.getEntity();
            // 获取攻击者实体（拥有者）
            Entity entity2 = this.getOwner();
            // 创建火球伤害源，指定攻击者和拥有者
            DamageSource damageSource = this.getDamageSources().fireball(this, entity2);
            // 对目标实体造成伤害
            entity.damage(serverWorld, damageSource, damage);
            // 触发附魔相关的伤害后处理逻辑
            EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource);
        }

    }
}
