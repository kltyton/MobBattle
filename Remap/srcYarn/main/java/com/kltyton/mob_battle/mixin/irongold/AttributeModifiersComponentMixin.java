package com.kltyton.mob_battle.mixin.irongold;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.text.DecimalFormat;

@Mixin(AttributeModifiersComponent.Display.Default.class)
public class AttributeModifiersComponentMixin {
    @Redirect(method = "addTooltip",
            at = @At(value = "INVOKE", target = "Ljava/text/DecimalFormat;format(D)Ljava/lang/String;")
    )
    public String addTooltip(DecimalFormat instance, double v, @Local(argsOnly = true) RegistryEntry<EntityAttribute> entry, @Local(argsOnly = true) EntityAttributeModifier modifier) {
        if (v >= Float.MAX_VALUE || (entry.value() instanceof ClampedEntityAttribute attribute && modifier.value() >= attribute.getMaxValue())) {
            return Text.translatable("options.ao.max").getString();
        } else {
            return instance.format(v);
        }
    }
}
