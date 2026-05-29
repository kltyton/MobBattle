package com.kltyton.mob_battle.entity.customfireball;

import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class CustomFireballEntity extends LargeFireball {
    public float damage;
    public float power;
    public boolean isExplosive;
    public CustomFireballEntity(EntityType<? extends LargeFireball> entityType, Level world) {
        super(entityType, world);
    }
    public CustomFireballEntity(EntityType<? extends LargeFireball> entityType, Level world, LivingEntity owner, float power, boolean createFire, float damage) {
        super(entityType, world);

        this.snapTo(owner.getX(), owner.getY(), owner.getZ(), this.getYRot(), this.getXRot());
        this.reapplyPosition();

        this.setOwner(owner);
        this.setRot(owner.getYRot(), owner.getXRot());

        this.power = power;
        this.isExplosive = createFire;
        this.damage = damage;
        this.setNoGravity(true);
    }
    public CustomFireballEntity(Level world, LivingEntity owner, float power, boolean createFire, float damage) {
        super(world, owner, Vec3.ZERO, 0);
        this.power = power;
        this.isExplosive = createFire;
        this.damage = damage;
        this.setNoGravity(true);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.ENTITY) {
            // 处理命中实体的情况
            EntityHitResult entityHitResult = (EntityHitResult)hitResult;
            Entity entity = entityHitResult.getEntity();
            if (entity instanceof LivingEntity living && !EntityUtil.isValidSummonCombatTarget(this, this.getOwner(), living)) {
                return;
            }
            // 如果命中的是可重定向的投射物实体，则进行重定向
            if (entity.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE) && entity instanceof Projectile projectileEntity) {
                projectileEntity.deflect(ProjectileDeflection.AIM_DEFLECT, this.getOwner(), this.getOwner(), true);
            }

            // 调用实体命中处理方法
            this.onHitEntity(entityHitResult);
            // 发射投射物着陆的游戏事件
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, hitResult.getLocation(), GameEvent.Context.of(this, null));
        } else if (type == HitResult.Type.BLOCK) {
            // 处理命中方块的情况
            BlockHitResult blockHitResult = (BlockHitResult)hitResult;
            // 调用方块命中处理方法
            this.onHitBlock(blockHitResult);
            BlockPos blockPos = blockHitResult.getBlockPos();
            // 发射投射物着陆的游戏事件，包含方块状态信息
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Context.of(this, this.level().getBlockState(blockPos)));
        }

        // 在服务端创建爆炸效果并移除当前实体
        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), power, isExplosive, Level.ExplosionInteraction.NONE);
            this.discard(); // 移除实体
        }

    }

    @Override
    public boolean canHitEntity(Entity entity) {
        if (entity instanceof LivingEntity living && !EntityUtil.isValidSummonCombatTarget(this, this.getOwner(), living)) {
            return false;
        }
        return super.canHitEntity(entity);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        // 检查当前世界是否为服务器世界，如果是则执行伤害处理逻辑
        if (this.level() instanceof ServerLevel serverWorld) {
            // 获取被击中的实体
            Entity entity = entityHitResult.getEntity();
            if (entity instanceof LivingEntity living && !EntityUtil.isValidSummonCombatTarget(this, this.getOwner(), living)) {
                return;
            }
            // 获取攻击者实体（拥有者）
            Entity entity2 = this.getOwner();
            // 创建火球伤害源，指定攻击者和拥有者
            DamageSource damageSource = this.damageSources().fireball(this, entity2);
            // 对目标实体造成伤害
            entity.hurtServer(serverWorld, damageSource, damage);
            // 触发附魔相关的伤害后处理逻辑
            EnchantmentHelper.doPostAttackEffects(serverWorld, entity, damageSource);
        }

    }
}
