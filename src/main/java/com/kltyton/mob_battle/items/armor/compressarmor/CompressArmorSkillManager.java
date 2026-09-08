package com.kltyton.mob_battle.items.armor.compressarmor;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.items.armor.support.ArmorSetRules;
import net.minecraft.server.level.ServerPlayer;

/**
 * 压缩护甲技能入口的兼容门面。
 * 保留 {@code CompressArmorSkillManager.handleSkill(ServerPlayer, int)} 与
 * {@code CompressArmorSkillManager.tickEcredcultistArmor(ServerPlayer)} 两个公开签名，
 * 供既有调用方无感使用。业务实现已按护甲领域拆分为 {@link EcredcultistArmorSkills}、
 * {@link CopperArmorSkills}、{@link IronArmorSkills}、{@link GoldArmorSkills}、
 * {@link DiamondNetheriteArmorSkills} 与共享工具 {@link CompressArmorSkillSupport}；
 * 本类不再持有常量、玩家状态 Map 或私有方法，只负责按原顺序转发技能请求。
 */
public final class CompressArmorSkillManager {
    private static final int SKILL_Z = 0;
    private static final int SKILL_X = 1;
    private static final int SKILL_C = 2;

    private CompressArmorSkillManager() {
    }

    /**
     * 根据玩家服务端实际穿戴的完整套装分派技能按键。
     *
     * @param player 服务端玩家
     * @param skillId 兼容协议中的技能编号：Z=0、X=1、C=2
     */
    public static void handleSkill(ServerPlayer player, int skillId) {
        if (ArmorSetRules.hasFullArmor(player, ModMaterial.ECREDCULTIST_INSTANCE)) {
            tickEcredcultistArmor(player);
            if (skillId == SKILL_C) EcredcultistArmorSkills.runEcredcultistFireball(player);
            return;
        }

        if (ArmorSetRules.hasFullArmor(player, ModMaterial.COMPRESSED_COPPER_ARMOR_INSTANCE)) {
            if (skillId == SKILL_X) CopperArmorSkills.runCopperLightningSkill(player);
            if (skillId == SKILL_C) CopperArmorSkills.runCopperKickSkill(player);
            return;
        }

        if (ArmorSetRules.hasFullArmor(player, ModMaterial.COMPRESSED_IRON_ARMOR_INSTANCE)) {
            if (skillId == SKILL_C) IronArmorSkills.runIronSkill(player);
            return;
        }

        if (ArmorSetRules.hasFullArmor(player, ModMaterial.COMPRESSED_GOLD_ARMOR_INSTANCE)) {
            if (skillId == SKILL_X) GoldArmorSkills.switchGoldBulletMode(player);
            if (skillId == SKILL_C) GoldArmorSkills.runGoldSkill(player);
            return;
        }

        if (ArmorSetRules.hasFullArmor(player, ModMaterial.COMPRESSED_DIAMOND_ARMOR_INSTANCE)) {
            if (skillId == SKILL_X) {
                DiamondNetheriteArmorSkills.runPullSkill(
                        player,
                        ModItems.COMPRESSED_DIAMOND,
                        12,
                        45.0F,
                        0.0F,
                        ModEffects.DIAMOND_MARK_ENTRY,
                        9,
                        0,
                        5
                );
            }

            if (skillId == SKILL_C) {
                DiamondNetheriteArmorSkills.runDashSkill(
                        player,
                        ModItems.COMPRESSED_DIAMOND_SWORD,
                        13,
                        5.0,
                        50.0F,
                        15.0F,
                        ModEffects.DIAMOND_MARK_ENTRY
                );
            }
            return;
        }

        if (ArmorSetRules.hasFullArmor(player, ModMaterial.COMPRESSED_NETHERITE_ARMOR_INSTANCE)) {
            if (skillId == SKILL_Z) DiamondNetheriteArmorSkills.runNetheriteTeleportSkill(player);

            if (skillId == SKILL_X) {
                DiamondNetheriteArmorSkills.runPullSkill(
                        player,
                        ModItems.COMPRESSED_NETHERITE_INGOT,
                        12,
                        100.0F,
                        5.0F,
                        ModEffects.NETHERITE_MARK_ENTRY,
                        19,
                        0,
                        10
                );
            }

            if (skillId == SKILL_C) {
                DiamondNetheriteArmorSkills.runDashSkill(
                        player,
                        ModItems.COMPRESSED_NETHERITE_SWORD,
                        13,
                        7.0,
                        300.0F,
                        20.0F,
                        ModEffects.NETHERITE_MARK_ENTRY
                );
            }
        }
    }

    /**
     * 兼容旧调用方的秘教套装弹药恢复 tick 入口。
     */
    public static void tickEcredcultistArmor(ServerPlayer player) {
        EcredcultistArmorSkills.tickEcredcultistArmor(player);
    }
}
