package com.kltyton.mob_battle.mixin.initgoals;

import com.kltyton.mob_battle.accessor.IPiglinEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractPiglinEntity.class)
@Implements(@Interface(iface = IPiglinEntity.class, prefix = "piglin$"))
public abstract class AbstractPiglinEntityMixin extends HostileEntity {
    @Unique
    public LivingEntity livingEntity;

    protected AbstractPiglinEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
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
