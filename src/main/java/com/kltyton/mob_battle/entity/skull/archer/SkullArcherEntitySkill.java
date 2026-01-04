package com.kltyton.mob_battle.entity.skull.archer;

public class SkullArcherEntitySkill {
    public static void runAttackSkill(SkullArcherEntity skullArcherEntity) {
        if (skullArcherEntity.getTarget() != null) skullArcherEntity.shootAtBase(skullArcherEntity.getTarget(), 1.0F);
    }
}
