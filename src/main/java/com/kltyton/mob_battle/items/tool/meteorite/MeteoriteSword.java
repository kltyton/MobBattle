package com.kltyton.mob_battle.items.tool.meteorite;

import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.items.tool.BaseSword;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class MeteoriteSword extends BaseSword {
    public MeteoriteSword(Properties settings) {
        super(settings.sword(ModMaterial.KLTYTON_TOOL_MATERIAL,36, -2.2f).stacksTo(1));
    }
    @Override
    public void addStatusEffect(LivingEntity target, LivingEntity attacker) {
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 160, 2, false, true, true), attacker);
    }
}
