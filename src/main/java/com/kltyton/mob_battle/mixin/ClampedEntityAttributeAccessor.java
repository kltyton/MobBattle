package com.kltyton.mob_battle.mixin;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClampedEntityAttribute.class)
public interface ClampedEntityAttributeAccessor {
    @Accessor("minValue")
    @Mutable
    void setMinValue(double minValue);

    @Accessor("maxValue")
    @Mutable
    void setMaxValue(double maxValue);
}
