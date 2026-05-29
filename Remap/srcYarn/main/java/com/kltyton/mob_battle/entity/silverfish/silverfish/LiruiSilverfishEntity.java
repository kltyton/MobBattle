package com.kltyton.mob_battle.entity.silverfish.silverfish;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class LiruiSilverfishEntity extends CoalSilverfishEntity {
    public LiruiSilverfishEntity(EntityType<? extends SilverfishEntity> entityType, World world) {
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
        if (entity.getTarget() != null && !this.getWorld().isClient) entity.getTarget().damage((ServerWorld) this.getWorld(), this.getDamageSources().mobAttack(this), 230);
    }
    public static DefaultAttributeContainer.Builder createAttributes() {
        return SilverfishEntity.createSilverfishAttributes()
                .add(EntityAttributes.MAX_HEALTH, 300.0D)
                .add(EntityAttributes.ATTACK_DAMAGE, 100.0D)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.5);
    }
}
