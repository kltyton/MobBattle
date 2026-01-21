package com.kltyton.mob_battle.entity.littleperson.skillentity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

public class WildBoarEntity extends BaseSkillLittlePersonEntity {
    public WildBoarEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, 0);
    }
    public static DefaultAttributeContainer.Builder createLittlePersonAttributes() {
        return BaseSkillLittlePersonEntity.createAttributes()
                .add(EntityAttributes.MAX_HEALTH, 250.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 30.00);
    }
}
