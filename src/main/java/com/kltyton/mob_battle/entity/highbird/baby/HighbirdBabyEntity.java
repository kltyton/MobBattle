package com.kltyton.mob_battle.entity.highbird.baby;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.highbird.HighbirdBaseEntity;
import com.kltyton.mob_battle.entity.highbird.egg.HighbirdSBEntity;
import com.kltyton.mob_battle.entity.highbird.teenage.HighbirdTeenageEntity;
import com.kltyton.mob_battle.sounds.ModSounds;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class HighbirdBabyEntity extends HighbirdBaseEntity implements HighbirdSBEntity {
    public static final int MAX_INCUBATION_TIME = 240000;
    public static final int MAX_INCUBATION_TIME_WILD = 72000;

    public HighbirdBabyEntity(EntityType<? extends HighbirdBabyEntity> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 5;
        this.getNavigation().setCanFloat(true);
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
        if (!this.level().isClientSide) {
            if (this.isTame()) {
                if (starveTicks % 240 == 0) this.makeSound(ModSounds.DOG_JIAO_SOUND_EVENT);
                if (growthValue >= MAX_INCUBATION_TIME) levelUp();
            } else {
                if (growthValue >= MAX_INCUBATION_TIME_WILD) levelUp();
            }
        }
    }
    @Override
    public void levelUp() {
        if (this.level() instanceof ServerLevel serverWorld) {
            HighbirdTeenageEntity highbird = ModEntities.HIGHBIRD_TEENAGE.create(serverWorld, EntitySpawnReason.CONVERSION);
            if (highbird != null) {
                if (this.isTame()) {
                    highbird.setOwnerReference(this.getOwnerReference());
                    highbird.setOwner(this.getOwner());
                    if (this.getOwner() instanceof Player player) highbird.tame(player);
                    highbird.setTame(true, true);
                    highbird.setPos(this.position());
                }
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
    // 关键修改：覆盖实体分组方法
        /* 属性注册 */
    public static AttributeSupplier.Builder createHighbirdAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.FOLLOW_RANGE, 16.0D); // 索敌距离
    }
}
