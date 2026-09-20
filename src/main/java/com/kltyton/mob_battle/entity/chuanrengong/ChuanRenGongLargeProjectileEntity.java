package com.kltyton.mob_battle.entity.chuanrengong;

import com.kltyton.mob_battle.entity.littleperson.skillentity.SkillProjectileEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** 传仁工 attack3 使用的大型弹体。 */
public final class ChuanRenGongLargeProjectileEntity extends SkillProjectileEntity {
    public ChuanRenGongLargeProjectileEntity(EntityType<? extends ChuanRenGongLargeProjectileEntity> entityType,
                                              Level world) {
        super(entityType, world);
    }
}
