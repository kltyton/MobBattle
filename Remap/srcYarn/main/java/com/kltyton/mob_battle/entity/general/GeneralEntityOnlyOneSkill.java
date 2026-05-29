package com.kltyton.mob_battle.entity.general;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import net.minecraft.entity.mob.MobEntity;
import software.bernie.geckolib.animatable.GeoEntity;

public interface GeneralEntityOnlyOneSkill<T extends MobEntity> extends ModSkillEntityType, GeoEntity {
    void runSkill(T entity);
    boolean hasSkill();
    void setHasSkill(boolean skill);
    default void stopSkill() {
        this.setHasSkill(false);
    }
}
