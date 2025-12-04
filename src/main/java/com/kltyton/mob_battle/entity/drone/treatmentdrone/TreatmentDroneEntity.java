package com.kltyton.mob_battle.entity.drone.treatmentdrone;

import com.kltyton.mob_battle.entity.drone.DroneEntity;
import com.kltyton.mob_battle.entity.drone.goal.HealTeamGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.world.World;

public class TreatmentDroneEntity extends DroneEntity {
    // 是否只治疗玩家（默认为 false，方便以后用染色或配置）
    public int healTickTimer = 0;
    public static final int HEAL_INTERVAL = 20;   // 每20tick治疗一次`
    public static final float HEAL_AMOUNT = 30.0F; // 每次恢复30血（15颗心）
    public static final float PLAYER_HEAL_AMOUNT = 7f;
    public static final TrackedData<Boolean> ONLY_PLAYER = DataTracker.registerData(TreatmentDroneEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public void setOnlyPlayer(boolean onlyPlayer) {
        this.getDataTracker().set(ONLY_PLAYER, onlyPlayer);
    }

    public boolean isOnlyPlayer() {
        return this.getDataTracker().get(ONLY_PLAYER);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(ONLY_PLAYER, false);
    }
    public TreatmentDroneEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(3, new HealTeamGoal(this, 1.2D));
    }
    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
    }
}
