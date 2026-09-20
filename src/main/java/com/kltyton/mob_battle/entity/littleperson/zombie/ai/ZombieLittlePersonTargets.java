package com.kltyton.mob_battle.entity.littleperson.zombie.ai;

import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.littleperson.militia.LittlePersonMilitiaEntity;
import com.kltyton.mob_battle.entity.littleperson.zombie.ZombieLittlePerson;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;

/** 感染实体共用的自然寻敌规则；显式队伍和召唤者友伤边界始终优先。 */
public final class ZombieLittlePersonTargets {
    private ZombieLittlePersonTargets() {
    }

    /** 只在构造阶段替换目标选择器，保留对应原型的移动与技能 Goal。 */
    public static void install(LittlePersonMilitiaEntity mob, GoalSelector targets) {
        targets.removeAllGoals(goal -> true);
        targets.addGoal(1, new HurtByTargetGoal(mob));
        targets.addGoal(2, new NearestAttackableTargetGoal<>(mob, LivingEntity.class, 10, true, false,
                (target, level) -> isNaturalTarget(mob, target)));
    }

    /** 无队伍时攻击玩家、村民、傀儡和未感染小人，不主动互相攻击。 */
    public static boolean isNaturalTarget(LittlePersonMilitiaEntity source, LivingEntity target) {
        return !(target instanceof ZombieLittlePerson)
                && (target instanceof Player || target instanceof AbstractVillager
                || target instanceof AbstractGolem || target instanceof LittlePersonEntity)
                && EntityQueries.isValidSummonCombatTarget(source, source.getSummonOwner(), target);
    }
}
