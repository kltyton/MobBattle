package com.kltyton.mob_battle.entity.misc;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ModifiedDragonBreathCloud extends AreaEffectCloud {

    public ModifiedDragonBreathCloud(EntityType<? extends AreaEffectCloud> type, Level world) {
        super(type, world);
    }

    public ModifiedDragonBreathCloud(Level world, double x, double y, double z) {
        this(ModEntities.MODIFIED_DRAGON_BREATH_CLOUD, world);
        this.setPos(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;
        if (this.tickCount >= 200) {
            this.discard();
        }
        // 每4 tick 造成一次伤害（性能好）
        if (this.tickCount % 4 == 0) {
            ServerLevel serverWorld = (ServerLevel) this.level();
            DamageSource source = this.damageSources().magic();  // 或 custom source

            for (LivingEntity living : serverWorld.getEntitiesOfClass(
                    LivingEntity.class, this.getBoundingBox().inflate(0.5))) {
                if (!living.isSpectator()) {
                    if (!EntityUtil.isValidSummonCombatTarget(this, this.getOwner(), living)) continue;
                    living.hurtServer(serverWorld, source, 50.0F);  // 50点魔法伤害
                }
            }
        }
    }
}
