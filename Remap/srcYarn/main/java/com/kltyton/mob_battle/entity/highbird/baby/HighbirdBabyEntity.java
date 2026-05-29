package com.kltyton.mob_battle.entity.highbird.baby;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.highbird.HighbirdBaseEntity;
import com.kltyton.mob_battle.entity.highbird.egg.HighbirdSBEntity;
import com.kltyton.mob_battle.entity.highbird.teenage.HighbirdTeenageEntity;
import com.kltyton.mob_battle.sounds.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class HighbirdBabyEntity extends HighbirdBaseEntity implements HighbirdSBEntity {
    public static final int MAX_INCUBATION_TIME = 240000;
    public static final int MAX_INCUBATION_TIME_WILD = 72000;

    public HighbirdBabyEntity(EntityType<? extends HighbirdBabyEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 5;
        this.getNavigation().setCanSwim(true);
    }
    @Override
    public Item getfoodItem() {
        return Items.SWEET_BERRIES;
    }
    @Override
    public int getMaxHunger() {
        return 20;
    }
    @Override
    public int getMaxHungerCooldown() {
        return 600;
    }
    @Override
    public int getMaxStarveTicks() {
        return 2400;
    }
    @Override
    public int getMaxFood() {
        return 2;
    }
    public int getMaxFoodHealth() {
        return 20;
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (this.isTamed()) {
                if (starveTicks % 240 == 0) this.playSound(ModSounds.DOG_JIAO_SOUND_EVENT);
                if (growthValue >= MAX_INCUBATION_TIME) levelUp();
            } else {
                if (growthValue >= MAX_INCUBATION_TIME_WILD) levelUp();
            }
        }
    }
    @Override
    public void levelUp() {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            HighbirdTeenageEntity highbird = ModEntities.HIGHBIRD_TEENAGE.create(serverWorld, SpawnReason.CONVERSION);
            if (highbird != null) {
                if (this.isTamed()) {
                    highbird.setOwner(this.getOwnerReference());
                    highbird.setOwner(this.getOwner());
                    if (this.getOwner() instanceof PlayerEntity player) highbird.setTamedBy(player);
                    highbird.setTamed(true, true);
                    highbird.setPosition(this.getPos());
                }
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
    // 关键修改：覆盖实体分组方法
        /* 属性注册 */
    public static DefaultAttributeContainer.Builder createHighbirdAttributes() {
        return AnimalEntity.createAnimalAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20.0D)
                .add(EntityAttributes.ATTACK_DAMAGE, 8.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3F)
                .add(EntityAttributes.FOLLOW_RANGE, 16.0D); // 索敌距离
    }
}
