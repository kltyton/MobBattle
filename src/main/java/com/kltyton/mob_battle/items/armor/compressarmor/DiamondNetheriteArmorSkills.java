package com.kltyton.mob_battle.items.armor.compressarmor;

import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

/**
 * COMPRESSED_DIAMOND / COMPRESSED_NETHERITE 套装技能入口：拉扯、冲刺与传送。
 * 钻石与下界合金共用同一套拉扯/冲刺实现，仅参数与标记效果不同；粒子特效
 * 按职责拆分到 {@link DiamondNetheritePullSkill} 与 {@link DiamondNetheriteDashTeleportSkill}。
 */
final class DiamondNetheriteArmorSkills {
    static final Vector3f COLOR_DIAMOND = new Vector3f(0.05F, 0.72F, 1.0F);
    static final Vector3f COLOR_NETHERITE = new Vector3f(0.03F, 0.02F, 0.04F);
    static final Vector3f COLOR_NETHERITE_PURPLE = new Vector3f(0.42F, 0.05F, 0.62F);

    private DiamondNetheriteArmorSkills() {
    }

    /**
     * 拉扯技能：5 格内非队友实体被拉向玩家、受物理/魔法伤害并被附加标记效果；
     * 玩家获得吸收与抗性，结束后进入指定秒数冷却。
     */
    static void runPullSkill(ServerPlayer player, Item cooldownItem, int cooldownSeconds, float attackDamage, float magicDamage,
                             Holder<MobEffect> mark, int absorptionAmplifier, int resistanceAmplifier, int resistanceSeconds) {
        ItemStack cooldownStack = new ItemStack(cooldownItem);
        if (CompressArmorSkillSupport.isCoolingDown(player, cooldownStack, cooldownSeconds)) return;

        ServerLevel world = player.level();
        boolean netherite = mark == ModEffects.NETHERITE_MARK_ENTRY;
        Vector3f color = netherite ? COLOR_NETHERITE : COLOR_DIAMOND;
        Vector3f secondaryColor = netherite ? COLOR_NETHERITE_PURPLE : COLOR_DIAMOND;

        DiamondNetheritePullSkill.spawnPullStartParticles(world, player, color, netherite);

        List<LivingEntity> targets = EntityQueries.getNearbyEntity(player, LivingEntity.class, 5.0, false, EntityQueries.TeamFilter.EXCLUDE_TEAM);

        for (LivingEntity target : targets) {
            Vec3 pull = player.position().subtract(target.position());

            if (pull.lengthSqr() > 0.01) {
                target.setDeltaMovement(pull.normalize().scale(1.25));
                target.hurtMarked = true;
            }

            DiamondNetheritePullSkill.spawnPullLineParticles(world, target.position().add(0.0D, target.getBbHeight() * 0.55D, 0.0D), player.position().add(0.0D, 1.0D, 0.0D), color, netherite);
            CompressArmorSkillSupport.spawnHitSpark(world, target, secondaryColor, netherite);

            if (attackDamage > 0.0F) {
                target.hurtServer(world, player.damageSources().playerAttack(player), attackDamage);
            }

            if (magicDamage > 0.0F) {
                target.invulnerableTime = 0;
                target.hurtServer(world, player.damageSources().indirectMagic(player, player), magicDamage);
            }

            target.addEffect(new MobEffectInstance(mark, 7 * 20, 0, false, false, true), player);
            DiamondNetheritePullSkill.spawnMarkParticles(world, target, color, netherite);
        }

        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 5 * 20, absorptionAmplifier, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, resistanceSeconds * 20, resistanceAmplifier, false, false, true));

        player.getCooldowns().addCooldown(cooldownStack, cooldownSeconds * 20);
    }

    /**
     * 冲刺技能：沿水平视线方向突进至无碰撞终点，沿途攻击并引爆目标的拉扯标记；
     * 未命中标记目标时进入指定秒数冷却，命中标记目标则免冷却。
     */
    static void runDashSkill(ServerPlayer player, Item cooldownItem, int cooldownSeconds, double distance, float attackDamage, float magicDamage,
                             Holder<MobEffect> mark) {
        ItemStack cooldownStack = new ItemStack(cooldownItem);
        if (CompressArmorSkillSupport.isCoolingDown(player, cooldownStack, cooldownSeconds)) return;

        ServerLevel world = player.level();
        Vec3 start = player.position();
        Vec3 direction = CompressArmorSkillSupport.horizontalDirection(player);
        Vec3 end = DiamondNetheriteDashTeleportSkill.findDashEnd(player, direction, distance);
        double dashDistance = start.distanceTo(end);

        boolean netherite = mark == ModEffects.NETHERITE_MARK_ENTRY;
        Vector3f color = netherite ? COLOR_NETHERITE : COLOR_DIAMOND;
        Vector3f secondaryColor = netherite ? COLOR_NETHERITE_PURPLE : COLOR_DIAMOND;

        DiamondNetheriteDashTeleportSkill.spawnDashChargeParticles(world, player, color, netherite);

        AABB attackBox = player.getBoundingBox().expandTowards(direction.scale(dashDistance)).inflate(1.5);
        List<LivingEntity> targets = EntityQueries.getNearbyEntity(
                player,
                LivingEntity.class,
                Object.class,
                dashDistance + 2.0,
                attackBox,
                false,
                EntityQueries.TeamFilter.EXCLUDE_TEAM,
                null,
                null
        );

        boolean markedHit = false;

        for (LivingEntity target : targets) {
            if (netherite) {
                target.invulnerableTime = 0;
            }
            target.hurtServer(world, player.damageSources().playerAttack(player), attackDamage);

            if (magicDamage > 0.0F) {
                target.invulnerableTime = 0;
                target.hurtServer(world, player.damageSources().indirectMagic(player, player), magicDamage);
            }
            if (netherite) {
                target.invulnerableTime = 0;
            }

            CompressArmorSkillSupport.spawnHitSpark(world, target, secondaryColor, netherite);

            if (target.hasEffect(mark)) {
                target.removeEffect(mark);
                markedHit = true;
                DiamondNetheritePullSkill.spawnMarkBreakParticles(world, target, color, netherite);
            }
        }

        DiamondNetheriteDashTeleportSkill.spawnDashTrailParticles(world, start, end, direction, color, netherite);
        DiamondNetheriteDashTeleportSkill.spawnDashSweepBlades(world, start, end, direction, color, netherite);
        DiamondNetheriteDashTeleportSkill.spawnDashEndBurst(world, end, color, netherite);

        double dashSpeed = Math.min(2.2D, Math.max(0.0D, dashDistance * 0.45D));
        player.setDeltaMovement(direction.scale(dashSpeed).add(0.0D, player.getDeltaMovement().y, 0.0D));
        player.hurtMarked = true;

        world.playSound(null, start.x, start.y, start.z, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.1F, netherite ? 0.55F : 1.35F);
        world.playSound(null, end.x, end.y, end.z, SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, netherite ? 0.7F : 0.45F, netherite ? 0.65F : 1.45F);

        if (!markedHit) {
            player.getCooldowns().addCooldown(cooldownStack, cooldownSeconds * 20);
        }
    }

    /**
     * 下界合金传送技能：传送到视线内带下界合金标记的目标处造成高额伤害，
     * 消耗标记并播放双端传送特效，65 秒冷却；无目标时静默返回。
     */
    static void runNetheriteTeleportSkill(ServerPlayer player) {
        ItemStack cooldownItem = new ItemStack(ModBlocks.COMPRESSED_NETHERITE_BLOCK.asItem());
        if (CompressArmorSkillSupport.isCoolingDown(player, cooldownItem, 65)) return;

        LivingEntity target = findMarkedTargetInSight(player, 25.0, ModEffects.NETHERITE_MARK_ENTRY);
        if (target == null) {
            return;
        }

        ServerLevel world = player.level();

        Vec3 from = player.position();
        Vec3 to = target.position();

        DiamondNetheriteDashTeleportSkill.spawnNetheriteTeleportStart(world, player);
        DiamondNetheritePullSkill.spawnPullLineParticles(world, from.add(0.0D, 1.0D, 0.0D), to.add(0.0D, target.getBbHeight() * 0.55D, 0.0D), COLOR_NETHERITE_PURPLE, true);

        player.teleportTo(target.getX(), target.getY(), target.getZ());

        target.hurtServer(world, player.damageSources().playerAttack(player), 160.0F);
        target.removeEffect(ModEffects.NETHERITE_MARK_ENTRY);

        DiamondNetheriteDashTeleportSkill.spawnNetheriteTeleportImpact(world, target);
        DiamondNetheritePullSkill.spawnMarkBreakParticles(world, target, COLOR_NETHERITE, true);

        world.playSound(null, from.x, from.y, from.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.45F);
        world.playSound(null, to.x, to.y, to.z, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.PLAYERS, 0.85F, 0.75F);

        player.getCooldowns().addCooldown(cooldownItem, 65 * 20);
    }

    /**
     * 视线范围内带指定标记效果的最近目标；无标记目标时返回 null。
     */
    private static LivingEntity findMarkedTargetInSight(ServerPlayer player, double range, Holder<MobEffect> mark) {
        return CompressArmorSkillSupport.findTargetInSight(player, range, target -> target.hasEffect(mark));
    }
}
