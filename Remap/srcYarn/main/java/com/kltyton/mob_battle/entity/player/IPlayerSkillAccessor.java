package com.kltyton.mob_battle.entity.player;

import net.minecraft.entity.LivingEntity;

public interface IPlayerSkillAccessor {
    boolean mobBattle$hasSkill();
    void mobBattle$setHasSkill(boolean hasSkill);
    boolean mobBattle$canMove();
    void mobBattle$setCanMove(boolean canMove);
    boolean mobBattle$canAttack(String animationName);
    int mobBattle$getAttackCooldown(String animationName);
    void mobBattle$setAttackCooldown(String controllerName, int cooldown);
    void mobBattle$runAttack(String controllerName, boolean canMove);
    void mobBattle$startCollision();
    void mobBattle$stopCollision();
    void mobBattle$setGrabbedEntity(LivingEntity grabbedEntity);
    LivingEntity mobBattle$getGrabbedEntity();
}
