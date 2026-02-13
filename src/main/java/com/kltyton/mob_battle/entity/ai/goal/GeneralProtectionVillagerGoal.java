package com.kltyton.mob_battle.entity.ai.goal;

import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class GeneralProtectionVillagerGoal extends TrackTargetGoal {
    private final MobEntity golem;
    @Nullable
    private LivingEntity target;
    private final TargetPredicate targetPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(64.0);

    public GeneralProtectionVillagerGoal(MobEntity golem) {
        super(golem, false, true);
        this.golem = golem;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        Box box = this.golem.getBoundingBox().expand(10.0, 8.0, 10.0);
        ServerWorld serverWorld = getServerWorld(this.golem);
        List<? extends LivingEntity> list = serverWorld.getTargets(VillagerEntity.class, this.targetPredicate, this.golem, box);
        List<LivingEntity> list2 = EntityUtil.getNearbyEntity(this.golem, LivingEntity.class, Object.class, box, false, EntityUtil.TeamFilter.EXCLUDE_TEAM, this.targetPredicate);

        for (LivingEntity livingEntity : list) {
            VillagerEntity villagerEntity = (VillagerEntity)livingEntity;

            for (LivingEntity playerEntity : list2) {
                LivingEntity attacker = villagerEntity.getAttacker();
                if (attacker == playerEntity) {
                    this.target = playerEntity;
                }
            }
        }

        return this.target != null && !(this.target instanceof PlayerEntity playerEntity2 && (playerEntity2.isSpectator() || playerEntity2.isCreative()));
    }

    @Override
    public void start() {
        this.golem.setTarget(this.target);
        super.start();
    }
}

