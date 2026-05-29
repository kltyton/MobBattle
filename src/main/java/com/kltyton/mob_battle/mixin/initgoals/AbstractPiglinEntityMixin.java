package com.kltyton.mob_battle.mixin.initgoals;

import com.kltyton.mob_battle.accessor.IPiglinEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractPiglin.class)
@Implements(@Interface(iface = IPiglinEntity.class, prefix = "piglin$"))
public abstract class AbstractPiglinEntityMixin extends Monster {
    @Unique
    public LivingEntity livingEntity;

    protected AbstractPiglinEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Unique
    public LivingEntity piglin$getTargetEntity() {
        return livingEntity;
    }
    @Unique
    public void piglin$setTargetEntity(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }
}
