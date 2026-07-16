package com.kltyton.mob_battle.entity.littleperson.skillentity.requested;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class YemoWenluEntity extends RequestedTaskLittlePersonEntity {
    public YemoWenluEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 5);
        this.healPerSecond = 5.0F;
        this.blockChance = 20;
        this.blockDamageCap = 200.0F;
        this.autoSkillRange = 12.0D;
        setCooldownSeconds(10, 15, 15, 15, 7);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return requestedAttributes(6500.0D, 80.0D, 0.55D, 40.0D, 0.30D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7D);
    }

    @Override
    protected double skillRange(String skillName) {
        return "attack6".equals(skillName) ? 3.5D : 8.0D;
    }

    @Override
    protected void runAttack() {
        damageTarget(80.0F, 0.0F);
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> damageTarget(switch (phase) {
                case 1 -> 100.0F;
                case 2 -> 115.0F;
                default -> 90.0F;
            }, 0.0F);
            case 3 -> forwardBoxDamage(4.0D, 2.0D, 2.0D, 150.0F, 0.0F);
            case 4 -> {
                LivingEntity target = this.getTarget();
                damageTarget(100.0F, 0.0F);
                if (phase > 0 && target != null && isValidSummonTarget(target)) {
                    addPiercing(target, 5, 1);
                }
            }
            case 5 -> areaDamage(2.5D, 100.0F, 0.0F);
            case 6 -> damageTarget(150.0F, 0.0F);
            default -> {
            }
        }
    }
}
