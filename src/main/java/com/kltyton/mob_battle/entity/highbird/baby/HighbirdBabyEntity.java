package com.kltyton.mob_battle.entity.highbird.baby;

import com.kltyton.mob_battle.entity.highbird.HighbirdBaseEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class HighbirdBabyEntity extends HighbirdBaseEntity {

    public HighbirdBabyEntity(EntityType<? extends HighbirdBabyEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 5;
        this.getNavigation().setCanSwim(true);
    }
    /* 属性注册 */
    public static DefaultAttributeContainer.Builder createHighbirdAttributes() {
        return AnimalEntity.createAnimalAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20.0D)
                .add(EntityAttributes.ATTACK_DAMAGE, 8.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3F)
                .add(EntityAttributes.ATTACK_SPEED, 1.0D)   // 攻击速度（影响冷却）
                .add(EntityAttributes.FOLLOW_RANGE, 16.0D); // 索敌距离
    }
}
