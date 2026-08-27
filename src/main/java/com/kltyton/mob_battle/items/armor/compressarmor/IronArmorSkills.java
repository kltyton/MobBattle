package com.kltyton.mob_battle.items.armor.compressarmor;

import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import com.kltyton.mob_battle.event.scheduler.ServerTickScheduler;
import com.mojang.math.Transformation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Brightness;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * COMPRESSED_IRON 套装技能：剑显示实体摆姿、两段斩击与斩击特效。
 * 剑显示的生成、姿态插值、延迟任务与伤害数值均与原 Manager 实现逐项一致。
 */
final class IronArmorSkills {
    private static final Vector3f COLOR_IRON = new Vector3f(0.92F, 0.96F, 1.0F);

    private IronArmorSkills() {
    }

    /**
     * 铁套装技能：6 秒冷却；生成剑显示并依次在 3/8/11/15/18 tick 摆姿、
     * 斩击（3、11 tick 各一次）与回收，结束后附加抗性与力量效果。
     */
    static void runIronSkill(ServerPlayer player) {
        ItemStack cooldownItem = new ItemStack(ModItems.COMPRESSED_IRON_SWORD);
        if (CompressArmorSkillSupport.isCoolingDown(player, cooldownItem, 6)) return;

        ServerLevel world = player.level();

        CompressArmorSkillSupport.spawnArmorBurst(world, player.position().add(0.0D, 1.0D, 0.0D), COLOR_IRON, 1.35F, 36, 0.9D);
        CompressArmorSkillSupport.spawnGroundRing(world, player.position(), 2.4D, COLOR_IRON, 72, 0.95F);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_IRON, SoundSource.PLAYERS, 1.0F, 0.75F);

        Display.ItemDisplay swordDisplay = getItemDisplayEntity(world);
        setIronSwordPose(swordDisplay, player, -42.0F, -0.85F, 1.85F, 1.95F);
        world.addFreshEntity(swordDisplay);

        ServerTickScheduler.schedule(world.getServer(), 3, () -> {
            if (!swordDisplay.isRemoved()) {
                setIronSwordPose(swordDisplay, player, -42.0F, 0.85F, 0.65F, 2.15F);
            }

            if (!player.isRemoved()) {
                playIronSlashEffect(player, 0.0F);
                ironSlash(player);
            }
        });

        ServerTickScheduler.schedule(world.getServer(), 8, () -> {
            if (!swordDisplay.isRemoved()) {
                setIronSwordPose(swordDisplay, player, 42.0F, 0.85F, 1.85F, 1.95F);
            }
        });

        ServerTickScheduler.schedule(world.getServer(), 11, () -> {
            if (!swordDisplay.isRemoved()) {
                setIronSwordPose(swordDisplay, player, 42.0F, -0.85F, 0.65F, 2.15F);
            }

            if (!player.isRemoved()) {
                playIronSlashEffect(player, 0.0F);
                ironSlash(player);
            }
        });

        ServerTickScheduler.schedule(world.getServer(), 15, () -> {
            if (!swordDisplay.isRemoved()) {
                setIronSwordPose(swordDisplay, player, 0.0F, 0.0F, 1.25F, 1.35F);
            }
        });

