package com.kltyton.mob_battle.items.tool.sword.zijin;

import com.kltyton.mob_battle.items.tool.BaseSword;
import com.kltyton.mob_battle.utils.CombatEffectUtil;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;

public class ZiJinSword extends BaseSword {
    public ZiJinSword(Settings settings) {
        super(settings);
    }

    @Override
    public void addStatusEffect(LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            boolean isCritical = player.getAttackCooldownProgress(0.5F) > 0.9F
                    && player.fallDistance > 0.0F
                    && !player.isOnGround()
                    && !player.isClimbing()
                    && !player.isTouchingWater()
                    && !player.hasStatusEffect(StatusEffects.BLINDNESS)
                    && !player.hasVehicle()
                    && !player.isSprinting();

            CombatEffectUtil.addPigSpiritMark(target, player, isCritical ? 8 : 1);

            if (isCritical) {
                target.getWorld().addParticleClient(ParticleTypes.CRIT,
                        target.getX(), target.getRandomBodyY(), target.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHit(stack, target, attacker);
        if (!(attacker instanceof PlayerEntity player) || attacker.getWorld().isClient()) {
            return;
        }
        if (player.getAttackCooldownProgress(0.5F) <= 0.9F) {
            return;
        }
        for (LivingEntity sweptTarget : EntityUtil.getNearbyEntity(player, LivingEntity.class, 3.0D, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            if (sweptTarget != target) {
                CombatEffectUtil.addPigSpiritMark(sweptTarget, player, 4);
            }
        }
    }
}
