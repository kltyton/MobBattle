package com.kltyton.mob_battle.items.armor.compressarmor;

import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.entity.bullet.GoldenBulletEntity;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.event.scheduler.ServerTickScheduler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * COMPRESSED_GOLD 套装技能：弹药模式切换、子弹消耗与射击、枪口/弹道粒子。
 * 模式数组与按 MinecraftServer identity、玩家 UUID 索引的模式状态常驻内存，
 * 射击消耗以玩家服务端库存为准。
 */
final class GoldArmorSkills {
    private static final String TEXT_GOLD_BULLET_MODE = "message.mob_battle.gold_bullet_mode";
    private static final String TEXT_MISSING_PROJECTILE_ITEM = "message.mob_battle.missing_projectile_item";

    private static final Vector3f COLOR_GOLD = new Vector3f(1.0F, 0.72F, 0.12F);

    private static final GoldBulletMode[] GOLD_BULLET_MODES = new GoldBulletMode[]{
            new GoldBulletMode(Items.GOLD_NUGGET, 5.0F),
            new GoldBulletMode(Items.GOLD_INGOT, 10.0F),
            new GoldBulletMode(Items.GOLD_BLOCK, 35.0F),
            new GoldBulletMode(ModItems.COMPRESSED_GOLD_INGOT, 100.0F),
            new GoldBulletMode(ModBlocks.COMPRESSED_GOLD_BLOCK.asItem(), 200.0F)
    };

    private static final Map<MinecraftServer, Map<UUID, Integer>> GOLD_MODE_INDEX = new IdentityHashMap<>();

    private GoldArmorSkills() {
    }

    /**
     * 释放指定玩家的模式索引状态。玩家断线时调用，防止已离线玩家的 UUID
     * 残留在静态 Map 中。
     *
     * @param server 目标玩家所属服务器
     * @param playerId 目标玩家 UUID
     */
    static void clearPlayer(MinecraftServer server, UUID playerId) {
        Map<UUID, Integer> playerStates = GOLD_MODE_INDEX.get(server);
        if (playerStates != null) {
            playerStates.remove(playerId);
            if (playerStates.isEmpty()) {
                GOLD_MODE_INDEX.remove(server);
            }
        }
    }

    /**
     * 清空指定 MinecraftServer 的全部模式索引状态。服务器停止时调用，避免跨会话复用旧玩家状态。
     *
     * @param server 目标服务器
     */
    static void clearAll(MinecraftServer server) {
        GOLD_MODE_INDEX.remove(server);
    }

    /**
     * 切换弹药模式：模式索引循环 +1，播放金色爆发/圆环/烟花并显示当前模式物品名 HUD。
     */
    static void switchGoldBulletMode(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        Map<UUID, Integer> modeIndices = stateFor(server);
        int index = (getGoldModeIndex(player) + 1) % GOLD_BULLET_MODES.length;
        modeIndices.put(player.getUUID(), index);

        ServerLevel world = player.level();
        CompressArmorSkillSupport.spawnArmorBurst(world, player.position().add(0.0D, 1.0D, 0.0D), COLOR_GOLD, 1.15F, 28, 0.7D);
        CompressArmorSkillSupport.spawnGroundRing(world, player.position(), 1.8D, COLOR_GOLD, 48, 0.8F);
        world.sendParticles(ParticleTypes.FIREWORK, player.getX(), player.getY(0.65D), player.getZ(), 18, 0.45, 0.5, 0.45, 0.08);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.PLAYERS, 0.8F, 1.6F);

