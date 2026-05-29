package com.kltyton.mob_battle.entity.highbird.teenage;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.highbird.HighbirdBaseEntity;
import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class HighbirdTeenageEntity extends HighbirdBaseEntity {
    public HighbirdTeenageEntity(EntityType<? extends HighbirdTeenageEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 5;
        this.getNavigation().setCanSwim(true);
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
        if (!this.getWorld().isClient) {
            if (this.isTamed()) {
                if (attackOwner) performAttack((ServerWorld) this.getWorld(), this.getOwner());
                if (growthValue >= MAX_INCUBATION_TIME) levelUp();
            } else {
                if (growthValue >= MAX_INCUBATION_TIME_WILD) levelUp();
            }
        }
    }
    @Override
    protected Box getAttackBox() {
        Box box = super.getAttackBox();
        return box.contract(2.0, 0.0, 2.0);
    }
    @Override
    public void levelUp() {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            HighbirdAdulthoodEntity highbird = ModEntities.HIGHBIRD_ADULTHOOD.create(serverWorld, SpawnReason.CONVERSION);
            if (highbird != null) {
                highbird.setPosition(this.getPos());
                serverWorld.spawnEntity(highbird);
                this.discard();
            }
        }
    }
    @Override
    protected void initGoals() {
        super.initGoals();
        this.targetSelector.add(1, new AttackWithOwnerGoal(this));   // 当主人攻击某个实体时，宠物也攻击该实体
        this.targetSelector.add(2, new TrackOwnerAttackerGoal(this)); // 当主人被攻击时，攻击攻击主人的实体
        // 添加复仇目标：只攻击攻击过它的实体
        this.targetSelector.add(3, new RevengeGoal(this));
    }
    public static DefaultAttributeContainer.Builder createHighbirdAttributes() {
        return AnimalEntity.createAnimalAttributes()
                .add(EntityAttributes.MAX_HEALTH, 300.0D)
                .add(EntityAttributes.ATTACK_DAMAGE, 20.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.6F)
                .add(EntityAttributes.FOLLOW_RANGE, 24.0D); // 索敌距离
    }
}
