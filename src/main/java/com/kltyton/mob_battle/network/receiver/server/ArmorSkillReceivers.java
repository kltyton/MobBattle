package com.kltyton.mob_battle.network.receiver.server;

import com.kltyton.mob_battle.command.CombatLogSystem;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.shield.ShieldEntity;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.items.armor.compressarmor.CompressArmorSkillManager;
import com.kltyton.mob_battle.network.packet.CompressArmorSkillPayload;
import com.kltyton.mob_battle.network.packet.ShieldSpawnPayload;
import com.kltyton.mob_battle.network.packet.ZiJinPayload;
import com.kltyton.mob_battle.items.armor.support.ArmorSetRules;
import com.kltyton.mob_battle.combat.effect.CombatEffectApplier;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import com.kltyton.mob_battle.particle.effect.ParticleEffectEmitter;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

/**
 * 套装技能接收器（服务端）。
 *
 * <p>承载原 {@code ServerPlayNetwork.init()} 中第 11、12、14 位的三个接收器：
 * {@link ShieldSpawnPayload}（翠绿套装护盾）、{@link ZiJinPayload}（紫金套装
 * 技能）、{@link CompressArmorSkillPayload}（压缩护甲技能）。每个接收器都有独立
 * 的 {@code init...} 方法，可被调用方按原注册顺序逐个插入；注意第 13 位
 * {@code PiglinCannonModePayload} 属于
 * {@code HeldItemActionReceivers.initPiglinCannonMode()}，调用方必须在
 * {@code initZiJin()} 与 {@code initCompressArmorSkill()} 之间插入该调用，
 * 才能保持与 HEAD 完全一致的总注册顺序。
 *
 * <p>信任边界：三个接收器都以服务端全套盔甲检查（
 * {@link ArmorSetRules#hasFullArmor}）作为进入门；技能 id 是否有效、冷却、粒子、
 * 伤害等全部语义由服务端技能管理器（{@link CompressArmorSkillManager} 等）裁决，
 * 负载中的数值不作为操作目标。冷却占位物品沿用既有入口（{@code Items.AIR} 与
 * {@code Items.COMMAND_BLOCK_MINECART}）；紫金套 C 技能为 12 秒，X 技能为 15 秒。
 * 本文件不新增权限或速率限制等推测性约束。
 */
public final class ArmorSkillReceivers {
    private static final int MIN_COMPRESS_ARMOR_SKILL_ID = 0;
    private static final int MAX_COMPRESS_ARMOR_SKILL_ID = 2;
    private static final int ZIJIN_C_COOLDOWN_TICKS = 12 * 20;
    private static final int ZIJIN_X_COOLDOWN_TICKS = 15 * 20;
    private static final int EMERALD_SHIELD_COOLDOWN_TICKS = 35 * 20;

    private ArmorSkillReceivers() {
    }