        player.sendOverlayMessage(Component.translatable(TEXT_GOLD_BULLET_MODE, new ItemStack(GOLD_BULLET_MODES[index].item()).getHoverName())
                .withStyle(ChatFormatting.GOLD));
    }

    /**
     * 射击技能：1 秒冷却，按当前模式消耗一发弹药生成金色子弹并播放枪口/弹道特效。
     */
    static void runGoldSkill(ServerPlayer player) {
        ItemStack cooldownItem = new ItemStack(ModItems.COMPRESSED_GOLD_SWORD);
        if (CompressArmorSkillSupport.isCoolingDown(player, cooldownItem, 1)) return;

        GoldBulletMode mode = GOLD_BULLET_MODES[getGoldModeIndex(player)];
        if (!consumeOne(player, mode.item())) {
            player.sendOverlayMessage(Component.translatable(TEXT_MISSING_PROJECTILE_ITEM, new ItemStack(mode.item()).getHoverName())
                    .withStyle(ChatFormatting.RED));
            return;
        }

        ServerLevel world = player.level();
        Vec3 rotation = player.getViewVector(1.0F);
        Vec3 muzzle = player.getEyePosition().add(rotation.normalize().scale(0.8D));

        spawnGoldMuzzleFlash(world, player, muzzle, rotation);

        ItemStack projectileStack = new ItemStack(mode.item());
        GoldenBulletEntity bullet = new GoldenBulletEntity(world, player, projectileStack, Items.BOW.getDefaultInstance());
        bullet.setBaseDamage(mode.damage());
        bullet.setTrueDamage(true, false);
        bullet.shoot(rotation.x, rotation.y, rotation.z, 3.5F, 0.0F);
        world.addFreshEntity(bullet);

        spawnGoldBulletTrail(world, player, muzzle, rotation, mode.damage());

        player.getCooldowns().addCooldown(cooldownItem, 20);
    }

    private static int getGoldModeIndex(ServerPlayer player) {
        Map<UUID, Integer> modeIndices = GOLD_MODE_INDEX.get(player.level().getServer());
        return modeIndices == null ? 0 : modeIndices.getOrDefault(player.getUUID(), 0);
    }

    private static Map<UUID, Integer> stateFor(MinecraftServer server) {
        return GOLD_MODE_INDEX.computeIfAbsent(server, ignored -> new HashMap<>());
    }

    /**
     * 从玩家服务端库存消耗第一个匹配物品，空栈时写回 EMPTY；未找到返回 false。
     */
    private static boolean consumeOne(ServerPlayer player, Item item) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (stack.is(item)) {
                stack.shrink(1);

                if (stack.isEmpty()) {
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }

                return true;
            }
        }

        return false;
    }

    /**
     * 枪口闪光：金色 dust/烟花/火焰粒子 + 前方小弧线 + 两种音效。
     */
    private static void spawnGoldMuzzleFlash(ServerLevel world, ServerPlayer player, Vec3 muzzle, Vec3 direction) {
        world.sendParticles(CompressArmorSkillSupport.dust(COLOR_GOLD, 1.35F), muzzle.x, muzzle.y, muzzle.z, 34, 0.25D, 0.25D, 0.25D, 0.08D);
        world.sendParticles(ParticleTypes.FIREWORK, muzzle.x, muzzle.y, muzzle.z, 18, 0.18D, 0.18D, 0.18D, 0.12D);
        world.sendParticles(ParticleTypes.FLAME, muzzle.x, muzzle.y, muzzle.z, 12, 0.12D, 0.12D, 0.12D, 0.04D);

        CompressArmorSkillSupport.spawnForwardArc(world, player.position().add(0.0D, 1.35D, 0.0D), new Vec3(direction.x, 0.0D, direction.z), COLOR_GOLD, 1.2D, 50.0D, 18);

        world.playSound(null, muzzle.x, muzzle.y, muzzle.z, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.85F, 1.45F);
        world.playSound(null, muzzle.x, muzzle.y, muzzle.z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.55F, 1.8F);
    }

    /**
     * 弹道尾迹：按伤害档位确定点数，逐 tick 沿飞行方向撒金色 dust/烟花粒子。
     */
    private static void spawnGoldBulletTrail(ServerLevel world, ServerPlayer player, Vec3 muzzle, Vec3 direction, float damage) {
        Vec3 dir = direction.normalize();
        int points = damage >= 100.0F ? 18 : damage >= 35.0F ? 14 : 10;

        for (int i = 1; i <= points; i++) {
            int delay = i;
            double distance = 0.55D * i;

            ServerTickScheduler.schedule(world.getServer(), delay, () -> {
                if (player.isRemoved()) {
                    return;
                }

                Vec3 pos = muzzle.add(dir.scale(distance));

                world.sendParticles(CompressArmorSkillSupport.dust(COLOR_GOLD, 1.0F), pos.x, pos.y, pos.z, 8, 0.08D, 0.08D, 0.08D, 0.02D);
                world.sendParticles(ParticleTypes.FIREWORK, pos.x, pos.y, pos.z, 2, 0.04D, 0.04D, 0.04D, 0.02D);
            });
        }
    }

    private record GoldBulletMode(Item item, float damage) {
    }
}
