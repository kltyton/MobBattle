package com.kltyton.mob_battle.enchantment.effect;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * 陨雷在成功的重锤下落命中后生成一道雷，按原版冲击范围结算本次下落附加伤害。
 * 触发条件由附魔 JSON 沿用风爆条件；雷实体仅显示，伤害只结算一次并保留攻击者归属。
 */
public record FallingThunderEnchantmentEffect() implements EnchantmentEntityEffect {
    public static final MapCodec<FallingThunderEnchantmentEffect> CODEC =
            MapCodec.unit(new FallingThunderEnchantmentEffect());
    private static final ResourceKey<DamageType> DAMAGE_TYPE = ResourceKey.create(
            Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "falling_thunder"));

    @Override
    public void apply(ServerLevel world, int level, EnchantedItemInUse context, Entity target, Vec3 position) {
        LivingEntity attacker = context.owner();
        if (attacker == null || !(target instanceof LivingEntity victim)
                || !(context.itemStack().getItem() instanceof MaceItem mace)) {
            return;
        }
        float damage = mace.getAttackDamageBonus(victim, 0.0F, attacker.damageSources().mace(attacker));
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world, EntitySpawnReason.EVENT);
        if (lightning == null) {
            return;
        }
        lightning.setPos(position);
        lightning.setVisualOnly(true);
        if (attacker instanceof ServerPlayer player) {
            lightning.setCause(player);
        }
        world.addFreshEntity(lightning);
        if (damage <= 0.0F) {
            return;
        }

        // 专用伤害类型只绕过受伤冷却，使紧随重锤的雷伤不会被原命中的无敌帧吞掉。
        DamageSource source = new DamageSource(
                world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DAMAGE_TYPE),
                lightning, attacker);
        double radius = MaceItem.SMASH_ATTACK_KNOCKBACK_RADIUS;
        for (LivingEntity nearby : world.getEntitiesOfClass(LivingEntity.class,
                victim.getBoundingBox().inflate(radius),
                candidate -> victim.distanceToSqr(candidate) <= radius * radius
                        && EntityQueries.isValidSummonCombatTarget(attacker, attacker, candidate))) {
            nearby.hurtServer(world, source, damage);
        }
    }

    @Override
    public MapCodec<FallingThunderEnchantmentEffect> codec() {
        return CODEC;
    }
}
