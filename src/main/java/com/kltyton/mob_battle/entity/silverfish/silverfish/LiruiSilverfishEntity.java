package com.kltyton.mob_battle.entity.silverfish.silverfish;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;

public class LiruiSilverfishEntity extends CoalSilverfishEntity {
    public LiruiSilverfishEntity(EntityType<? extends Silverfish> entityType, Level world) {
        super(entityType, world);
    }
    @Override
    public int getCooldownTime() {
        return 20 * 35;
    }
    @Override
    public boolean canBlock() {
        return false;
    }
    @Override
    public void runSkill(CoalSilverfishEntity entity) {
        if (entity.getTarget() != null && !this.level().isClientSide) entity.getTarget().hurtServer((ServerLevel) this.level(), this.damageSources().mobAttack(this), 230);
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Silverfish.createAttributes()
                .add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.ATTACK_DAMAGE, 100.0D)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.5);
    }
}