        ServerTickScheduler.schedule(world.getServer(), 18, swordDisplay::discard);
        player.getCooldowns().addCooldown(cooldownItem, 6 * 20);
    }

    /**
     * 构造铁剑显示实体：第三人称右手手持、无重力、无敌、全亮并带光晕。
     */
    private static Display.@NotNull ItemDisplay getItemDisplayEntity(ServerLevel world) {
        Display.ItemDisplay swordDisplay = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, world);
        swordDisplay.setItemStack(new ItemStack(ModItems.COMPRESSED_IRON_SWORD));
        swordDisplay.setItemTransform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
        swordDisplay.setNoGravity(true);
        swordDisplay.setInvulnerable(true);
        swordDisplay.setBrightnessOverride(Brightness.FULL_BRIGHT);
        swordDisplay.setGlowColorOverride(0xD8E8FF);
        swordDisplay.setGlowingTag(true);
        swordDisplay.setViewRange(32.0F);
        swordDisplay.setWidth(3.0F);
        swordDisplay.setHeight(3.0F);
        return swordDisplay;
    }

    /**
     * 设置剑显示在玩家身侧的摆动姿态与插值时长，旋转与缩放参数由调用点传入。
     */
    private static void setIronSwordPose(Display.ItemDisplay swordDisplay, ServerPlayer player, float swingDegrees,
                                         float sideOffset, float heightOffset, float scale) {
        Vec3 forward = CompressArmorSkillSupport.horizontalDirection(player);
        Vec3 right = new Vec3(-forward.z, 0.0, forward.x);

        Vec3 displayPos = player.position()
                .add(forward.scale(1.35))
                .add(right.scale(sideOffset))
                .add(0.0, heightOffset, 0.0);

        swordDisplay.setPos(displayPos.x, displayPos.y, displayPos.z);
        swordDisplay.setYRot(player.getYRot());
        swordDisplay.setXRot(0.0F);
        swordDisplay.setPosRotInterpolationDuration(3);
        swordDisplay.setTransformationInterpolationDuration(3);
        swordDisplay.setTransformationInterpolationDelay(0);
        swordDisplay.setTransformation(new Transformation(
                new Vector3f(0.0F, 0.0F, 0.0F),
                new Quaternionf().rotateXYZ(Mth.DEG_TO_RAD * 65.0F, 0.0F, Mth.DEG_TO_RAD * swingDegrees),
                new Vector3f(scale, scale, scale),
                new Quaternionf()
        ));
    }

    /**
     * 斩击特效：玩家前方生成扫击、dust、暴击、电火花与末地烛粒子，附带弧线与音效。
     */
    private static void playIronSlashEffect(ServerPlayer player, float sideOffset) {
        ServerLevel world = player.level();
        Vec3 forward = CompressArmorSkillSupport.horizontalDirection(player);
        Vec3 right = new Vec3(-forward.z, 0.0, forward.x);

        Vec3 center = player.position()
                .add(forward.scale(1.9))
                .add(right.scale(sideOffset))
                .add(0.0, 1.0, 0.0);

        world.sendParticles(ParticleTypes.SWEEP_ATTACK, center.x, center.y, center.z, 4, 0.55, 0.2, 0.55, 0.0);
        world.sendParticles(CompressArmorSkillSupport.dust(COLOR_IRON, 1.25F), center.x, center.y, center.z, 42, 1.1, 0.55, 1.1, 0.08);
        world.sendParticles(ParticleTypes.CRIT, center.x, center.y, center.z, 24, 1.0, 0.55, 1.0, 0.18);
        world.sendParticles(ParticleTypes.ELECTRIC_SPARK, center.x, center.y + 0.15, center.z, 18, 0.8, 0.35, 0.8, 0.12);
        world.sendParticles(ParticleTypes.END_ROD, center.x, center.y + 0.1, center.z, 12, 0.45, 0.25, 0.45, 0.04);

        CompressArmorSkillSupport.spawnForwardArc(world, player.position().add(0.0D, 1.0D, 0.0D), forward, COLOR_IRON, 2.0D, 95.0D, 32);

        world.playSound(null, center.x, center.y, center.z, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.2F, 0.75F);
        world.playSound(null, center.x, center.y, center.z, SoundEvents.ANVIL_HIT, SoundSource.PLAYERS, 0.55F, 1.45F);
    }

    /**
     * 斩击判定：对 3 格内非队友实体造成 25 点伤害，并给自己附加抗性/力量。
     */
    private static void ironSlash(ServerPlayer player) {
        ServerLevel world = player.level();

        for (LivingEntity target : EntityQueries.getNearbyEntity(player, LivingEntity.class, 3.0, false, EntityQueries.TeamFilter.EXCLUDE_TEAM)) {
            target.hurtServer(world, player.damageSources().playerAttack(player), 25.0F);
            CompressArmorSkillSupport.spawnHitSpark(world, target, COLOR_IRON, false);
        }

        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 3 * 20, 2, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 6 * 20, 2, false, false, true));
    }
}
