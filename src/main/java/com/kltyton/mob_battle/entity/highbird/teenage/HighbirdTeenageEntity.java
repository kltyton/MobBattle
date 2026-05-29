package com.kltyton.mob_battle.entity.highbird.teenage;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.highbird.HighbirdBaseEntity;
import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class HighbirdTeenageEntity extends HighbirdBaseEntity {
    public HighbirdTeenageEntity(EntityType<? extends HighbirdTeenageEntity> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 5;
        this.getNavigation().setCanFloat(true);
    }
    public static final int MAX_INCUBATION_TIME = 360000;
    public static final int MAX_INCUBATION_TIME_WILD = 120000;
    @Override
    public Item getfoodItem() {
        return Items.COOKED_BEEF;
    }
    @Override
    public int getMaxHunger() {
        return 60;
    }
    @Override
    public int getMaxHungerCooldown() {
        return 133;
    }
    @Override
    public int getMaxStarveTicks() {
        return 4800;
    }
    @Override
    public int getMaxFood() {
        return 8;
    }
    @Override
    public int getMaxFoodHealth() {
        return 50;
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.isTame()) {
                if (attackOwner) performAttack((ServerLevel) this.level(), this.getOwner());
                if (growthValue >= MAX_INCUBATION_TIME) levelUp();
            } else {
                if (growthValue >= MAX_INCUBATION_TIME_WILD) levelUp();
            }
        }
    }
    @Override
    protected AABB getAttackBoundingBox() {
        AABB box = super.getAttackBoundingBox();
        return box.deflate(2.0, 0.0, 2.0);
    }
    @Override
    public void levelUp() {
        if (this.level() instanceof ServerLevel serverWorld) {
            HighbirdAdulthoodEntity highbird = ModEntities.HIGHBIRD_ADULTHOOD.create(serverWorld, EntitySpawnReason.CONVERSION);
            if (highbird != null) {
                highbird.setPos(this.position());
                serverWorld.addFreshEntity(highbird);
                this.discard();
            }
        }
    }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));   // 当主人攻击某个实体时，宠物也攻击该实体
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this)); // 当主人被攻击时，攻击攻击主人的实体
        // 添加复仇目标：只攻击攻击过它的实体
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }
    public static AttributeSupplier.Builder createHighbirdAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.ATTACK_DAMAGE, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.6F)
                .add(Attributes.FOLLOW_RANGE, 24.0D); // 索敌距离
    }
}
