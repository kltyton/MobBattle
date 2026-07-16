package com.kltyton.mob_battle.entity.littleperson.skillentity.requested;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class LittlePersonBoxerEntity extends RequestedTaskLittlePersonEntity {
    private boolean nextFirstAttack = true;

    public LittlePersonBoxerEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 4);
        this.blockChance = 10;
        this.blockDamageCap = 50.0F;
        this.autoSkillRange = 6.0D;
        setCooldownSeconds(8, 15, 15, 15);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return requestedAttributes(2000.0D, 30.0D, 0.55D, 40.0D, 0.0D);
    }

    @Override
    protected void performNormalAttack() {
        this.setHasSkill(true);
        this.setNormalAttackKnockbackAllowed(true);
        this.setNoAi(false);
        this.triggerAnim("skill_controller", this.nextFirstAttack ? "attack_1" : "attack_2");
        this.nextFirstAttack = !this.nextFirstAttack;
    }

    @Override
    protected void runAttack() {
        damageTarget(30.0F, 0.0F);
    }

    @Override
    protected void runAttackVariant(int variant) {
        damageTarget(30.0F, 0.0F);
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> {
                LivingEntity target = this.getTarget();
                damageTarget(40.0F, 0.0F);
                if (target != null && isValidSummonTarget(target)) {
                    knockTargetAway(target, 1.5D, 0.45D);
                }
            }
            case 3 -> damageTarget(50.0F, 0.0F);
            case 4 -> {
                dashForward(0.75D);
                forwardBoxDamage(4.0D, 1.5D, 2.0D, 30.0F, 0.0F);
            }
            case 5 -> damageTarget(50.0F, 0.0F);
            default -> {
            }
        }
    }
}
