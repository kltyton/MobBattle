package com.kltyton.mob_battle.items.armor.compressarmor;

import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.bullet.GoldenBulletEntity;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.utils.ArmorUtil;
import com.kltyton.mob_battle.utils.EntityUtil;
import com.kltyton.mob_battle.utils.TaskSchedulerUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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

    private static void runIronSkill(ServerPlayerEntity player) {
        ItemStack cooldownItem = new ItemStack(ModItems.COMPRESSED_IRON_SWORD);
        if (isCoolingDown(player, cooldownItem, 6)) return;

        ServerWorld world = player.getWorld();

        spawnArmorBurst(world, player.getPos().add(0.0D, 1.0D, 0.0D), COLOR_IRON, 1.35F, 36, 0.9D);
        spawnGroundRing(world, player.getPos(), 2.4D, COLOR_IRON, 72, 0.95F);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 1.0F, 0.75F);

        DisplayEntity.ItemDisplayEntity swordDisplay = getItemDisplayEntity(world);
        setIronSwordPose(swordDisplay, player, -42.0F, -0.85F, 1.85F, 1.95F);
        world.spawnEntity(swordDisplay);

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
        player.getItemCooldownManager().set(cooldownItem, 6 * 20);
    }

    private static DisplayEntity.@NotNull ItemDisplayEntity getItemDisplayEntity(ServerWorld world) {
        DisplayEntity.ItemDisplayEntity swordDisplay = new DisplayEntity.ItemDisplayEntity(EntityType.ITEM_DISPLAY, world);
        swordDisplay.setItemStack(new ItemStack(ModItems.COMPRESSED_IRON_SWORD));
        swordDisplay.setItemDisplayContext(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
        swordDisplay.setNoGravity(true);
        swordDisplay.setInvulnerable(true);
        swordDisplay.setBrightness(Brightness.FULL);
        swordDisplay.setGlowColorOverride(0xD8E8FF);
        swordDisplay.setGlowing(true);
        swordDisplay.setViewRange(32.0F);
        swordDisplay.setDisplayWidth(3.0F);
        swordDisplay.setDisplayHeight(3.0F);
        return swordDisplay;
    }

    private static void setIronSwordPose(DisplayEntity.ItemDisplayEntity swordDisplay, ServerPlayerEntity player, float swingDegrees,
                                         float sideOffset, float heightOffset, float scale) {
        Vec3d forward = horizontalDirection(player);
        Vec3d right = new Vec3d(-forward.z, 0.0, forward.x);

        Vec3d displayPos = player.getPos()
                .add(forward.multiply(1.35))
                .add(right.multiply(sideOffset))
                .add(0.0, heightOffset, 0.0);

        swordDisplay.setPosition(displayPos.x, displayPos.y, displayPos.z);
        swordDisplay.setYaw(player.getYaw());
        swordDisplay.setPitch(0.0F);
        swordDisplay.setTeleportDuration(3);
        swordDisplay.setInterpolationDuration(3);
        swordDisplay.setStartInterpolation(0);
        swordDisplay.setTransformation(new AffineTransformation(
                new Vector3f(0.0F, 0.0F, 0.0F),
                new Quaternionf().rotateXYZ(MathHelper.RADIANS_PER_DEGREE * 65.0F, 0.0F, MathHelper.RADIANS_PER_DEGREE * swingDegrees),
                new Vector3f(scale, scale, scale),
                new Quaternionf()
        ));
    }

    private static void playIronSlashEffect(ServerPlayerEntity player, float sideOffset) {
        ServerWorld world = player.getWorld();
        Vec3d forward = horizontalDirection(player);
        Vec3d right = new Vec3d(-forward.z, 0.0, forward.x);

        Vec3d center = player.getPos()
                .add(forward.multiply(1.9))
                .add(right.multiply(sideOffset))
                .add(0.0, 1.0, 0.0);

        world.spawnParticles(ParticleTypes.SWEEP_ATTACK, center.x, center.y, center.z, 4, 0.55, 0.2, 0.55, 0.0);
        world.spawnParticles(dust(COLOR_IRON, 1.25F), center.x, center.y, center.z, 42, 1.1, 0.55, 1.1, 0.08);
        world.spawnParticles(ParticleTypes.CRIT, center.x, center.y, center.z, 24, 1.0, 0.55, 1.0, 0.18);
        world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, center.x, center.y + 0.15, center.z, 18, 0.8, 0.35, 0.8, 0.12);
        world.spawnParticles(ParticleTypes.END_ROD, center.x, center.y + 0.1, center.z, 12, 0.45, 0.25, 0.45, 0.04);

        spawnForwardArc(world, player.getPos().add(0.0D, 1.0D, 0.0D), forward, COLOR_IRON, 2.0D, 95.0D, 32);

        world.playSound(null, center.x, center.y, center.z, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.2F, 0.75F);
        world.playSound(null, center.x, center.y, center.z, SoundEvents.BLOCK_ANVIL_HIT, SoundCategory.PLAYERS, 0.55F, 1.45F);
    }

    private static void ironSlash(ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();

        for (LivingEntity target : EntityUtil.getNearbyEntity(player, LivingEntity.class, 3.0, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            target.damage(world, player.getDamageSources().playerAttack(player), 25.0F);
            spawnHitSpark(world, target, COLOR_IRON, true);
        }

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 3 * 20, 2, false, false, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 6 * 20, 2, false, false, true));
    }

    private static void switchGoldBulletMode(ServerPlayerEntity player) {
        int index = (getGoldModeIndex(player) + 1) % GOLD_BULLET_MODES.length;
        GOLD_MODE_INDEX.put(player.getUuid(), index);

        ServerWorld world = player.getWorld();
        spawnArmorBurst(world, player.getPos().add(0.0D, 1.0D, 0.0D), COLOR_GOLD, 1.15F, 28, 0.7D);
        spawnGroundRing(world, player.getPos(), 1.8D, COLOR_GOLD, 48, 0.8F);
        world.spawnParticles(ParticleTypes.FIREWORK, player.getX(), player.getBodyY(0.65D), player.getZ(), 18, 0.45, 0.5, 0.45, 0.08);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), SoundCategory.PLAYERS, 0.8F, 1.6F);

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
        Vec3d rotation = player.getRotationVec(1.0F);
        Vec3d muzzle = player.getEyePos().add(rotation.normalize().multiply(0.8D));

        spawnGoldMuzzleFlash(world, player, muzzle, rotation);

        ItemStack projectileStack = new ItemStack(mode.item());
        GoldenBulletEntity bullet = new GoldenBulletEntity(world, player, projectileStack, Items.BOW.getDefaultStack());
        bullet.setDamage(mode.damage());
        bullet.setTrueDamage(true, false);
        bullet.setVelocity(rotation.x, rotation.y, rotation.z, 3.5F, 0.0F);
        world.spawnEntity(bullet);

        spawnGoldBulletTrail(world, player, muzzle, rotation, mode.damage());

        player.getItemCooldownManager().set(cooldownItem, 20);
    }

    private static void runPullSkill(ServerPlayerEntity player, Item cooldownItem, int cooldownSeconds, float attackDamage, float magicDamage,
                                     RegistryEntry<StatusEffect> mark, int absorptionAmplifier, int resistanceAmplifier, int resistanceSeconds) {
        ItemStack cooldownStack = new ItemStack(cooldownItem);
        if (isCoolingDown(player, cooldownStack, cooldownSeconds)) return;

        ServerWorld world = player.getWorld();
        boolean netherite = mark == ModEffects.NETHERITE_MARK_ENTRY;
        Vector3f color = netherite ? COLOR_NETHERITE : COLOR_DIAMOND;
        Vector3f secondaryColor = netherite ? COLOR_NETHERITE_PURPLE : COLOR_DIAMOND;

        spawnPullStartParticles(world, player, color, netherite);

        List<LivingEntity> targets = EntityUtil.getNearbyEntity(player, LivingEntity.class, 5.0, false, EntityUtil.TeamFilter.EXCLUDE_TEAM);

        for (LivingEntity target : targets) {
            Vec3d pull = player.getPos().subtract(target.getPos());

            if (pull.lengthSquared() > 0.01) {
                target.setVelocity(pull.normalize().multiply(1.25));
                target.velocityModified = true;
            }

            spawnPullLineParticles(world, target.getPos().add(0.0D, target.getHeight() * 0.55D, 0.0D), player.getPos().add(0.0D, 1.0D, 0.0D), color, netherite);
            spawnHitSpark(world, target, secondaryColor, false);

            if (attackDamage > 0.0F) {
                target.damage(world, player.getDamageSources().playerAttack(player), attackDamage);
            }

            if (magicDamage > 0.0F) {
                target.timeUntilRegen = 0;
                target.damage(world, player.getDamageSources().indirectMagic(player, player), magicDamage);
            }

            target.addStatusEffect(new StatusEffectInstance(mark, 7 * 20, 0, false, false, true), player);
            spawnMarkParticles(world, target, color, netherite);
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
        Vec3d start = player.getPos();
        Vec3d direction = horizontalDirection(player);
        Vec3d end = findDashEnd(player, direction, distance);
        double dashDistance = start.distanceTo(end);

        boolean netherite = mark == ModEffects.NETHERITE_MARK_ENTRY;
        Vector3f color = netherite ? COLOR_NETHERITE : COLOR_DIAMOND;
        Vector3f secondaryColor = netherite ? COLOR_NETHERITE_PURPLE : COLOR_DIAMOND;

        spawnDashChargeParticles(world, player, color, netherite);

        Box attackBox = player.getBoundingBox().stretch(direction.multiply(dashDistance)).expand(1.5);
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
            target.damage(world, player.getDamageSources().playerAttack(player), attackDamage);

            if (magicDamage > 0.0F) {
                target.timeUntilRegen = 0;
                target.damage(world, player.getDamageSources().indirectMagic(player, player), magicDamage);
            }

            spawnHitSpark(world, target, secondaryColor, true);

            if (target.hasStatusEffect(mark)) {
                target.removeStatusEffect(mark);
                markedHit = true;
                spawnMarkBreakParticles(world, target, color, netherite);
            }
        }

        spawnDashTrailParticles(world, start, end, direction, color, netherite);
        spawnDashSweepBlades(world, start, end, direction, color, netherite);
        spawnDashEndBurst(world, end, color, netherite);

        double dashSpeed = Math.min(2.2D, Math.max(0.0D, dashDistance * 0.45D));
        player.setVelocity(direction.multiply(dashSpeed).add(0.0D, player.getVelocity().y, 0.0D));
        player.velocityModified = true;

        world.playSound(null, start.x, start.y, start.z, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.1F, netherite ? 0.55F : 1.35F);
        world.playSound(null, end.x, end.y, end.z, SoundEvents.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.PLAYERS, netherite ? 0.7F : 0.45F, netherite ? 0.65F : 1.45F);

        if (!markedHit) {
            player.getItemCooldownManager().set(cooldownStack, cooldownSeconds * 20);
        }
    }

    private static Vec3d findDashEnd(ServerPlayerEntity player, Vec3d direction, double distance) {
        ServerWorld world = player.getWorld();
        Vec3d start = player.getPos();
        Vec3d safeEnd = start;
        int steps = Math.max(1, MathHelper.ceil(distance / 0.25D));

        for (int i = 1; i <= steps; i++) {
            double stepDistance = distance * i / steps;
            Vec3d candidate = start.add(direction.multiply(stepDistance));
            Box candidateBox = player.getBoundingBox().offset(candidate.subtract(start));
            if (!world.isSpaceEmpty(player, candidateBox)) {
                break;
            }
            safeEnd = candidate;
        }

        return safeEnd;
    }

    private static void runNetheriteTeleportSkill(ServerPlayerEntity player) {
        ItemStack cooldownItem = new ItemStack(ModBlocks.COMPRESSED_NETHERITE_BLOCK.asItem());
        if (isCoolingDown(player, cooldownItem, 65)) return;

        LivingEntity target = findMarkedTargetInSight(player, 25.0, ModEffects.NETHERITE_MARK_ENTRY);
        if (target == null) {
            return;
        }

        ServerWorld world = player.getWorld();

        Vec3d from = player.getPos();
        Vec3d to = target.getPos();

        spawnNetheriteTeleportStart(world, player);
        spawnPullLineParticles(world, from.add(0.0D, 1.0D, 0.0D), to.add(0.0D, target.getHeight() * 0.55D, 0.0D), COLOR_NETHERITE_PURPLE, true);

        player.requestTeleport(target.getX(), target.getY(), target.getZ());

        target.damage(world, player.getDamageSources().playerAttack(player), 160.0F);
        target.removeStatusEffect(ModEffects.NETHERITE_MARK_ENTRY);

        spawnNetheriteTeleportImpact(world, target);
        spawnMarkBreakParticles(world, target, COLOR_NETHERITE, true);

        world.playSound(null, from.x, from.y, from.z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 0.45F);
        world.playSound(null, to.x, to.y, to.z, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.PLAYERS, 0.85F, 0.75F);

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
                        && !(target instanceof net.minecraft.entity.player.PlayerEntity targetPlayer && targetPlayer.isCreative())
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

    private static ParticleEffect dust(Vector3f color, float scale) {
        return new DustParticleEffect(
                ((int) (color.x * 255.0F) << 16)
                        | ((int) (color.y * 255.0F) << 8)
                        | (int) (color.z * 255.0F),
                scale
        );
    }

    private static void spawnArmorBurst(ServerWorld world, Vec3d center, Vector3f color, float scale, int count, double spread) {
        world.spawnParticles(dust(color, scale), center.x, center.y, center.z, count, spread, spread * 0.75D, spread, 0.08D);
        world.spawnParticles(ParticleTypes.END_ROD, center.x, center.y, center.z, Math.max(6, count / 4), spread * 0.35D, spread * 0.55D, spread * 0.35D, 0.035D);
    }

    private static void spawnGroundRing(ServerWorld world, Vec3d center, double radius, Vector3f color, int points, float scale) {
        double y = center.y + 0.08D;

        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0D * i / points;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;

            world.spawnParticles(dust(color, scale), x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);

            if (i % 6 == 0) {
                world.spawnParticles(ParticleTypes.CRIT, x, y + 0.08D, z, 1, 0.02D, 0.02D, 0.02D, 0.03D);
            }
        }
    }

    private static void spawnForwardArc(ServerWorld world, Vec3d origin, Vec3d forward, Vector3f color, double radius, double arcDegrees, int points) {
        Vec3d horizontal = new Vec3d(forward.x, 0.0D, forward.z).normalize();
        double baseAngle = Math.atan2(horizontal.z, horizontal.x);
        double halfArc = Math.toRadians(arcDegrees * 0.5D);

        for (int i = 0; i < points; i++) {
            double progress = points <= 1 ? 0.5D : (double) i / (double) (points - 1);
            double angle = baseAngle - halfArc + halfArc * 2.0D * progress;
            double x = origin.x + Math.cos(angle) * radius;
            double z = origin.z + Math.sin(angle) * radius;

            world.spawnParticles(dust(color, 1.15F), x, origin.y, z, 1, 0.02D, 0.02D, 0.02D, 0.01D);
        }
    }

    private static void spawnHitSpark(ServerWorld world, LivingEntity target, Vector3f color, boolean heavy) {
        double x = target.getX();
        double y = target.getBodyY(0.55D);
        double z = target.getZ();

        world.spawnParticles(dust(color, heavy ? 1.35F : 1.0F), x, y, z, heavy ? 30 : 16, 0.35D, 0.45D, 0.35D, 0.07D);
        world.spawnParticles(ParticleTypes.CRIT, x, y, z, heavy ? 18 : 8, 0.32D, 0.38D, 0.32D, 0.12D);

        if (heavy) {
            world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, x, y + 0.2D, z, 10, 0.32D, 0.32D, 0.32D, 0.12D);
        }
    }

    private static void spawnGoldMuzzleFlash(ServerWorld world, ServerPlayerEntity player, Vec3d muzzle, Vec3d direction) {
        world.spawnParticles(dust(COLOR_GOLD, 1.35F), muzzle.x, muzzle.y, muzzle.z, 34, 0.25D, 0.25D, 0.25D, 0.08D);
        world.spawnParticles(ParticleTypes.FIREWORK, muzzle.x, muzzle.y, muzzle.z, 18, 0.18D, 0.18D, 0.18D, 0.12D);
        world.spawnParticles(ParticleTypes.FLAME, muzzle.x, muzzle.y, muzzle.z, 12, 0.12D, 0.12D, 0.12D, 0.04D);

        spawnForwardArc(world, player.getPos().add(0.0D, 1.35D, 0.0D), new Vec3d(direction.x, 0.0D, direction.z), COLOR_GOLD, 1.2D, 50.0D, 18);

        world.playSound(null, muzzle.x, muzzle.y, muzzle.z, SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.PLAYERS, 0.85F, 1.45F);
        world.playSound(null, muzzle.x, muzzle.y, muzzle.z, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 0.55F, 1.8F);
    }

    private static void spawnGoldBulletTrail(ServerWorld world, ServerPlayerEntity player, Vec3d muzzle, Vec3d direction, float damage) {
        Vec3d dir = direction.normalize();
        int points = damage >= 100.0F ? 18 : damage >= 35.0F ? 14 : 10;

        for (int i = 1; i <= points; i++) {
            int delay = i;
            double distance = 0.55D * i;

            TaskSchedulerUtil.runLater(delay, () -> {
                if (player.isRemoved()) {
                    return;
                }

                Vec3d pos = muzzle.add(dir.multiply(distance));

                world.spawnParticles(dust(COLOR_GOLD, 1.0F), pos.x, pos.y, pos.z, 8, 0.08D, 0.08D, 0.08D, 0.02D);
                world.spawnParticles(ParticleTypes.FIREWORK, pos.x, pos.y, pos.z, 2, 0.04D, 0.04D, 0.04D, 0.02D);
            });
        }
    }

    private static void spawnPullStartParticles(ServerWorld world, ServerPlayerEntity player, Vector3f color, boolean netherite) {
        Vec3d center = player.getPos().add(0.0D, 0.1D, 0.0D);

        spawnGroundRing(world, center, 5.0D, color, 120, netherite ? 1.25F : 1.05F);
        spawnGroundRing(world, center, 2.6D, netherite ? COLOR_NETHERITE_PURPLE : color, 72, 0.9F);
        spawnArmorBurst(world, player.getPos().add(0.0D, 1.0D, 0.0D), color, netherite ? 1.45F : 1.2F, netherite ? 80 : 56, 1.15D);

        if (netherite) {
            world.spawnParticles(ParticleTypes.REVERSE_PORTAL, player.getX(), player.getBodyY(0.55D), player.getZ(), 90, 1.2D, 0.8D, 1.2D, 0.08D);
            world.spawnParticles(ParticleTypes.SMOKE, player.getX(), player.getBodyY(0.45D), player.getZ(), 45, 1.0D, 0.4D, 1.0D, 0.035D);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.PLAYERS, 0.75F, 0.65F);
        } else {
            world.spawnParticles(ParticleTypes.ENCHANT, player.getX(), player.getBodyY(0.55D), player.getZ(), 70, 1.15D, 0.8D, 1.15D, 0.25D);
            world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, player.getX(), player.getBodyY(0.6D), player.getZ(), 30, 0.7D, 0.45D, 0.7D, 0.08D);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.PLAYERS, 0.9F, 1.5F);
        }
    }

    private static void spawnPullLineParticles(ServerWorld world, Vec3d from, Vec3d to, Vector3f color, boolean netherite) {
        Vec3d delta = to.subtract(from);
        int points = Math.max(6, (int) (delta.length() * 4.0D));

        for (int i = 0; i <= points; i++) {
            double progress = (double) i / (double) points;
            Vec3d pos = from.add(delta.multiply(progress));

            world.spawnParticles(dust(color, netherite ? 1.15F : 0.95F), pos.x, pos.y, pos.z, 2, 0.035D, 0.035D, 0.035D, 0.01D);

            if (i % 4 == 0) {
                world.spawnParticles(netherite ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.ENCHANT, pos.x, pos.y, pos.z, 1, 0.02D, 0.02D, 0.02D, 0.03D);
            }
        }
    }

    private static void spawnMarkParticles(ServerWorld world, LivingEntity target, Vector3f color, boolean netherite) {
        Vec3d center = target.getPos().add(0.0D, target.getHeight() * 0.55D, 0.0D);
        double radius = 0.75D;
        int points = 42;

        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0D * i / points;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;

            world.spawnParticles(dust(color, 0.95F), x, center.y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        world.spawnParticles(netherite ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.ENCHANT, center.x, center.y, center.z, netherite ? 28 : 34, 0.35D, 0.5D, 0.35D, netherite ? 0.08D : 0.25D);
    }

    private static void spawnMarkBreakParticles(ServerWorld world, LivingEntity target, Vector3f color, boolean netherite) {
        Vec3d center = target.getPos().add(0.0D, target.getHeight() * 0.55D, 0.0D);

        world.spawnParticles(dust(color, netherite ? 1.55F : 1.35F), center.x, center.y, center.z, 64, 0.6D, 0.7D, 0.6D, 0.12D);
        world.spawnParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        world.spawnParticles(netherite ? ParticleTypes.SMOKE : ParticleTypes.ELECTRIC_SPARK, center.x, center.y, center.z, 28, 0.45D, 0.45D, 0.45D, 0.08D);
    }

    private static void spawnDashChargeParticles(ServerWorld world, ServerPlayerEntity player, Vector3f color, boolean netherite) {
        Vec3d center = player.getPos().add(0.0D, 1.0D, 0.0D);

        world.spawnParticles(dust(color, netherite ? 1.45F : 1.2F), center.x, center.y, center.z, netherite ? 70 : 52, 0.6D, 0.75D, 0.6D, 0.08D);
        world.spawnParticles(netherite ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.ELECTRIC_SPARK, center.x, center.y, center.z, netherite ? 55 : 36, 0.55D, 0.65D, 0.55D, netherite ? 0.08D : 0.1D);
        spawnGroundRing(world, player.getPos(), netherite ? 2.8D : 2.2D, color, netherite ? 90 : 72, netherite ? 1.25F : 1.0F);
    }

    private static void spawnDashTrailParticles(ServerWorld world, Vec3d start, Vec3d end, Vec3d direction, Vector3f color, boolean netherite) {
        Vec3d delta = end.subtract(start);
        int points = Math.max(8, (int) (delta.length() * 5.0D));
        Vec3d right = new Vec3d(-direction.z, 0.0D, direction.x).normalize();

        for (int i = 0; i <= points; i++) {
            double progress = (double) i / (double) points;
            Vec3d base = start.add(delta.multiply(progress)).add(0.0D, 0.18D, 0.0D);

            world.spawnParticles(dust(color, netherite ? 1.35F : 1.1F), base.x, base.y, base.z, 6, 0.12D, 0.08D, 0.12D, 0.035D);

            Vec3d left = base.add(right.multiply(-0.65D));
            Vec3d rightPos = base.add(right.multiply(0.65D));

            world.spawnParticles(dust(color, 0.9F), left.x, left.y, left.z, 2, 0.04D, 0.04D, 0.04D, 0.01D);
            world.spawnParticles(dust(color, 0.9F), rightPos.x, rightPos.y, rightPos.z, 2, 0.04D, 0.04D, 0.04D, 0.01D);

            if (i % 3 == 0) {
                world.spawnParticles(netherite ? ParticleTypes.SMOKE : ParticleTypes.ELECTRIC_SPARK, base.x, base.y + 0.1D, base.z, 3, 0.12D, 0.08D, 0.12D, 0.03D);
            }
        }
    }

    private static void spawnDashSweepBlades(ServerWorld world, Vec3d start, Vec3d end, Vec3d direction, Vector3f color, boolean netherite) {
        Vec3d delta = end.subtract(start);
        int blades = netherite ? 7 : 5;
        Vec3d right = new Vec3d(-direction.z, 0.0D, direction.x).normalize();

        for (int i = 0; i < blades; i++) {
            double progress = (i + 0.5D) / blades;
            Vec3d center = start.add(delta.multiply(progress)).add(0.0D, 1.0D, 0.0D);

            world.spawnParticles(ParticleTypes.SWEEP_ATTACK, center.x, center.y, center.z, netherite ? 3 : 2, 0.18D, 0.08D, 0.18D, 0.0D);

            for (int j = -5; j <= 5; j++) {
                Vec3d blade = center
                        .add(right.multiply(j * 0.28D))
                        .add(0.0D, Math.sin((j + 5) / 10.0D * Math.PI) * 0.35D, 0.0D);

                world.spawnParticles(dust(color, netherite ? 1.35F : 1.15F), blade.x, blade.y, blade.z, 1, 0.015D, 0.015D, 0.015D, 0.0D);
            }

            if (netherite) {
                world.spawnParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y, center.z, 12, 0.45D, 0.25D, 0.45D, 0.06D);
            } else {
                world.spawnParticles(ParticleTypes.ENCHANT, center.x, center.y, center.z, 10, 0.45D, 0.25D, 0.45D, 0.18D);
            }
        }
    }

    private static void spawnDashEndBurst(ServerWorld world, Vec3d end, Vector3f color, boolean netherite) {
        Vec3d center = end.add(0.0D, 1.0D, 0.0D);

        world.spawnParticles(dust(color, netherite ? 1.55F : 1.25F), center.x, center.y, center.z, netherite ? 90 : 62, 0.75D, 0.75D, 0.75D, 0.11D);
        world.spawnParticles(ParticleTypes.SWEEP_ATTACK, center.x, center.y, center.z, netherite ? 5 : 3, 0.55D, 0.2D, 0.55D, 0.0D);
        world.spawnParticles(netherite ? ParticleTypes.SMOKE : ParticleTypes.ELECTRIC_SPARK, center.x, center.y, center.z, netherite ? 42 : 28, 0.65D, 0.45D, 0.65D, 0.08D);

        if (netherite) {
            world.spawnParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y, center.z, 70, 0.9D, 0.7D, 0.9D, 0.1D);
        } else {
            world.spawnParticles(ParticleTypes.END_ROD, center.x, center.y, center.z, 24, 0.45D, 0.45D, 0.45D, 0.06D);
        }
    }

    private static void spawnNetheriteTeleportStart(ServerWorld world, ServerPlayerEntity player) {
        Vec3d center = player.getPos().add(0.0D, 1.0D, 0.0D);

        spawnGroundRing(world, player.getPos(), 2.2D, COLOR_NETHERITE, 96, 1.25F);
        spawnGroundRing(world, player.getPos(), 1.1D, COLOR_NETHERITE_PURPLE, 54, 1.0F);
        world.spawnParticles(dust(COLOR_NETHERITE, 1.5F), center.x, center.y, center.z, 95, 0.75D, 0.9D, 0.75D, 0.1D);
        world.spawnParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y, center.z, 120, 0.9D, 1.0D, 0.9D, 0.12D);
        world.spawnParticles(ParticleTypes.SMOKE, center.x, center.y - 0.2D, center.z, 40, 0.65D, 0.35D, 0.65D, 0.04D);
    }

    private static void spawnNetheriteTeleportImpact(ServerWorld world, LivingEntity target) {
        Vec3d center = target.getPos().add(0.0D, target.getHeight() * 0.55D, 0.0D);

        world.spawnParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 2, 0.15D, 0.15D, 0.15D, 0.0D);
        world.spawnParticles(dust(COLOR_NETHERITE, 1.65F), center.x, center.y, center.z, 120, 0.8D, 0.8D, 0.8D, 0.13D);
        world.spawnParticles(dust(COLOR_NETHERITE_PURPLE, 1.2F), center.x, center.y, center.z, 70, 0.65D, 0.65D, 0.65D, 0.1D);
        world.spawnParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y, center.z, 95, 0.85D, 0.85D, 0.85D, 0.14D);
        world.spawnParticles(ParticleTypes.SMOKE, center.x, center.y - 0.1D, center.z, 50, 0.55D, 0.45D, 0.55D, 0.05D);
    }

    private record GoldBulletMode(Item item, float damage) {
    }
}
