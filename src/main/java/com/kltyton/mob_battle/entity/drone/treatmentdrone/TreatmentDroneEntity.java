package com.kltyton.mob_battle.entity.drone.treatmentdrone;

import com.kltyton.mob_battle.entity.drone.DroneEntity;
import com.kltyton.mob_battle.entity.drone.goal.HealTeamGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;

public class TreatmentDroneEntity extends DroneEntity {
    public int healTickTimer = 0;
    public static final int HEAL_INTERVAL = 40;
    public static final float HEAL_AMOUNT = 13.0F;
    public static final float PLAYER_HEAL_AMOUNT = 1.5F;
    public static final EntityDataAccessor<Boolean> ONLY_PLAYER = SynchedEntityData.defineId(TreatmentDroneEntity.class, EntityDataSerializers.BOOLEAN);

    public TreatmentDroneEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    }

    public void setOnlyPlayer(boolean onlyPlayer) {
        this.getEntityData().set(ONLY_PLAYER, onlyPlayer);
    }

    public boolean isOnlyPlayer() {
        return this.getEntityData().get(ONLY_PLAYER);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ONLY_PLAYER, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new HealTeamGoal(this, 1.2D));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
    }
}
