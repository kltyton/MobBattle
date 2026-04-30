package com.kltyton.mob_battle.items.armor.compressarmor;

import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.bullet.GoldenBulletEntity;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.utils.ArmorUtil;
import com.kltyton.mob_battle.utils.EntityUtil;
import com.kltyton.mob_battle.utils.TaskSchedulerUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CompressArmorSkillManager {
    private static final int SKILL_Z = 0;
    private static final int SKILL_X = 1;
    private static final int SKILL_C = 2;
    private static final String TEXT_GOLD_BULLET_MODE = "message.mob_battle.gold_bullet_mode";
    private static final String TEXT_MISSING_PROJECTILE_ITEM = "message.mob_battle.missing_projectile_item";
    private static final String TEXT_ARMOR_SKILL_COOLING_DOWN = "message.mob_battle.armor_skill_cooling_down";

    private static final GoldBulletMode[] GOLD_BULLET_MODES = new GoldBulletMode[]{
            new GoldBulletMode(Items.GOLD_NUGGET, 5.0F),
            new GoldBulletMode(Items.GOLD_INGOT, 10.0F),
            new GoldBulletMode(Items.GOLD_BLOCK, 35.0F),
            new GoldBulletMode(ModItems.COMPRESSED_GOLD_INGOT, 100.0F),
            new GoldBulletMode(ModBlocks.COMPRESSED_GOLD_BLOCK.asItem(), 200.0F)
    };
    private static final Map<UUID, Integer> GOLD_MODE_INDEX = new HashMap<>();

    public static void handleSkill(ServerPlayerEntity player, int skillId) {
        if (ArmorUtil.hasFullArmor(player, ModMaterial.COMPRESSED_IRON_ARMOR_INSTANCE)) {
            if (skillId == SKILL_C) runIronSkill(player);
            return;
        }
        if (ArmorUtil.hasFullArmor(player, ModMaterial.COMPRESSED_GOLD_ARMOR_INSTANCE)) {
            if (skillId == SKILL_X) switchGoldBulletMode(player);
            if (skillId == SKILL_C) runGoldSkill(player);
            return;
        }
        if (ArmorUtil.hasFullArmor(player, ModMaterial.COMPRESSED_DIAMOND_ARMOR_INSTANCE)) {
            if (skillId == SKILL_X) runPullSkill(player, ModItems.COMPRESSED_DIAMOND, 12, 45.0F, 0.0F, ModEffects.DIAMOND_MARK_ENTRY, 9, 0, 5);
            if (skillId == SKILL_C) runDashSkill(player, ModItems.COMPRESSED_DIAMOND_SWORD, 13, 5.0, 50.0F, 5.0F, ModEffects.DIAMOND_MARK_ENTRY);
            return;
        }
        if (ArmorUtil.hasFullArmor(player, ModMaterial.COMPRESSED_NETHERITE_ARMOR_INSTANCE)) {
            if (skillId == SKILL_Z) runNetheriteTeleportSkill(player);
            if (skillId == SKILL_X) runPullSkill(player, ModItems.COMPRESSED_NETHERITE_INGOT, 12, 100.0F, 5.0F, ModEffects.NETHERITE_MARK_ENTRY, 19, 0, 10);
            if (skillId == SKILL_C) runDashSkill(player, ModItems.COMPRESSED_NETHERITE_SWORD, 13, 7.0, 95.0F, 10.0F, ModEffects.NETHERITE_MARK_ENTRY);
        }
    }

    private static void runIronSkill(ServerPlayerEntity player) {
        ItemStack cooldownItem = new ItemStack(ModItems.COMPRESSED_IRON_SWORD);
        if (isCoolingDown(player, cooldownItem, 6)) return;

        ServerWorld world = player.getWorld();
        Vec3d direction = horizontalDirection(player);
        Vec3d standPos = player.getPos().add(direction.multiply(1.0)).add(0.0, 0.6, 0.0);
        ArmorStandEntity swordStand = new ArmorStandEntity(world, standPos.x, standPos.y, standPos.z);
        swordStand.setInvisible(true);
        swordStand.setNoGravity(true);
        swordStand.setInvulnerable(true);
        swordStand.setShowArms(true);
        swordStand.setHideBasePlate(true);
        swordStand.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.COMPRESSED_IRON_SWORD));
        swordStand.setYaw(player.getYaw());
        swordStand.setRightArmRotation(new EulerAngle(-120.0F, 0.0F, 25.0F));
        world.spawnEntity(swordStand);

        ironSlash(player);
        TaskSchedulerUtil.runLater(4, () -> {
            if (!swordStand.isRemoved()) {
                swordStand.setRightArmRotation(new EulerAngle(30.0F, 0.0F, -45.0F));
            }
        });
        TaskSchedulerUtil.runLater(8, () -> {
            if (!player.isRemoved()) {
                swordStand.setRightArmRotation(new EulerAngle(-120.0F, 0.0F, 25.0F));
                ironSlash(player);
            }
        });
        TaskSchedulerUtil.runLater(12, () -> {
            if (!swordStand.isRemoved()) {
                swordStand.setRightArmRotation(new EulerAngle(30.0F, 0.0F, -45.0F));
            }
        });
        TaskSchedulerUtil.runLater(18, swordStand::discard);
        player.getItemCooldownManager().set(cooldownItem, 6 * 20);
    }

    private static void ironSlash(ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();
        for (LivingEntity target : EntityUtil.getNearbyEntity(player, LivingEntity.class, 3.0, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            target.damage(world, player.getDamageSources().playerAttack(player), 25.0F);
        }
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 3 * 20, 2, false, false, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 6 * 20, 2, false, false, true));
    }

    private static void switchGoldBulletMode(ServerPlayerEntity player) {
        int index = (getGoldModeIndex(player) + 1) % GOLD_BULLET_MODES.length;
        GOLD_MODE_INDEX.put(player.getUuid(), index);
        player.sendMessage(Text.translatable(TEXT_GOLD_BULLET_MODE, new ItemStack(GOLD_BULLET_MODES[index].item()).getName())
                .formatted(Formatting.GOLD), true);
    }

    private static void runGoldSkill(ServerPlayerEntity player) {
        ItemStack cooldownItem = new ItemStack(ModItems.COMPRESSED_GOLD_SWORD);
        if (isCoolingDown(player, cooldownItem, 1)) return;

        GoldBulletMode mode = GOLD_BULLET_MODES[getGoldModeIndex(player)];
        if (!consumeOne(player, mode.item())) {
            player.sendMessage(Text.translatable(TEXT_MISSING_PROJECTILE_ITEM, new ItemStack(mode.item()).getName())
                    .formatted(Formatting.RED), true);
            return;
        }

        ServerWorld world = player.getWorld();
        ItemStack projectileStack = new ItemStack(mode.item());
        GoldenBulletEntity bullet = new GoldenBulletEntity(world, player, projectileStack, Items.BOW.getDefaultStack());
        bullet.setDamage(mode.damage());
        bullet.setTrueDamage(true, false);
        Vec3d rotation = player.getRotationVec(1.0F);
        bullet.setVelocity(rotation.x, rotation.y, rotation.z, 3.5F, 0.0F);
        world.spawnEntity(bullet);
        player.getItemCooldownManager().set(cooldownItem, 20);
    }

    private static void runPullSkill(ServerPlayerEntity player, Item cooldownItem, int cooldownSeconds, float attackDamage, float magicDamage,
                                     RegistryEntry<StatusEffect> mark, int absorptionAmplifier, int resistanceAmplifier, int resistanceSeconds) {
        ItemStack cooldownStack = new ItemStack(cooldownItem);
        if (isCoolingDown(player, cooldownStack, cooldownSeconds)) return;

        ServerWorld world = player.getWorld();
        for (LivingEntity target : EntityUtil.getNearbyEntity(player, LivingEntity.class, 5.0, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            Vec3d pull = player.getPos().subtract(target.getPos());
            if (pull.lengthSquared() > 0.01) {
                target.setVelocity(pull.normalize().multiply(1.25));
                target.velocityModified = true;
            }
            if (attackDamage > 0.0F) {
                target.damage(world, player.getDamageSources().playerAttack(player), attackDamage);
            }
            if (magicDamage > 0.0F) {
                target.damage(world, player.getDamageSources().indirectMagic(player, player), magicDamage);
            }
            target.addStatusEffect(new StatusEffectInstance(mark, 7 * 20, 0, false, false, true), player);
        }
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 5 * 20, absorptionAmplifier, false, false, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, resistanceSeconds * 20, resistanceAmplifier, false, false, true));
        player.getItemCooldownManager().set(cooldownStack, cooldownSeconds * 20);
    }

    private static void runDashSkill(ServerPlayerEntity player, Item cooldownItem, int cooldownSeconds, double distance, float attackDamage, float magicDamage,
                                     RegistryEntry<StatusEffect> mark) {
        ItemStack cooldownStack = new ItemStack(cooldownItem);
        if (isCoolingDown(player, cooldownStack, cooldownSeconds)) return;

        ServerWorld world = player.getWorld();
        Vec3d direction = horizontalDirection(player);
        Box attackBox = player.getBoundingBox().stretch(direction.multiply(distance)).expand(1.5);
        List<LivingEntity> targets = EntityUtil.getNearbyEntity(player, LivingEntity.class, Object.class, distance + 2.0, attackBox, false, EntityUtil.TeamFilter.EXCLUDE_TEAM, null, null);
        boolean markedHit = false;
        for (LivingEntity target : targets) {
            target.damage(world, player.getDamageSources().playerAttack(player), attackDamage);
            if (magicDamage > 0.0F) {
                target.damage(world, player.getDamageSources().indirectMagic(player, player), magicDamage);
            }
            if (target.hasStatusEffect(mark)) {
                target.removeStatusEffect(mark);
                markedHit = true;
            }
        }

        Vec3d end = player.getPos().add(direction.multiply(distance));
        player.requestTeleport(end.x, end.y, end.z);
        player.setVelocity(direction.multiply(1.2));
        player.velocityModified = true;
        if (!markedHit) {
            player.getItemCooldownManager().set(cooldownStack, cooldownSeconds * 20);
        }
    }

    private static void runNetheriteTeleportSkill(ServerPlayerEntity player) {
        ItemStack cooldownItem = new ItemStack(ModBlocks.COMPRESSED_NETHERITE_BLOCK.asItem());
        if (isCoolingDown(player, cooldownItem, 65)) return;

        LivingEntity target = findMarkedTargetInSight(player, 25.0, ModEffects.NETHERITE_MARK_ENTRY);
        if (target == null) {
            return;
        }
        ServerWorld world = player.getWorld();
        player.requestTeleport(target.getX(), target.getY(), target.getZ());
        target.damage(world, player.getDamageSources().playerAttack(player), 160.0F);
        target.removeStatusEffect(ModEffects.NETHERITE_MARK_ENTRY);
        player.getItemCooldownManager().set(cooldownItem, 65 * 20);
    }

    private static LivingEntity findMarkedTargetInSight(ServerPlayerEntity player, double range, RegistryEntry<StatusEffect> mark) {
        Vec3d eye = player.getEyePos();
        Vec3d look = player.getRotationVec(1.0F).normalize();
        Box box = player.getBoundingBox().stretch(look.multiply(range)).expand(2.0);
        LivingEntity best = null;
        double bestDistance = range + 1.0;

        for (LivingEntity target : player.getWorld().getEntitiesByClass(LivingEntity.class, box, target ->
                target != player
                        && target.isAlive()
                        && !target.isSpectator()
                        && !target.isTeammate(player)
                        && target.hasStatusEffect(mark))) {
            Vec3d toTarget = target.getBoundingBox().getCenter().subtract(eye);
            double alongRay = toTarget.dotProduct(look);
            if (alongRay < 0.0 || alongRay > range) {
                continue;
            }
            double distanceToRay = toTarget.subtract(look.multiply(alongRay)).lengthSquared();
            if (distanceToRay <= 2.25 && alongRay < bestDistance) {
                best = target;
                bestDistance = alongRay;
            }
        }
        return best;
    }

    private static boolean isCoolingDown(ServerPlayerEntity player, ItemStack cooldownItem, int cooldownSeconds) {
        if (!player.getItemCooldownManager().isCoolingDown(cooldownItem)) {
            return false;
        }
        float progress = player.getItemCooldownManager().getCooldownProgress(cooldownItem, 0.0F);
        float remainingSeconds = progress * cooldownSeconds;
        player.sendMessage(Text.translatable(TEXT_ARMOR_SKILL_COOLING_DOWN, String.format("%.1f", remainingSeconds))
                .formatted(Formatting.RED), true);
        return true;
    }

    private static int getGoldModeIndex(ServerPlayerEntity player) {
        return GOLD_MODE_INDEX.getOrDefault(player.getUuid(), 0);
    }

    private static boolean consumeOne(ServerPlayerEntity player, Item item) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(item)) {
                stack.decrement(1);
                if (stack.isEmpty()) {
                    player.getInventory().setStack(i, ItemStack.EMPTY);
                }
                return true;
            }
        }
        return false;
    }

    private static Vec3d horizontalDirection(ServerPlayerEntity player) {
        Vec3d direction = player.getRotationVec(1.0F);
        Vec3d horizontal = new Vec3d(direction.x, 0.0, direction.z);
        if (horizontal.lengthSquared() < 0.001) {
            return new Vec3d(0.0, 0.0, 1.0);
        }
        return horizontal.normalize();
    }

    private record GoldBulletMode(Item item, float damage) {
    }
}
