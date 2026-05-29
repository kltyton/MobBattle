package com.kltyton.mob_battle.entity.highbird;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;

public abstract class HighbirdAndEggEntity extends TameableEntity implements GeoEntity {
    // 常量定义
    protected static final String GROWTH_VALUE_KEY = "GrowthValue";
    protected int growthValue = 0;
    protected HighbirdAndEggEntity(EntityType<? extends HighbirdAndEggEntity> entityType, World world) {
        super(entityType, world);
    }
    protected boolean startGrowth() {
        return true;
    }
    protected void levelUp() {}
    @Override
    public void tick() {
        super.tick();

        // 仅在服务器端增加成长值（避免客户端和服务端不同步）
        if (!this.getWorld().isClient() && startGrowth()) {
            this.growthValue++;
        }
    }
    // ========== NBT 数据持久化 ==========
    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.growthValue = view.getInt(GROWTH_VALUE_KEY, 0);
    }
    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putInt(GROWTH_VALUE_KEY, this.growthValue);
    }
    // ========== 成长值相关方法 ==========
    public int getGrowthValue() {
        return growthValue;
    }

    public void setGrowthValue(int value) {
        this.growthValue = value;
    }
    public boolean isDay() {
        long time = this.getWorld().getTimeOfDay() % 24000; // 获取当天游戏刻（0~23999）
        return time >= 1000 && time < 13000;
    }
}
