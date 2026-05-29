package com.kltyton.mob_battle.items.armor.compressarmor;

import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.bullet.GoldenBulletEntity;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.utils.ArmorUtil;
import com.kltyton.mob_battle.utils.EntityUtil;
import com.kltyton.mob_battle.utils.TaskSchedulerUtil;
import com.mojang.math.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Brightness;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CompressArmorSkillManager {
    private static final int SKILL_Z = 0;
    private static final int SKILL_X = 1;
    private static final int SKILL_C = 2;

    private static final String TEXT_GOLD_BULLET_MODE = "message.mob_battle.gold_bullet_mode";
    private static final String TEXT_MISSING_PROJECTILE_ITEM = "message.mob_battle.missing_projectile_item";
    private static final String TEXT_ARMOR_SKILL_COOLING_DOWN = "message.mob_battle.armor_skill_cooling_down";

    private static final Vector3f COLOR_IRON = new Vector3f(0.92F, 0.96F, 1.0F);
    private static final Vector3f COLOR_GOLD = new Vector3f(1.0F, 0.72F, 0.12F);
    private static final Vector3f COLOR_DIAMOND = new Vector3f(0.05F, 0.72F, 1.0F);
    private static final Vector3f COLOR_NETHERITE = new Vector3f(0.03F, 0.02F, 0.04F);
    private static final Vector3f COLOR_NETHERITE_PURPLE = new Vector3f(0.42F, 0.05F, 0.62F);

    private static final GoldBulletMode[] GOLD_BULLET_MODES = new GoldBulletMode[]{
            new GoldBulletMode(Items.GOLD_NUGGET, 5.0F),
            new GoldBulletMode(Items.GOLD_INGOT, 10.0F),
            new GoldBulletMode(Items.GOLD_BLOCK, 35.0F),
            new GoldBulletMode(ModItems.COMPRESSED_GOLD_INGOT, 100.0F),
            new GoldBulletMode(ModBlocks.COMPRESSED_GOLD_BLOCK.asItem(), 200.0F)
    };

    private static final Map<UUID, Integer> GOLD_MODE_INDEX = new HashMap<>();

    public static void handleSkill(ServerPlayer player, int skillId) {
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
            if (skillId == SKILL_X) {
                runPullSkill(
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
                runDashSkill(
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

        if (ArmorUtil.hasFullArmor(player, ModMaterial.COMPRESSED_NETHERITE_ARMOR_INSTANCE)) {
            if (skillId == SKILL_Z) runNetheriteTeleportSkill(player);

            if (skillId == SKILL_X) {
                runPullSkill(
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
                runDashSkill(
                        player,
                        ModItems.COMPRESSED_NETHERITE_SWORD,
                        13,
                        7.0,
                        150.0F,
                        20.0F,
                        ModEffects.NETHERITE_MARK_ENTRY
                );
            }
        }
    }

    private static void runIronSkill(ServerPlayer player) {
        ItemStack cooldownItem = new ItemStack(ModItems.COMPRESSED_IRON_SWORD);
        if (isCoolingDown(player, cooldownItem, 6)) return;

        ServerLevel world = player.level();

        spawnArmorBurst(world, player.position().add(0.0D, 1.0D, 0.0D), COLOR_IRON, 1.35F, 36, 0.9D);
        spawnGroundRing(world, player.position(), 2.4D, COLOR_IRON, 72, 0.95F);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_IRON, SoundSource.PLAYERS, 1.0F, 0.75F);

        Display.ItemDisplay swordDisplay = getItemDisplayEntity(world);
        setIronSwordPose(swordDisplay, player, -42.0F, -0.85F, 1.85F, 1.95F);
        world.addFreshEntity(swordDisplay);

        TaskSchedulerUtil.runLater(3, () -> {
            if (!swordDisplay.isRemoved()) {
                setIronSwordPose(swordDisplay, player, -42.0F, 0.85F, 0.65F, 2.15F);
            }

            if (!player.isRemoved()) {
                playIronSlashEffect(player, 0.0F);
                ironSlash(player);
            }
        });

        TaskSchedulerUtil.runLater(8, () -> {
            if (!swordDisplay.isRemoved()) {
                setIronSwordPose(swordDisplay, player, 42.0F, 0.85F, 1.85F, 1.95F);
            }
        });

        TaskSchedulerUtil.runLater(11, () -> {
            if (!swordDisplay.isRemoved()) {
                setIronSwordPose(swordDisplay, player, 42.0F, -0.85F, 0.65F, 2.15F);
            }

            if (!player.isRemoved()) {
                playIronSlashEffect(player, 0.0F);
                ironSlash(player);
            }
        });

        TaskSchedulerUtil.runLater(15, () -> {
            if (!swordDisplay.isRemoved()) {
                setIronSwordPose(swordDisplay, player, 0.0F, 0.0F, 1.25F, 1.35F);
            }
        });

        TaskSchedulerUtil.runLater(18, swordDisplay::discard);
        player.getCooldowns().addCooldown(cooldownItem, 6 * 20);
    }

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

    private static void setIronSwordPose(Display.ItemDisplay swordDisplay, ServerPlayer player, float swingDegrees,
                                         float sideOffset, float heightOffset, float scale) {
        Vec3 forward = horizontalDirection(player);
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

    private static void playIronSlashEffect(ServerPlayer player, float sideOffset) {
        ServerLevel world = player.level();
        Vec3 forward = horizontalDirection(player);
        Vec3 right = new Vec3(-forward.z, 0.0, forward.x);

        Vec3 center = player.position()
                .add(forward.scale(1.9))
                .add(right.scale(sideOffset))
                .add(0.0, 1.0, 0.0);

        world.sendParticles(ParticleTypes.SWEEP_ATTACK, center.x, center.y, center.z, 4, 0.55, 0.2, 0.55, 0.0);
        world.sendParticles(dust(COLOR_IRON, 1.25F), center.x, center.y, center.z, 42, 1.1, 0.55, 1.1, 0.08);
        world.sendParticles(ParticleTypes.CRIT, center.x, center.y, center.z, 24, 1.0, 0.55, 1.0, 0.18);
        world.sendParticles(ParticleTypes.ELECTRIC_SPARK, center.x, center.y + 0.15, center.z, 18, 0.8, 0.35, 0.8, 0.12);
        world.sendParticles(ParticleTypes.END_ROD, center.x, center.y + 0.1, center.z, 12, 0.45, 0.25, 0.45, 0.04);

        spawnForwardArc(world, player.position().add(0.0D, 1.0D, 0.0D), forward, COLOR_IRON, 2.0D, 95.0D, 32);

        world.playSound(null, center.x, center.y, center.z, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.2F, 0.75F);
        world.playSound(null, center.x, center.y, center.z, SoundEvents.ANVIL_HIT, SoundSource.PLAYERS, 0.55F, 1.45F);
    }

    private static void ironSlash(ServerPlayer player) {
        ServerLevel world = player.level();

        for (LivingEntity target : EntityUtil.getNearbyEntity(player, LivingEntity.class, 3.0, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            target.hurtServer(world, player.damageSources().playerAttack(player), 25.0F);
            spawnHitSpark(world, target, COLOR_IRON, true);
        }

        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 3 * 20, 2, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 6 * 20, 2, false, false, true));
    }

    private static void switchGoldBulletMode(ServerPlayer player) {
        int index = (getGoldModeIndex(player) + 1) % GOLD_BULLET_MODES.length;
        GOLD_MODE_INDEX.put(player.getUUID(), index);

        ServerLevel world = player.level();
        spawnArmorBurst(world, player.position().add(0.0D, 1.0D, 0.0D), COLOR_GOLD, 1.15F, 28, 0.7D);
        spawnGroundRing(world, player.position(), 1.8D, COLOR_GOLD, 48, 0.8F);
        world.sendParticles(ParticleTypes.FIREWORK, player.getX(), player.getY(0.65D), player.getZ(), 18, 0.45, 0.5, 0.45, 0.08);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.PLAYERS, 0.8F, 1.6F);

        player.displayClientMessage(Component.translatable(TEXT_GOLD_BULLET_MODE, new ItemStack(GOLD_BULLET_MODES[index].item()).getHoverName())
                .withStyle(ChatFormatting.GOLD), true);
    }

    private static void runGoldSkill(ServerPlayer player) {
        ItemStack cooldownItem = new ItemStack(ModItems.COMPRESSED_GOLD_SWORD);
        if (isCoolingDown(player, cooldownItem, 1)) return;

        GoldBulletMode mode = GOLD_BULLET_MODES[getGoldModeIndex(player)];
        if (!consumeOne(player, mode.item())) {
            player.displayClientMessage(Component.translatable(TEXT_MISSING_PROJECTILE_ITEM, new ItemStack(mode.item()).getHoverName())
                    .withStyle(ChatFormatting.RED), true);
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

    private static void runPullSkill(ServerPlayer player, Item cooldownItem, int cooldownSeconds, float attackDamage, float magicDamage,
                                     Holder<MobEffect> mark, int absorptionAmplifier, int resistanceAmplifier, int resistanceSeconds) {
        ItemStack cooldownStack = new ItemStack(cooldownItem);
        if (isCoolingDown(player, cooldownStack, cooldownSeconds)) return;

        ServerLevel world = player.level();
        boolean netherite = mark == ModEffects.NETHERITE_MARK_ENTRY;
        Vector3f color = netherite ? COLOR_NETHERITE : COLOR_DIAMOND;
        Vector3f secondaryColor = netherite ? COLOR_NETHERITE_PURPLE : COLOR_DIAMOND;

        spawnPullStartParticles(world, player, color, netherite);

        List<LivingEntity> targets = EntityUtil.getNearbyEntity(player, LivingEntity.class, 5.0, false, EntityUtil.TeamFilter.EXCLUDE_TEAM);

        for (LivingEntity target : targets) {
            Vec3 pull = player.position().subtract(target.position());

            if (pull.lengthSqr() > 0.01) {
                target.setDeltaMovement(pull.normalize().scale(1.25));
                target.hurtMarked = true;
            }

            spawnPullLineParticles(world, target.position().add(0.0D, target.getBbHeight() * 0.55D, 0.0D), player.position().add(0.0D, 1.0D, 0.0D), color, netherite);
            spawnHitSpark(world, target, secondaryColor, false);

            if (attackDamage > 0.0F) {
                target.hurtServer(world, player.damageSources().playerAttack(player), attackDamage);
            }

            if (magicDamage > 0.0F) {
                target.invulnerableTime = 0;
                target.hurtServer(world, player.damageSources().indirectMagic(player, player), magicDamage);
            }

            target.addEffect(new MobEffectInstance(mark, 7 * 20, 0, false, false, true), player);
            spawnMarkParticles(world, target, color, netherite);
        }

        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 5 * 20, absorptionAmplifier, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, resistanceSeconds * 20, resistanceAmplifier, false, false, true));

        player.getCooldowns().addCooldown(cooldownStack, cooldownSeconds * 20);
    }

    private static void runDashSkill(ServerPlayer player, Item cooldownItem, int cooldownSeconds, double distance, float attackDamage, float magicDamage,
                                     Holder<MobEffect> mark) {
        ItemStack cooldownStack = new ItemStack(cooldownItem);
        if (isCoolingDown(player, cooldownStack, cooldownSeconds)) return;

        ServerLevel world = player.level();
        Vec3 start = player.position();
        Vec3 direction = horizontalDirection(player);
        Vec3 end = findDashEnd(player, direction, distance);
        double dashDistance = start.distanceTo(end);

        boolean netherite = mark == ModEffects.NETHERITE_MARK_ENTRY;
        Vector3f color = netherite ? COLOR_NETHERITE : COLOR_DIAMOND;
        Vector3f secondaryColor = netherite ? COLOR_NETHERITE_PURPLE : COLOR_DIAMOND;

        spawnDashChargeParticles(world, player, color, netherite);

        AABB attackBox = player.getBoundingBox().expandTowards(direction.scale(dashDistance)).inflate(1.5);
        List<LivingEntity> targets = EntityUtil.getNearbyEntity(
                player,
                LivingEntity.class,
                Object.class,
                dashDistance + 2.0,
                attackBox,
                false,
                EntityUtil.TeamFilter.EXCLUDE_TEAM,
                null,
                null
        );

        boolean markedHit = false;

        for (LivingEntity target : targets) {
            target.hurtServer(world, player.damageSources().playerAttack(player), attackDamage);

            if (magicDamage > 0.0F) {
                target.invulnerableTime = 0;
                target.hurtServer(world, player.damageSources().indirectMagic(player, player), magicDamage);
            }

            spawnHitSpark(world, target, secondaryColor, true);

            if (target.hasEffect(mark)) {
                target.removeEffect(mark);
                markedHit = true;
                spawnMarkBreakParticles(world, target, color, netherite);
            }
        }

        spawnDashTrailParticles(world, start, end, direction, color, netherite);
        spawnDashSweepBlades(world, start, end, direction, color, netherite);
        spawnDashEndBurst(world, end, color, netherite);

        double dashSpeed = Math.min(2.2D, Math.max(0.0D, dashDistance * 0.45D));
        player.setDeltaMovement(direction.scale(dashSpeed).add(0.0D, player.getDeltaMovement().y, 0.0D));
        player.hurtMarked = true;

        world.playSound(null, start.x, start.y, start.z, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.1F, netherite ? 0.55F : 1.35F);
        world.playSound(null, end.x, end.y, end.z, SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, netherite ? 0.7F : 0.45F, netherite ? 0.65F : 1.45F);

        if (!markedHit) {
            player.getCooldowns().addCooldown(cooldownStack, cooldownSeconds * 20);
        }
    }

    private static Vec3 findDashEnd(ServerPlayer player, Vec3 direction, double distance) {
        ServerLevel world = player.level();
        Vec3 start = player.position();
        Vec3 safeEnd = start;
        int steps = Math.max(1, Mth.ceil(distance / 0.25D));

        for (int i = 1; i <= steps; i++) {
            double stepDistance = distance * i / steps;
            Vec3 candidate = start.add(direction.scale(stepDistance));
            AABB candidateBox = player.getBoundingBox().move(candidate.subtract(start));
            if (!world.noCollision(player, candidateBox)) {
                break;
            }
            safeEnd = candidate;
        }

        return safeEnd;
    }

    private static void runNetheriteTeleportSkill(ServerPlayer player) {
        ItemStack cooldownItem = new ItemStack(ModBlocks.COMPRESSED_NETHERITE_BLOCK.asItem());
        if (isCoolingDown(player, cooldownItem, 65)) return;

        LivingEntity target = findMarkedTargetInSight(player, 25.0, ModEffects.NETHERITE_MARK_ENTRY);
        if (target == null) {
            return;
        }

        ServerLevel world = player.level();

        Vec3 from = player.position();
        Vec3 to = target.position();

        spawnNetheriteTeleportStart(world, player);
        spawnPullLineParticles(world, from.add(0.0D, 1.0D, 0.0D), to.add(0.0D, target.getBbHeight() * 0.55D, 0.0D), COLOR_NETHERITE_PURPLE, true);

        player.teleportTo(target.getX(), target.getY(), target.getZ());

        target.hurtServer(world, player.damageSources().playerAttack(player), 160.0F);
        target.removeEffect(ModEffects.NETHERITE_MARK_ENTRY);

        spawnNetheriteTeleportImpact(world, target);
        spawnMarkBreakParticles(world, target, COLOR_NETHERITE, true);

        world.playSound(null, from.x, from.y, from.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.45F);
        world.playSound(null, to.x, to.y, to.z, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.PLAYERS, 0.85F, 0.75F);

        player.getCooldowns().addCooldown(cooldownItem, 65 * 20);
    }

    private static LivingEntity findMarkedTargetInSight(ServerPlayer player, double range, Holder<MobEffect> mark) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F).normalize();
        AABB box = player.getBoundingBox().expandTowards(look.scale(range)).inflate(2.0);
        LivingEntity best = null;
        double bestDistance = range + 1.0;

        for (LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class, box, target ->
                target != player
                        && target.isAlive()
                        && !target.isSpectator()
                        && !(target instanceof net.minecraft.world.entity.player.Player targetPlayer && targetPlayer.isCreative())
                        && !target.isAlliedTo(player)
                        && target.hasEffect(mark))) {
            Vec3 toTarget = target.getBoundingBox().getCenter().subtract(eye);
            double alongRay = toTarget.dot(look);

            if (alongRay < 0.0 || alongRay > range) {
                continue;
            }

            double distanceToRay = toTarget.subtract(look.scale(alongRay)).lengthSqr();

            if (distanceToRay <= 2.25 && alongRay < bestDistance) {
                best = target;
                bestDistance = alongRay;
            }
        }

        return best;
    }

    private static boolean isCoolingDown(ServerPlayer player, ItemStack cooldownItem, int cooldownSeconds) {
        if (!player.getCooldowns().isOnCooldown(cooldownItem)) {
            return false;
        }

        float progress = player.getCooldowns().getCooldownPercent(cooldownItem, 0.0F);
        float remainingSeconds = progress * cooldownSeconds;

        player.displayClientMessage(Component.translatable(TEXT_ARMOR_SKILL_COOLING_DOWN, String.format("%.1f", remainingSeconds))
                .withStyle(ChatFormatting.RED), true);

        return true;
    }

    private static int getGoldModeIndex(ServerPlayer player) {
        return GOLD_MODE_INDEX.getOrDefault(player.getUUID(), 0);
    }

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

    private static Vec3 horizontalDirection(ServerPlayer player) {
        Vec3 direction = player.getViewVector(1.0F);
        Vec3 horizontal = new Vec3(direction.x, 0.0, direction.z);

        if (horizontal.lengthSqr() < 0.001) {
            return new Vec3(0.0, 0.0, 1.0);
        }

        return horizontal.normalize();
    }

    private static ParticleOptions dust(Vector3f color, float scale) {
        return new DustParticleOptions(
                ((int) (color.x * 255.0F) << 16)
                        | ((int) (color.y * 255.0F) << 8)
                        | (int) (color.z * 255.0F),
                scale
        );
    }

    private static void spawnArmorBurst(ServerLevel world, Vec3 center, Vector3f color, float scale, int count, double spread) {
        world.sendParticles(dust(color, scale), center.x, center.y, center.z, count, spread, spread * 0.75D, spread, 0.08D);
        world.sendParticles(ParticleTypes.END_ROD, center.x, center.y, center.z, Math.max(6, count / 4), spread * 0.35D, spread * 0.55D, spread * 0.35D, 0.035D);
    }

    private static void spawnGroundRing(ServerLevel world, Vec3 center, double radius, Vector3f color, int points, float scale) {
        double y = center.y + 0.08D;

        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0D * i / points;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;

            world.sendParticles(dust(color, scale), x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);

            if (i % 6 == 0) {
                world.sendParticles(ParticleTypes.CRIT, x, y + 0.08D, z, 1, 0.02D, 0.02D, 0.02D, 0.03D);
            }
        }
    }

    private static void spawnForwardArc(ServerLevel world, Vec3 origin, Vec3 forward, Vector3f color, double radius, double arcDegrees, int points) {
        Vec3 horizontal = new Vec3(forward.x, 0.0D, forward.z).normalize();
        double baseAngle = Math.atan2(horizontal.z, horizontal.x);
        double halfArc = Math.toRadians(arcDegrees * 0.5D);

        for (int i = 0; i < points; i++) {
            double progress = points <= 1 ? 0.5D : (double) i / (double) (points - 1);
            double angle = baseAngle - halfArc + halfArc * 2.0D * progress;
            double x = origin.x + Math.cos(angle) * radius;
            double z = origin.z + Math.sin(angle) * radius;

            world.sendParticles(dust(color, 1.15F), x, origin.y, z, 1, 0.02D, 0.02D, 0.02D, 0.01D);
        }
    }

    private static void spawnHitSpark(ServerLevel world, LivingEntity target, Vector3f color, boolean heavy) {
        double x = target.getX();
        double y = target.getY(0.55D);
        double z = target.getZ();

        world.sendParticles(dust(color, heavy ? 1.35F : 1.0F), x, y, z, heavy ? 30 : 16, 0.35D, 0.45D, 0.35D, 0.07D);
        world.sendParticles(ParticleTypes.CRIT, x, y, z, heavy ? 18 : 8, 0.32D, 0.38D, 0.32D, 0.12D);

        if (heavy) {
            world.sendParticles(ParticleTypes.DAMAGE_INDICATOR, x, y + 0.2D, z, 10, 0.32D, 0.32D, 0.32D, 0.12D);
        }
    }

    private static void spawnGoldMuzzleFlash(ServerLevel world, ServerPlayer player, Vec3 muzzle, Vec3 direction) {
        world.sendParticles(dust(COLOR_GOLD, 1.35F), muzzle.x, muzzle.y, muzzle.z, 34, 0.25D, 0.25D, 0.25D, 0.08D);
        world.sendParticles(ParticleTypes.FIREWORK, muzzle.x, muzzle.y, muzzle.z, 18, 0.18D, 0.18D, 0.18D, 0.12D);
        world.sendParticles(ParticleTypes.FLAME, muzzle.x, muzzle.y, muzzle.z, 12, 0.12D, 0.12D, 0.12D, 0.04D);

        spawnForwardArc(world, player.position().add(0.0D, 1.35D, 0.0D), new Vec3(direction.x, 0.0D, direction.z), COLOR_GOLD, 1.2D, 50.0D, 18);

        world.playSound(null, muzzle.x, muzzle.y, muzzle.z, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.85F, 1.45F);
        world.playSound(null, muzzle.x, muzzle.y, muzzle.z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.55F, 1.8F);
    }

    private static void spawnGoldBulletTrail(ServerLevel world, ServerPlayer player, Vec3 muzzle, Vec3 direction, float damage) {
        Vec3 dir = direction.normalize();
        int points = damage >= 100.0F ? 18 : damage >= 35.0F ? 14 : 10;

        for (int i = 1; i <= points; i++) {
            int delay = i;
            double distance = 0.55D * i;

            TaskSchedulerUtil.runLater(delay, () -> {
                if (player.isRemoved()) {
                    return;
                }

                Vec3 pos = muzzle.add(dir.scale(distance));

                world.sendParticles(dust(COLOR_GOLD, 1.0F), pos.x, pos.y, pos.z, 8, 0.08D, 0.08D, 0.08D, 0.02D);
                world.sendParticles(ParticleTypes.FIREWORK, pos.x, pos.y, pos.z, 2, 0.04D, 0.04D, 0.04D, 0.02D);
            });
        }
    }

    private static void spawnPullStartParticles(ServerLevel world, ServerPlayer player, Vector3f color, boolean netherite) {
        Vec3 center = player.position().add(0.0D, 0.1D, 0.0D);

        spawnGroundRing(world, center, 5.0D, color, 120, netherite ? 1.25F : 1.05F);
        spawnGroundRing(world, center, 2.6D, netherite ? COLOR_NETHERITE_PURPLE : color, 72, 0.9F);
        spawnArmorBurst(world, player.position().add(0.0D, 1.0D, 0.0D), color, netherite ? 1.45F : 1.2F, netherite ? 80 : 56, 1.15D);

        if (netherite) {
            world.sendParticles(ParticleTypes.REVERSE_PORTAL, player.getX(), player.getY(0.55D), player.getZ(), 90, 1.2D, 0.8D, 1.2D, 0.08D);
            world.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY(0.45D), player.getZ(), 45, 1.0D, 0.4D, 1.0D, 0.035D);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS, 0.75F, 0.65F);
        } else {
            world.sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY(0.55D), player.getZ(), 70, 1.15D, 0.8D, 1.15D, 0.25D);
            world.sendParticles(ParticleTypes.ELECTRIC_SPARK, player.getX(), player.getY(0.6D), player.getZ(), 30, 0.7D, 0.45D, 0.7D, 0.08D);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 0.9F, 1.5F);
        }
    }

    private static void spawnPullLineParticles(ServerLevel world, Vec3 from, Vec3 to, Vector3f color, boolean netherite) {
        Vec3 delta = to.subtract(from);
        int points = Math.max(6, (int) (delta.length() * 4.0D));

        for (int i = 0; i <= points; i++) {
            double progress = (double) i / (double) points;
            Vec3 pos = from.add(delta.scale(progress));

            world.sendParticles(dust(color, netherite ? 1.15F : 0.95F), pos.x, pos.y, pos.z, 2, 0.035D, 0.035D, 0.035D, 0.01D);

            if (i % 4 == 0) {
                world.sendParticles(netherite ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.ENCHANT, pos.x, pos.y, pos.z, 1, 0.02D, 0.02D, 0.02D, 0.03D);
            }
        }
    }

    private static void spawnMarkParticles(ServerLevel world, LivingEntity target, Vector3f color, boolean netherite) {
        Vec3 center = target.position().add(0.0D, target.getBbHeight() * 0.55D, 0.0D);
        double radius = 0.75D;
        int points = 42;

        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0D * i / points;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;

            world.sendParticles(dust(color, 0.95F), x, center.y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        world.sendParticles(netherite ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.ENCHANT, center.x, center.y, center.z, netherite ? 28 : 34, 0.35D, 0.5D, 0.35D, netherite ? 0.08D : 0.25D);
    }

    private static void spawnMarkBreakParticles(ServerLevel world, LivingEntity target, Vector3f color, boolean netherite) {
        Vec3 center = target.position().add(0.0D, target.getBbHeight() * 0.55D, 0.0D);

        world.sendParticles(dust(color, netherite ? 1.55F : 1.35F), center.x, center.y, center.z, 64, 0.6D, 0.7D, 0.6D, 0.12D);
        world.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        world.sendParticles(netherite ? ParticleTypes.SMOKE : ParticleTypes.ELECTRIC_SPARK, center.x, center.y, center.z, 28, 0.45D, 0.45D, 0.45D, 0.08D);
    }

    private static void spawnDashChargeParticles(ServerLevel world, ServerPlayer player, Vector3f color, boolean netherite) {
        Vec3 center = player.position().add(0.0D, 1.0D, 0.0D);

        world.sendParticles(dust(color, netherite ? 1.45F : 1.2F), center.x, center.y, center.z, netherite ? 70 : 52, 0.6D, 0.75D, 0.6D, 0.08D);
        world.sendParticles(netherite ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.ELECTRIC_SPARK, center.x, center.y, center.z, netherite ? 55 : 36, 0.55D, 0.65D, 0.55D, netherite ? 0.08D : 0.1D);
        spawnGroundRing(world, player.position(), netherite ? 2.8D : 2.2D, color, netherite ? 90 : 72, netherite ? 1.25F : 1.0F);
    }

    private static void spawnDashTrailParticles(ServerLevel world, Vec3 start, Vec3 end, Vec3 direction, Vector3f color, boolean netherite) {
        Vec3 delta = end.subtract(start);
        int points = Math.max(8, (int) (delta.length() * 5.0D));
        Vec3 right = new Vec3(-direction.z, 0.0D, direction.x).normalize();

        for (int i = 0; i <= points; i++) {
            double progress = (double) i / (double) points;
            Vec3 base = start.add(delta.scale(progress)).add(0.0D, 0.18D, 0.0D);

            world.sendParticles(dust(color, netherite ? 1.35F : 1.1F), base.x, base.y, base.z, 6, 0.12D, 0.08D, 0.12D, 0.035D);

            Vec3 left = base.add(right.scale(-0.65D));
            Vec3 rightPos = base.add(right.scale(0.65D));

            world.sendParticles(dust(color, 0.9F), left.x, left.y, left.z, 2, 0.04D, 0.04D, 0.04D, 0.01D);
            world.sendParticles(dust(color, 0.9F), rightPos.x, rightPos.y, rightPos.z, 2, 0.04D, 0.04D, 0.04D, 0.01D);

            if (i % 3 == 0) {
                world.sendParticles(netherite ? ParticleTypes.SMOKE : ParticleTypes.ELECTRIC_SPARK, base.x, base.y + 0.1D, base.z, 3, 0.12D, 0.08D, 0.12D, 0.03D);
            }
        }
    }

    private static void spawnDashSweepBlades(ServerLevel world, Vec3 start, Vec3 end, Vec3 direction, Vector3f color, boolean netherite) {
        Vec3 delta = end.subtract(start);
        int blades = netherite ? 7 : 5;
        Vec3 right = new Vec3(-direction.z, 0.0D, direction.x).normalize();

        for (int i = 0; i < blades; i++) {
            double progress = (i + 0.5D) / blades;
            Vec3 center = start.add(delta.scale(progress)).add(0.0D, 1.0D, 0.0D);

            world.sendParticles(ParticleTypes.SWEEP_ATTACK, center.x, center.y, center.z, netherite ? 3 : 2, 0.18D, 0.08D, 0.18D, 0.0D);

            for (int j = -5; j <= 5; j++) {
                Vec3 blade = center
                        .add(right.scale(j * 0.28D))
                        .add(0.0D, Math.sin((j + 5) / 10.0D * Math.PI) * 0.35D, 0.0D);

                world.sendParticles(dust(color, netherite ? 1.35F : 1.15F), blade.x, blade.y, blade.z, 1, 0.015D, 0.015D, 0.015D, 0.0D);
            }

            if (netherite) {
                world.sendParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y, center.z, 12, 0.45D, 0.25D, 0.45D, 0.06D);
            } else {
                world.sendParticles(ParticleTypes.ENCHANT, center.x, center.y, center.z, 10, 0.45D, 0.25D, 0.45D, 0.18D);
            }
        }
    }

    private static void spawnDashEndBurst(ServerLevel world, Vec3 end, Vector3f color, boolean netherite) {
        Vec3 center = end.add(0.0D, 1.0D, 0.0D);

        world.sendParticles(dust(color, netherite ? 1.55F : 1.25F), center.x, center.y, center.z, netherite ? 90 : 62, 0.75D, 0.75D, 0.75D, 0.11D);
        world.sendParticles(ParticleTypes.SWEEP_ATTACK, center.x, center.y, center.z, netherite ? 5 : 3, 0.55D, 0.2D, 0.55D, 0.0D);
        world.sendParticles(netherite ? ParticleTypes.SMOKE : ParticleTypes.ELECTRIC_SPARK, center.x, center.y, center.z, netherite ? 42 : 28, 0.65D, 0.45D, 0.65D, 0.08D);

        if (netherite) {
            world.sendParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y, center.z, 70, 0.9D, 0.7D, 0.9D, 0.1D);
        } else {
            world.sendParticles(ParticleTypes.END_ROD, center.x, center.y, center.z, 24, 0.45D, 0.45D, 0.45D, 0.06D);
        }
    }

    private static void spawnNetheriteTeleportStart(ServerLevel world, ServerPlayer player) {
        Vec3 center = player.position().add(0.0D, 1.0D, 0.0D);

        spawnGroundRing(world, player.position(), 2.2D, COLOR_NETHERITE, 96, 1.25F);
        spawnGroundRing(world, player.position(), 1.1D, COLOR_NETHERITE_PURPLE, 54, 1.0F);
        world.sendParticles(dust(COLOR_NETHERITE, 1.5F), center.x, center.y, center.z, 95, 0.75D, 0.9D, 0.75D, 0.1D);
        world.sendParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y, center.z, 120, 0.9D, 1.0D, 0.9D, 0.12D);
        world.sendParticles(ParticleTypes.SMOKE, center.x, center.y - 0.2D, center.z, 40, 0.65D, 0.35D, 0.65D, 0.04D);
    }

    private static void spawnNetheriteTeleportImpact(ServerLevel world, LivingEntity target) {
        Vec3 center = target.position().add(0.0D, target.getBbHeight() * 0.55D, 0.0D);

        world.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 2, 0.15D, 0.15D, 0.15D, 0.0D);
        world.sendParticles(dust(COLOR_NETHERITE, 1.65F), center.x, center.y, center.z, 120, 0.8D, 0.8D, 0.8D, 0.13D);
        world.sendParticles(dust(COLOR_NETHERITE_PURPLE, 1.2F), center.x, center.y, center.z, 70, 0.65D, 0.65D, 0.65D, 0.1D);
        world.sendParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y, center.z, 95, 0.85D, 0.85D, 0.85D, 0.14D);
        world.sendParticles(ParticleTypes.SMOKE, center.x, center.y - 0.1D, center.z, 50, 0.55D, 0.45D, 0.55D, 0.05D);
    }

    private record GoldBulletMode(Item item, float damage) {
    }
}
