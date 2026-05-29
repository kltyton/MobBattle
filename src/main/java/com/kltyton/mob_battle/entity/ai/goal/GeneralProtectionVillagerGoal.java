package com.kltyton.mob_battle.entity.ai.goal;

import com.kltyton.mob_battle.utils.EntityUtil;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class GeneralProtectionVillagerGoal extends TargetGoal {
    private final Mob golem;
    @Nullable
    private LivingEntity target;
    private final TargetingConditions targetPredicate = TargetingConditions.forCombat().range(64.0);

    public GeneralProtectionVillagerGoal(Mob golem) {
        super(golem, false, true);
        this.golem = golem;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        AABB box = this.golem.getBoundingBox().inflate(10.0, 8.0, 10.0);
        ServerLevel serverWorld = getServerLevel(this.golem);
        List<LivingEntity> list = serverWorld.getEntitiesOfClass(LivingEntity.class, box,
                living -> isProtectedVillager(living) && this.targetPredicate.test(serverWorld, this.golem, living));
        List<LivingEntity> list2 = EntityUtil.getNearbyEntity(this.golem, LivingEntity.class, Object.class, box, false, EntityUtil.TeamFilter.EXCLUDE_TEAM, this.targetPredicate);

        for (LivingEntity livingEntity : list) {
            for (LivingEntity playerEntity : list2) {
                LivingEntity attacker = livingEntity.getLastHurtByMob();
                if (attacker == playerEntity) {
                    this.targetMob = playerEntity;
                }
            }
        }

        return this.targetMob != null && !(this.targetMob instanceof Player playerEntity2 && (playerEntity2.isSpectator() || playerEntity2.isCreative()));
    }

    private boolean isProtectedVillager(LivingEntity living) {
        if (living instanceof Villager) {
            return true;
        }
        Package pkg = living.getClass().getPackage();
        return pkg != null && pkg.getName().contains(".entity.villager.");
    }

    @Override
    public void start() {
        this.golem.setTarget(this.targetMob);
        super.start();
    }
}

