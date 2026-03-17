package com.kltyton.mob_battle.items.tool.sword.zijin;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.items.tool.BaseSword;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;

public class ZiJinSword extends BaseSword {

    public ZiJinSword(Settings settings) {
        super(settings);
    }

    @Override
    public void addStatusEffect(LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            float h = player.getAttackCooldownProgress(0.5F);
            boolean bl = h > 0.9F;

            // 1. 判定是否为暴击
            boolean isCritical = bl
                    && player.fallDistance > 0.0
                    && !player.isOnGround()
                    && !player.isClimbing()
                    && !player.isTouchingWater()
                    && !player.hasStatusEffect(StatusEffects.BLINDNESS)
                    && !player.hasVehicle()
                    && target instanceof LivingEntity
                    && !player.isSprinting();

            // 2. 确定增加的层数
            int layersToAdd = isCritical ? 2 : 1;
            int duration = 8 * 20; // 8秒 = 160 ticks

            // 3. 处理叠加逻辑
            int currentAmplifier = -1; // -1 表示当前没有该效果
            if (target.hasStatusEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY)) {
                currentAmplifier = target.getStatusEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY).getAmplifier();
            }
            int newAmplifier = currentAmplifier + layersToAdd;

            // 限制最大等级为 80级 (amplifier 79)
            newAmplifier = Math.min(newAmplifier, 79);

            // 4. 应用效果
            target.addStatusEffect(new StatusEffectInstance(
                    ModEffects.PIG_SPIRIT_MARK_ENTRY,
                    duration,
                    newAmplifier,
                    false,
                    false
            ));

            if (isCritical) {
                target.getWorld().addParticleClient(ParticleTypes.CRIT,
                        target.getX(), target.getRandomBodyY(), target.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }
}