package com.kltyton.mob_battle.entity.boss.dragon;

import com.kltyton.mob_battle.entity.OwnedSummon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.jetbrains.annotations.Nullable;

public class EnderDragonShadowEntity extends EnderDragon implements OwnedSummon {

    private EnderDragon ownerDragon; // 主龙引用

    public EnderDragonShadowEntity(EntityType<? extends EnderDragon> type, Level world) {
        super(type, world);
    }
    public void setOwner(EnderDragon owner) {
        this.ownerDragon = owner;
    }
    @Nullable
    @Override
    public Entity getSummonOwner() {
        return this.ownerDragon;
    }
    // 阻止虚影参与原版战斗系统（无Boss血条）
    @Override
    public void setDragonFight(EndDragonFight fight) {
        // do nothing
    }

    @Override
    public void aiStep() {
        // 主龙死亡 → 虚影立即死亡
        if (ownerDragon != null && (!ownerDragon.isAlive() || ownerDragon.isRemoved())) {
            this.kill((ServerLevel) this.level());
            return;
        }
        super.aiStep();
    }
}
