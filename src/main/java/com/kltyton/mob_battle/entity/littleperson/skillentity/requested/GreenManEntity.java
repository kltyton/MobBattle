package com.kltyton.mob_battle.entity.littleperson.skillentity.requested;

import com.kltyton.mob_battle.event.scheduler.ServerTickScheduler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class GreenManEntity extends RequestedTaskLittlePersonEntity {
    @Override
    public String getDeathAnimationName() {
        return "die";
    }

    public GreenManEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 2);
        this.healPerSecond = 1.0F;
        this.autoSkillRange = 6.0D;
        setCooldownSeconds(30, 60);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return requestedAttributes(2000.0D, 25.0D, 0.50D, 40.0D, 0.0D);
    }

    @Override
    protected void performNormalAttack() {
        this.setHasSkill(true);
        this.setNormalAttackKnockbackAllowed(true);
        this.setNoAi(true);
        this.triggerAnim("skill_controller", "attack");
    }

    @Override
    protected void runAttack() {
        areaDamage(1.5D, 25.0F, 0.0F);
    }

    @Override
    protected boolean canUseNormalAttack(LivingEntity target) {
        return !this.hasSkill() && this.distanceTo(target) <= 4.5D;
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> {
                for (int i = 0; i < 20; i++) {
                    ServerTickScheduler.schedule(this.level().getServer(), i, () -> {
                        if (!this.isRemoved()) {
                            damageTargetNoInvulnerability(1.0F);
                        }
                    });
                }
            }
            case 3 -> areaDamage(3.0D, 80.0F, 0.0F);
            default -> {
            }
        }
    }
}
