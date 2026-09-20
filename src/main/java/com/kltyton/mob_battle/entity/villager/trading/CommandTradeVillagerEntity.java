package com.kltyton.mob_battle.entity.villager.trading;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

/**
 * 仅通过命令写入 Offers 的固定傻子职业村民基类。
 *
 * <p>NITWIT 不会认领工作站，原版 Villager 的 Offers 存档仍保留，因此管理员可用
 * 原版数据命令直接配置交易内容。</p>
 */
public abstract class CommandTradeVillagerEntity extends Villager implements NoHeroDiscountVillager {
    protected CommandTradeVillagerEntity(EntityType<? extends Villager> type, Level level) {
        super(type, level);
        this.setVillagerData(this.getVillagerData().withProfession(level.registryAccess(), VillagerProfession.NITWIT).withLevel(1));
    }

    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            EntitySpawnReason reason,
            @Nullable SpawnGroupData spawnData
    ) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, spawnData);
        this.setVillagerData(this.getVillagerData().withProfession(level.registryAccess(), VillagerProfession.NITWIT).withLevel(1));
        return result;
    }

    @Override
    public void setVillagerData(VillagerData data) {
        super.setVillagerData(data.withProfession(this.level().registryAccess(), VillagerProfession.NITWIT));
    }
}
