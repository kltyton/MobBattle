package com.kltyton.mob_battle.entity.roughwhitezetsu;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** 粗糙白绝只替换僵尸外观，保留 Zombie 的原版 AI、转化与掉落行为。 */
public final class RoughWhiteZetsuEntity extends Zombie {
    public RoughWhiteZetsuEntity(EntityType<? extends Zombie> entityType, Level world) {
        super(entityType, world);
        this.setSilent(true);
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }
}
