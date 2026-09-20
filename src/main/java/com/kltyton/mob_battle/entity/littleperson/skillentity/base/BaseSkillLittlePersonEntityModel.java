package com.kltyton.mob_battle.entity.littleperson.skillentity.base;

import com.kltyton.mob_battle.client.model.littleperson.LittlePersonAppearanceModel;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import net.minecraft.world.entity.LivingEntity;

/** 技能小人沿用公开名称和手部标志，外观资源由共享彩蛋模型选择。 */
public class BaseSkillLittlePersonEntityModel<T extends LivingEntity & LittlePersonEntity> extends LittlePersonAppearanceModel<T> {
    public boolean hasHand;
    public BaseSkillLittlePersonEntityModel(String name, boolean hasHand) {
        super(name);
        this.hasHand = hasHand;
    }
}
