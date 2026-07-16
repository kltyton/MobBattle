package com.kltyton.mob_battle.entity.littleperson.skillentity.requested;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class RenfuEntity extends RequestedTaskLittlePersonEntity {
    public RenfuEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 3);
        this.healPerSecond = 3.0F;
        this.blockChance = 10;
        this.blockDamageCap = 80.0F;
        this.autoSkillRange = 6.0D;
        setCooldownSeconds(10, 10, 25);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return requestedAttributes(4000.0D, 45.0D, 0.55D, 40.0D, 0.0D);
    }

    @Override
    protected void runAttack() {
        damageTarget(45.0F, 0.0F);
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> areaDamage(2.0D, 80.0F, 0.0F);
            case 3 -> areaDamage(2.0D, 70.0F, 0.0F);
            case 4 -> areaDamage(2.0D, phase == 0 ? 80.0F : 100.0F, 0.0F);
            default -> {
            }
        }
    }
}
