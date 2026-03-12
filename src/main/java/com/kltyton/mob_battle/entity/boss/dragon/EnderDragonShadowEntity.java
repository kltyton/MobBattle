package com.kltyton.mob_battle.entity.boss.dragon;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class EnderDragonShadowEntity extends EnderDragonEntity {

    private EnderDragonEntity ownerDragon; // 主龙引用

    public EnderDragonShadowEntity(EntityType<? extends EnderDragonEntity> type, World world) {
        super(type, world);
    }
    public void setOwner(EnderDragonEntity owner) {
        this.ownerDragon = owner;
    }
    // 阻止虚影参与原版战斗系统（无Boss血条）
    @Override
    public void setFight(EnderDragonFight fight) {
        // do nothing
    }

    @Override
    public void tickMovement() {
        // 主龙死亡 → 虚影立即死亡
        if (ownerDragon != null && (!ownerDragon.isAlive() || ownerDragon.isRemoved())) {
            this.kill((ServerWorld) this.getWorld());
            return;
        }
        super.tickMovement();
    }
}
