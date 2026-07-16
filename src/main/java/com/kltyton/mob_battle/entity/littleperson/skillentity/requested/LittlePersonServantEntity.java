package com.kltyton.mob_battle.entity.littleperson.skillentity.requested;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class LittlePersonServantEntity extends RequestedTaskLittlePersonEntity {
    public LittlePersonServantEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 0);
        this.autoSkillRange = 0.0D;
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return requestedAttributes(10.0D, 2.0D, 0.4D, 24.0D, 0.0D);
    }

    @Override
    protected void runAttack() {
        damageTarget(2.0F, 0.0F);
    }

    @Override
    protected void runSkill(int attack, int phase) {
    }
}
