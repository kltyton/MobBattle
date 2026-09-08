package com.kltyton.mob_battle.entity.littleperson.skillentity.ironmanbullet;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/** 复用原版追踪弹的运动、碰撞和存档，只提供钢铁侠导弹的目标边界与伤害。 */
public class IronManBulletEntity extends ShulkerBullet {
    public IronManBulletEntity(EntityType<? extends IronManBulletEntity> entityType, Level world) {
        super(entityType, world);
    }

    public IronManBulletEntity(Level world, LivingEntity owner, Entity target, Direction.Axis axis) {
        this(ModEntities.IRON_MAN_BULLET_ENTITY, world);
        setOwner(owner);
        Vec3 origin = owner.getBoundingBox().getCenter();
        snapTo(origin.x, origin.y, origin.z, getYRot(), getXRot());
        this.finalTarget = EntityReference.of(target);
        this.currentMoveDirection = Direction.UP;
        this.selectNextMoveDirection(axis, target);
    }

    /** 切换锁定对象时沿用原版选向算法和既有 Target 存档字段。 */
    public void retarget(Entity target, @Nullable Direction.Axis axis) {
        this.finalTarget = EntityReference.of(target);
        this.selectNextMoveDirection(axis, target);
    }

    @Override
    public void tick() {
        Entity target = level().isClientSide() ? null : EntityReference.getEntity(this.finalTarget, level());
        if (target instanceof Player player && player.isCreative()) {
            // 创造模式期间按原版失去目标的规则下坠，保留引用供玩家离开创造模式后继续锁定。
            EntityReference<Entity> suspendedTarget = this.finalTarget;
            this.finalTarget = null;
            try {
                super.tick();
            } finally {
                this.finalTarget = suspendedTarget;
            }
        } else {
            super.tick();
        }
    }

    @Override
    public boolean canHitEntity(Entity entity) {
        if (entity instanceof LivingEntity living && !EntityQueries.isValidSummonCombatTarget(this, getOwner(), living)) {
            return false;
        }
        return super.canHitEntity(entity);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        // 本弹只应用下面的自定义伤害，不叠加 ShulkerBullet 的 4 点伤害和漂浮效果。
        Entity victim = result.getEntity();
        Entity owner = getOwner();
        if (victim instanceof LivingEntity living && !EntityQueries.isValidSummonCombatTarget(this, owner, living)) {
            return;
        }
        LivingEntity attacker = owner instanceof LivingEntity living ? living : null;
        DamageSource source = attacker == null ? damageSources().thrown(this, this) : damageSources().mobProjectile(this, attacker);
        if (victim.hurtOrSimulate(source, 90.0F)) {
            if (level() instanceof ServerLevel serverWorld) {
                EnchantmentHelper.doPostAttackEffects(serverWorld, victim, source);
            }
            if (victim instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 5 * 20, 7), owner != null ? owner : this);
            }
        }
    }
}
