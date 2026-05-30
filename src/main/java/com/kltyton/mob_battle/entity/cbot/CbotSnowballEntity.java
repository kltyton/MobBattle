package com.kltyton.mob_battle.entity.cbot;

import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CbotSnowballEntity extends Projectile {
    private float physicalDamage;
    private float magicDamage;
    private final ItemStack displayStack = new ItemStack(Items.SNOWBALL);

    public CbotSnowballEntity(EntityType<? extends CbotSnowballEntity> entityType, Level world) {
        super(entityType, world);
    }

    public void configure(LivingEntity owner, Vec3 position, Vec3 velocity, float physicalDamage, float magicDamage) {
        this.setOwner(owner);
        this.setPos(position);
        this.setDeltaMovement(velocity);
        this.physicalDamage = physicalDamage;
        this.magicDamage = magicDamage;
        this.hurtMarked = true;
    }

    public ItemStack getDisplayStack() {
        return this.displayStack;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
    }

    @Override
    public void tick() {
        super.tick();
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
        if (this.level().isClientSide()) {
            return;
        }
        if (this.tickCount > 80 || this.level().getBlockState(this.blockPosition()).isRedstoneConductor(this.level(), this.blockPosition())) {
            this.discard();
            return;
        }
        Entity owner = this.getOwner();
        AABB box = this.getBoundingBox().inflate(0.35D);
        for (LivingEntity target : ((ServerLevel) this.level()).getEntitiesOfClass(LivingEntity.class, box,
                living -> EntityUtil.isValidSummonCombatTarget(this, owner, living))) {
            if (this.physicalDamage > 0.0F) {
                target.invulnerableTime = 0;
                if (owner instanceof LivingEntity livingOwner) {
                    target.hurtServer((ServerLevel) this.level(), this.damageSources().mobProjectile(this, livingOwner), this.physicalDamage);
                } else {
                    target.hurtServer((ServerLevel) this.level(), this.damageSources().thrown(this, this), this.physicalDamage);
                }
            }
            if (this.magicDamage > 0.0F) {
                target.invulnerableTime = 0;
                target.hurtServer((ServerLevel) this.level(), this.damageSources().indirectMagic(this, owner == null ? this : owner), this.magicDamage);
            }
            this.discard();
            return;
        }
    }
}
