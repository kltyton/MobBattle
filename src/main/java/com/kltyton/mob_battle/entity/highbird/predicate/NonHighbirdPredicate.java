package com.kltyton.mob_battle.entity.highbird.predicate;

import com.kltyton.mob_battle.entity.highbird.HighbirdAndEggEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.server.world.ServerWorld;

public class NonHighbirdPredicate implements TargetPredicate.EntityPredicate {
    @Override
    public boolean test(LivingEntity target, ServerWorld world) {
        return !(target instanceof HighbirdAndEggEntity);
    }
}
