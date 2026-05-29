package com.kltyton.mob_battle.items.tool.meteorite;

import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.items.tool.BaseSword;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class MeteoriteSword extends BaseSword {
    public MeteoriteSword(Settings settings) {
        super(settings.sword(ModMaterial.KLTYTON_TOOL_MATERIAL,36, -2.2f).maxCount(1));
    }
    @Override
    public void addStatusEffect(LivingEntity target, LivingEntity attacker) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 160, 2, false, true, true), attacker);
    }
}
