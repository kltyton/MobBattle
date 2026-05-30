package com.kltyton.mob_battle.mixin.irongold;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.text.DecimalFormat;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.component.ItemAttributeModifiers;

@Mixin(ItemAttributeModifiers.Display.Default.class)
public class AttributeModifiersComponentMixin {
    @Redirect(method = "apply",
            at = @At(value = "INVOKE", target = "Ljava/text/DecimalFormat;format(D)Ljava/lang/String;")
    )
    public String addTooltip(DecimalFormat instance, double v, @Local(argsOnly = true) Holder<Attribute> entry, @Local(argsOnly = true) AttributeModifier modifier) {
        if (v >= Float.MAX_VALUE || (entry.value() instanceof RangedAttribute attribute && modifier.amount() >= attribute.getMaxValue())) {
            return Component.translatable("options.ao.max").getString();
        } else {
            return instance.format(v);
        }
    }
}
