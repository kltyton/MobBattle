package com.kltyton.mob_battle.entity.highbird;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import software.bernie.geckolib.animatable.GeoEntity;

public abstract class HighbirdAndEggEntity extends TamableAnimal implements GeoEntity {
    // 常量定义
    protected static final String GROWTH_VALUE_KEY = "GrowthValue";
    protected int growthValue = 0;
    protected HighbirdAndEggEntity(EntityType<? extends HighbirdAndEggEntity> entityType, Level world) {
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
        if (!this.level().isClientSide() && startGrowth()) {
            this.growthValue++;
        }
    }
    // ========== NBT 数据持久化 ==========
    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        this.growthValue = view.getIntOr(GROWTH_VALUE_KEY, 0);
    }
    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
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
        long time = this.level().getDayTime() % 24000; // 获取当天游戏刻（0~23999）
        return time >= 1000 && time < 13000;
    }
}
