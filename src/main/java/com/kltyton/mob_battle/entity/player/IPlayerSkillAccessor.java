package com.kltyton.mob_battle.entity.player;

import net.minecraft.world.entity.LivingEntity;

public interface IPlayerSkillAccessor {
    boolean mobBattle$hasSkill();
    void mobBattle$setHasSkill(boolean hasSkill);
    boolean mobBattle$canMove();
    void mobBattle$setCanMove(boolean canMove);
    boolean mobBattle$canAttack(String animationName);
    int mobBattle$getAttackCooldown(String animationName);
    void mobBattle$setAttackCooldown(String controllerName, int cooldown);
    String mobBattle$getActiveSkill();
    void mobBattle$setActiveSkill(String skillName);
    /** 返回当前服务端技能会话的启动 tick；无有效会话时返回负数。 */
    long mobBattle$getSkillStartTick();
    /** 返回服务端允许接受 stop 的最早 tick；无有效会话时返回负数。 */
    long mobBattle$getEarliestStopTick();
    /**
     * 在当前服务端确认的技能生命周期内消费一次命中命令。
     */
    boolean mobBattle$consumeHitCommand(String command);
    /**
     * 尝试启动技能；返回值表示本次是否实际通过冷却和状态检查。
     */
    boolean mobBattle$runAttack(String controllerName, boolean canMove);
    void mobBattle$startCollision();
    void mobBattle$stopCollision();
    void mobBattle$setGrabbedEntity(LivingEntity grabbedEntity);
    LivingEntity mobBattle$getGrabbedEntity();
}