    /**
     * 翠绿套装护盾接收器（原注册顺序第 11 位）。
     *
     * <p>行为与 HEAD 完全一致：仅当服务端判定玩家穿着全套
     * {@link ModMaterial#EMERALD_DIAMOND_ALLOY_INSTANCE} 时生效；冷却判断、
     * {@link ShieldEntity} 生成、冷却写入与音效均保持原样。
     */
    public static void initShieldSpawn() {
        ServerPlayNetworking.registerGlobalReceiver(ShieldSpawnPayload.ID, (payload, context) -> {
            //翠绿套装护盾效果
            ServerPlayer player = context.player();
            ServerLevel world = player.level();
            if (ArmorSetRules.hasFullArmor(player, ModMaterial.EMERALD_DIAMOND_ALLOY_INSTANCE)) {
                ItemStack cooldownItem = Items.AIR.getDefaultInstance();
                if (player.getCooldowns().isOnCooldown(cooldownItem)) {
                    // 获取剩余冷却进度 (0.0 到 1.0 之间的浮点数)
                    float progress = player.getCooldowns().getCooldownPercent(cooldownItem, 0);
                    float remainingSeconds = (progress * EMERALD_SHIELD_COOLDOWN_TICKS) / 20.0F;
                    player.sendOverlayMessage(
                            Component.literal("护盾冷却中！还需等待 " + String.format("%.1f", remainingSeconds) + " 秒")
                                    .withStyle(ChatFormatting.RED)
                    );
                    return;
                }
                ShieldEntity shield = new ShieldEntity(ModEntities.SHIELD, world);
                shield.setPos(player.getX(), player.getY(), player.getZ());
                shield.setOwner(player);
                world.addFreshEntity(shield);
                player.getCooldowns().addCooldown(cooldownItem, EMERALD_SHIELD_COOLDOWN_TICKS);
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ARMOR_EQUIP_DIAMOND, SoundSource.PLAYERS, 1.0F, 1.2F);
            }
        });
    }

    /**
     * 紫金套装技能接收器（原注册顺序第 12 位）。
     *
     * <p>行为与 HEAD 完全一致：仅当服务端判定玩家穿着全套
     * {@link ModMaterial#ZIJIN_ARMOR_INSTANCE} 时生效；技能 0/1 的上印记、爆炸、
     * 魔法/近战伤害与粒子保持原样；C/X 分别使用 12/15 秒冷却。
     */
    public static void initZiJin() {
        ServerPlayNetworking.registerGlobalReceiver(ZiJinPayload.ID, (payload, context) -> {
            //紫金套装效果
            ServerPlayer player = context.player();
            ServerLevel world = player.level();
            int cooldownTicks = zijinCooldownTicks(payload.skill_id());
            if (cooldownTicks < 0) {
                return;
            }
            if (ArmorSetRules.hasFullArmor(player, ModMaterial.ZIJIN_ARMOR_INSTANCE)) {
                    if (payload.skill_id() == 0) {
                        ItemStack cooldownItem = Items.AIR.getDefaultInstance();
                        if (player.getCooldowns().isOnCooldown(cooldownItem)) {
                            // 获取剩余冷却进度 (0.0 到 1.0 之间的浮点数)
                            float progress = player.getCooldowns().getCooldownPercent(cooldownItem, 0);
                            float remainingSeconds = (progress * cooldownTicks) / 20.0F;
                            player.sendOverlayMessage(
                                    Component.literal("套装技能冷却中！还需等待 " + String.format("%.1f", remainingSeconds) + " 秒")
                                            .withStyle(ChatFormatting.RED)
                            );
                            return;
                        }
                        List<LivingEntity> firstRangeTargets = EntityQueries.getNearbyEntity(player, LivingEntity.class, 5.0, false, EntityQueries.TeamFilter.EXCLUDE_TEAM);
                        for (LivingEntity target : firstRangeTargets) {
                            CombatEffectApplier.addPigSpiritMark(target, player, 5);
                        }

                        // 技能0第一段：近距离上印记粒子
                        ParticleEffectEmitter.spawnZiJinSkill0MarkParticles(world, player, firstRangeTargets);

                        List<LivingEntity> secondRangeTargets = EntityQueries.getNearbyEntity(player, LivingEntity.class, 20.0, false, EntityQueries.TeamFilter.EXCLUDE_TEAM);
                        for (LivingEntity target : secondRangeTargets) {
                            if (target.hasEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY)) {
                                MobEffectInstance effect = target.getEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY);
                                int level = effect.getAmplifier() + 1;
                                target.invulnerableTime = 0;
                                // 造成魔法伤害 (等同于等级)
                                target.hurtServer(world, player.damageSources().indirectMagic(player, player), (float) level);
                                // 造成攻击伤害 (等级的 5 倍)
                                // 使用 playerAttack 确保伤害来源被计入玩家
                                target.invulnerableTime = 0;
                                target.hurtServer(world, player.damageSources().playerAttack(player), (float) (level * 5));
                                target.removeEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY);
                            }
                        }
                        ParticleEffectEmitter.spawnZiJinSkill0DetonateParticles(world, player, secondRangeTargets);
                        player.getCooldowns().addCooldown(cooldownItem, cooldownTicks);
                    } else if (payload.skill_id() == 1) {
                        ItemStack cooldownItem = Items.COMMAND_BLOCK_MINECART.getDefaultInstance();
                        if (player.getCooldowns().isOnCooldown(cooldownItem)) {
                            float progress = player.getCooldowns().getCooldownPercent(cooldownItem, 0);
                            float remainingSeconds = (progress * cooldownTicks) / 20.0F;
                            player.sendOverlayMessage(
                                    Component.literal("套装技能冷却中！还需等待 " + String.format("%.1f", remainingSeconds) + " 秒")
                                            .withStyle(ChatFormatting.RED)
                            );
                            return;
                        }
                        List<LivingEntity> targets = EntityQueries.getNearbyEntity(player, LivingEntity.class, 6.0, false, EntityQueries.TeamFilter.EXCLUDE_TEAM);
                        for (LivingEntity target : targets) {
                            CombatEffectApplier.addPigSpiritMark(target, player, 10);
                        }

                        // 技能1：强化上印记粒子
                        ParticleEffectEmitter.spawnZiJinSkill1MarkParticles(world, player, targets);

                        player.getCooldowns().addCooldown(cooldownItem, cooldownTicks);
                    }
            }
        });
    }

    /**
     * 压缩护甲技能接收器（原注册顺序第 14 位）。
     *
     * <p>先拒绝未知技能 id，再记录合法请求并把技能 id 交给
     * {@link CompressArmorSkillManager#handleSkill} 裁决（其中仍包含全套盔甲门、
     * 弹药/冷却/伤害/粒子等全部原语义）。
     */
    public static void initCompressArmorSkill() {
        ServerPlayNetworking.registerGlobalReceiver(CompressArmorSkillPayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            int skillId = payload.skill_id();
            if (!isKnownCompressArmorSkillId(skillId)) {
                return;
            }
            CombatLogSystem.logAction(player, "使用压缩护甲技能 " + skillId);
            CompressArmorSkillManager.handleSkill(player, skillId);
        });
    }

    /**
     * 验证压缩护甲兼容协议 skill_id，必须在日志和技能管理器调用前执行。
     *
     * @param skillId 客户端携带的技能编号：Z=0、X=1、C=2
     * @return 仅当编号属于现有兼容协议 0、1、2 时返回 true
     */
    static boolean isKnownCompressArmorSkillId(int skillId) {
        return skillId >= MIN_COMPRESS_ARMOR_SKILL_ID && skillId <= MAX_COMPRESS_ARMOR_SKILL_ID;
    }

    static int zijinCooldownTicks(int skillId) {
        return switch (skillId) {
            case 0 -> ZIJIN_C_COOLDOWN_TICKS;
            case 1 -> ZIJIN_X_COOLDOWN_TICKS;
            default -> -1;
        };
    }
}
