package com.kltyton.mob_battle.entity.skull.mage;

import com.kltyton.mob_battle.entity.OwnedSummon;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SummonedSkeletonEntity extends Skeleton implements OwnedSummon {
    private static final double FOLLOW_OWNER_DISTANCE_SQ = 8.0D * 8.0D;
    private static final double FOLLOW_OWNER_SPEED = 1.15D;
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> OWNER =
            SynchedEntityData.defineId(SummonedSkeletonEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);

    public SummonedSkeletonEntity(EntityType<? extends Skeleton> entityType, Level world) {
        super(entityType, world);
    }

    public static AttributeSupplier.Builder createSummonedSkeletonAttributes() {
        return Skeleton.createAttributes()
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER, Optional.empty());
    }

    public void setSummonOwner(@Nullable LivingEntity owner) {
        this.entityData.set(OWNER, Optional.ofNullable(owner).map(EntityReference::of));
        if (owner != null) {
            EntityUtil.joinSameTeam(this, owner);
        }
    }

    @Nullable
    @Override
    public Entity getSummonOwner() {
        return this.entityData.get(OWNER)
                .map(ref -> ref.getEntity(this.level(), LivingEntity.class))
                .orElse(null);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return EntityUtil.isValidSummonCombatTarget(this, getSummonOwner(), target) && super.canAttack(target);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target != null && !EntityUtil.isValidSummonCombatTarget(this, getSummonOwner(), target) ? null : target);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            updateOwnerBehavior();
        }
    }

    private void updateOwnerBehavior() {
        Entity ownerEntity = getSummonOwner();
        if (!(ownerEntity instanceof LivingEntity owner) || !owner.isAlive()) {
            return;
        }
        LivingEntity ownerTarget = owner instanceof net.minecraft.world.entity.Mob mob ? mob.getTarget() : owner.getLastHurtMob();
        if (!trySetOwnerTarget(ownerTarget)) {
            trySetOwnerTarget(owner.getLastHurtByMob());
        }
        if (this.getTarget() == null && this.distanceToSqr(owner) > FOLLOW_OWNER_DISTANCE_SQ) {
            this.getNavigation().moveTo(owner, FOLLOW_OWNER_SPEED);
        }
    }

    private boolean trySetOwnerTarget(@Nullable LivingEntity target) {
        if (target != null && EntityUtil.isValidSummonCombatTarget(this, getSummonOwner(), target)) {
            this.setTarget(target);
            return true;
        }
        return false;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        EntityReference.store(this.entityData.get(OWNER).orElse(null), output, "Owner");
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        EntityReference<LivingEntity> owner = EntityReference.readWithOldOwnerConversion(input, "Owner", this.level());
        this.entityData.set(OWNER, Optional.ofNullable(owner));
    }
}
