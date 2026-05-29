package com.kltyton.mob_battle.entity.highbird.predicate;

import com.kltyton.mob_battle.entity.highbird.HighbirdAndEggEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class NonHighbirdPredicate implements TargetingConditions.Selector {
    @Override
    public boolean test(LivingEntity target, ServerLevel world) {
        return !(target instanceof HighbirdAndEggEntity);
    }
}
