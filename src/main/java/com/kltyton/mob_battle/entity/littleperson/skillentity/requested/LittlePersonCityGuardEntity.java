package com.kltyton.mob_battle.entity.littleperson.skillentity.requested;

import com.kltyton.mob_battle.entity.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class LittlePersonCityGuardEntity extends RequestedTaskLittlePersonEntity {
    public LittlePersonCityGuardEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 1);
        this.healPerSecond = 1.0F;
        this.autoSkillRange = 6.0D;
        setCooldownSeconds(30);
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return requestedAttributes(100.0D, 5.0D, 0.45D, 40.0D, 0.0D);
    }

    @Override
    protected void runAttack() {
        damageTarget(5.0F, 0.0F);
    }

    @Override
    protected void runSkill(int attack, int phase) {
        if (attack != 2 || !(this.level() instanceof ServerLevel world)) {
            return;
        }
        LittlePersonServantEntity servant = ModEntities.LITTLE_PERSON_SERVANT.create(world, EntitySpawnReason.MOB_SUMMONED);
        if (servant == null) {
            return;
        }
        servant.snapTo(
                this.getX() + (this.getRandom().nextDouble() - 0.5D),
                this.getY(),
                this.getZ() + (this.getRandom().nextDouble() - 0.5D),
                this.getYRot(),
                this.getXRot()
        );
        LivingEntity owner = this.getSummonOwner() != null ? this.getSummonOwner() : this;
        servant.setSummonOwner(owner);
        world.addFreshEntity(servant);
    }
}
