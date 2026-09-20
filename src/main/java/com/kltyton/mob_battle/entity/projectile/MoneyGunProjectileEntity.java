package com.kltyton.mob_battle.entity.projectile;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * 撒币枪弹体。客户端显示实际消耗的货币物品，服务端持有固定伤害与散弹命中规则。
 */
public final class MoneyGunProjectileEntity extends ThrowableItemProjectile {
    private float damage;
    private boolean bypassInvulnerability;
    private float knockbackStrength;

    public MoneyGunProjectileEntity(EntityType<? extends MoneyGunProjectileEntity> type, Level level) {
        super(type, level);
    }

    public MoneyGunProjectileEntity(
            Level level,
            LivingEntity owner,
            ItemStack visualStack,
            float damage,
            boolean bypassInvulnerability,
            float knockbackStrength
    ) {
        super(ModEntities.MONEY_GUN_PROJECTILE, owner, level, visualStack);
        this.damage = damage;
        this.bypassInvulnerability = bypassInvulnerability;
        this.knockbackStrength = knockbackStrength;
        this.setNoGravity(true);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.EMERALD;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (!(this.level() instanceof ServerLevel level)
                || !(hitResult.getEntity() instanceof LivingEntity target)
                || !EntityQueries.isValidSummonCombatTarget(this, this.getOwner(), target)) {
            return;
        }
        if (bypassInvulnerability) {
            target.invulnerableTime = 0;
        }
        boolean damaged = target.hurtServer(level, this.damageSources().thrown(this, this.getOwner()), damage);
        if (bypassInvulnerability) {
            target.invulnerableTime = 0;
        }
        if (damaged && knockbackStrength > 0.0F) {
            target.knockback(knockbackStrength, this.getX() - target.getX(), this.getZ() - target.getZ());
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == 3) {
            ParticleOptions particle = new ItemParticleOption(ParticleTypes.ITEM, this.getItem().getItem());
            for (int i = 0; i < 8; i++) {
                this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putFloat("MoneyGunDamage", damage);
        output.putBoolean("MoneyGunBypassInvulnerability", bypassInvulnerability);
        output.putFloat("MoneyGunKnockback", knockbackStrength);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        damage = input.getFloatOr("MoneyGunDamage", 0.0F);
        bypassInvulnerability = input.getBooleanOr("MoneyGunBypassInvulnerability", false);
        knockbackStrength = input.getFloatOr("MoneyGunKnockback", 0.0F);
    }
}
