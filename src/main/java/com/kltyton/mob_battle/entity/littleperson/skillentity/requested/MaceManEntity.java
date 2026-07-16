package com.kltyton.mob_battle.entity.littleperson.skillentity.requested;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class MaceManEntity extends RequestedTaskLittlePersonEntity {
    public MaceManEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 3);
        this.blockChance = 10;
        this.blockDamageCap = 50.0F;
        this.autoSkillRange = 5.0D;
        setCooldownSeconds(10, 8, 10);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return requestedAttributes(2000.0D, 30.0D, 0.55D, 40.0D, 0.0D);
    }

    @Override
    protected void runAttack() {
        damageTarget(30.0F, 0.0F);
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> {
                this.setNoAi(false);
                dashForward(0.55D);
                startMovingHitbox(25, 25.0F);
            }
            case 3 -> damageTarget(45.0F, 0.0F);
            case 4 -> damageTarget(30.0F, 0.0F);
            default -> {
            }
        }
    }
}
