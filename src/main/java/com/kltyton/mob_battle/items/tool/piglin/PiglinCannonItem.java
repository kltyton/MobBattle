package com.kltyton.mob_battle.items.tool.piglin;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.bullet.GoldenTrailProjectile;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.utils.EntityUtil;
import com.kltyton.mob_battle.utils.TaskSchedulerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PiglinCannonItem extends Item {
    private static final String FAST_COUNTER_KEY = "FastCounter";
    private static final String LAST_FAST_FIRE_TICK_KEY = "LastFastFireTick";
    private static final String LAST_HEAVY_DRAIN_STEP_KEY = "LastHeavyDrainStep";
    private static final String HEAVY_FAILED_KEY = "HeavyFailed";

    private static final int FAST_FIRE_INTERVAL = 3;
    private static final int HEAVY_CHARGE_TICKS = 120;

    public PiglinCannonItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);

        if (user.isShiftKeyDown()) {
            PiglinCannonModeUtil.Mode mode = PiglinCannonModeUtil.toggleMode(stack);
            if (!world.isClientSide) {
                user.displayClientMessage(Component.literal(mode == PiglinCannonModeUtil.Mode.FAST_FIRE ? "切换为速射形态" : "切换为重击模式"), true);
            }
            return InteractionResult.SUCCESS;
        }

        resetHeavyState(stack);
        user.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);

        if (!(entity instanceof Player player)) {
            return;
        }

        if (!(player.isUsingItem() && player.getUseItem() == stack)) {
            return;
        }
        PiglinCannonModeUtil.Mode mode = PiglinCannonModeUtil.getMode(stack);
        if (mode == PiglinCannonModeUtil.Mode.FAST_FIRE) {
            handleFastFireTick(world, player, stack);
        } else {
            handleHeavyChargeTick(world, player, stack);
        }
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (!(world instanceof ServerLevel serverWorld)) {
            return false;
        }
        if (!(user instanceof Player player)) {
            return false;
        }

        PiglinCannonModeUtil.Mode mode = PiglinCannonModeUtil.getMode(stack);
        if (mode != PiglinCannonModeUtil.Mode.HEAVY_BLAST) {
            return true;
        }

        int usedTicks = this.getUseDuration(stack, user) - remainingUseTicks;
        boolean failed = getBoolean(stack, HEAVY_FAILED_KEY);

        if (!failed && usedTicks >= HEAVY_CHARGE_TICKS) {
            Mob_battle.LOGGER.info("Heavy blast");
            fireHeavyBlast(serverWorld, player, stack);
        }

        resetHeavyState(stack);
        return true;
    }


    private void handleFastFireTick(ServerLevel world, Player player, ItemStack stack) {
        long now = world.getGameTime();
        long lastFire = getLong(stack, LAST_FAST_FIRE_TICK_KEY);

        if (now - lastFire < FAST_FIRE_INTERVAL) {
            return;
        }

        boolean fired = fireFastShot(world, player, stack);
        if (fired) {
            setLong(stack, LAST_FAST_FIRE_TICK_KEY, now);
        } else {
            player.stopUsingItem();
        }
    }

    private boolean fireFastShot(ServerLevel world, Player player, ItemStack stack) {
        boolean consumedGold = consumeOneGoldBlock(player);
        boolean bloodStrengthen = false;

        if (!consumedGold) {
            boolean paidByPiglin = drainPiglinHealth(world, player, 10.0F);
            if (!paidByPiglin) {
                return false;
            }
            bloodStrengthen = true;
        }

        int counter = getInt(stack, FAST_COUNTER_KEY);
        boolean strengthen = (counter >= 9);
        counter = strengthen ? 0 : counter + 1;
        setInt(stack, FAST_COUNTER_KEY, counter);

        GoldenTrailProjectile projectile = new GoldenTrailProjectile(
                ModEntities.GOLDEN_TRAIL_PROJECTILE,
                player,
                world,
                new ItemStack(ModItems.TRAIN_BULLET),
                stack
        );

        projectile.pickup = net.minecraft.world.entity.projectile.AbstractArrow.Pickup.DISALLOWED;
        projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 4.0F, 0.35F);
        projectile.setBaseDamage(90.0F);
        projectile.setTrueDamage(true, false);

        projectile.setStrengthen(strengthen);
        projectile.setBloodStrengthen(bloodStrengthen);

        world.addFreshEntity(projectile);

        if (strengthen) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ZOMBIFIED_PIGLIN_ANGRY, SoundSource.PLAYERS, 0.7F, 1.25F);
        } else {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 0.7F, 1.4F);
        }

        return true;
    }

    private void handleHeavyChargeTick(ServerLevel world, Player player, ItemStack stack) {
        int elapsed = this.getUseDuration(stack, player) - player.getUseItemRemainingTicks();
        if (elapsed <= 0) {
            return;
        }

        int step = elapsed / 20;
        int lastStep = getInt(stack, LAST_HEAVY_DRAIN_STEP_KEY);

        if (step > 6) {
            return;
        }
        if (elapsed % 20 == 0 && step > 0 && step != lastStep) {
            boolean success = drainPiglinHealth(world, player, 100.0F);
            setInt(stack, LAST_HEAVY_DRAIN_STEP_KEY, step);

            if (!success) {
                setBoolean(stack, HEAVY_FAILED_KEY, true);
                player.stopUsingItem();
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.8F, 0.8F);
                return;
            }

            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 0.7F + step * 0.05F);
        }
    }

    private void fireHeavyBlast(ServerLevel world, Player player, ItemStack stack) {
        Vec3 start = player.getEyePosition();
        Vec3 dir = player.getViewVector(1.0F).normalize();
        Vec3 end = start.add(dir.scale(50.0D));
        Mob_battle.LOGGER.info("Heavy blast: " + start + " -> " + end);

        spawnHeavyLaserLine(world, start, end);

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0F, 0.6F);

        TaskSchedulerUtil.runLater(20, () -> {
            Mob_battle.LOGGER.info("Exploding heavy blast: " + start + " -> " + end);
            if (!player.isAlive() || player.level() != world) {
                return;
            }
            explodeHeavyLaser(world, player, start, end);
        });
    }

    private void spawnHeavyLaserLine(ServerLevel world, Vec3 start, Vec3 end) {
        Mob_battle.LOGGER.info("Spawning heavy laser line: " + start + " -> " + end);
        Vec3 diff = end.subtract(start);
        int steps = 60;

        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            Vec3 pos = start.add(diff.scale(t));

            world.sendParticles(
                    new DustParticleOptions(0xFFD54A, 1.8F),
                    pos.x, pos.y, pos.z,
                    4,
                    0.08, 0.08, 0.08,
                    0.0
            );

            if (i % 2 == 0) {
                world.sendParticles(
                        ParticleTypes.GLOW,
                        pos.x, pos.y, pos.z,
                        2,
                        0.03, 0.03, 0.03,
                        0.0
                );
            }
        }
    }

    private void explodeHeavyLaser(ServerLevel world, Player player, Vec3 start, Vec3 end) {
        Mob_battle.LOGGER.info("Exploding heavy laser: " + start + " -> " + end);
        Vec3 diff = end.subtract(start);
        int steps = 30;
        Set<LivingEntity> hitEntities = new HashSet<>();

        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            Vec3 pos = start.add(diff.scale(t));

            world.sendParticles(
                    ParticleTypes.EXPLOSION,
                    pos.x, pos.y, pos.z,
                    1,
                    0.0, 0.0, 0.0,
                    0.0
            );

            AABB box = new AABB(
                    pos.x - 1.5, pos.y - 1.5, pos.z - 1.5,
                    pos.x + 1.5, pos.y + 1.5, pos.z + 1.5
            );

            List<LivingEntity> list = world.getEntitiesOfClass(
                    LivingEntity.class,
                    box,
                    entity -> entity.isAlive() && entity != player
            );
            hitEntities.addAll(list);
        }

        for (LivingEntity entity : hitEntities) {
            entity.hurtServer(world, entity.damageSources().explosion(player, player), 220.0F);
            entity.hurtServer(world, entity.damageSources().mobProjectile(player, player), 100.0F);
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.PLAYERS, 1.2F, 0.8F);
    }



    private boolean consumeOneGoldBlock(Player player) {
        if (player.hasInfiniteMaterials()) {
            return true;
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack inv = player.getInventory().getItem(i);
            if (inv.is(ModItems.TRAIN_BULLET)) {
                inv.shrink(1);
                return true;
            }
        }
        return false;
    }

    private boolean drainPiglinHealth(ServerLevel world, Player player, double amount) {
        if (player.isCreative()) return true;
        double remain = amount;

        while (remain > 0.0D) {
            List<AbstractPiglin> piglins = new ArrayList<>(
                    EntityUtil.getNearbyEntity(player, AbstractPiglin.class, 8.0, false, EntityUtil.TeamFilter.ALL)
            );

            if (piglins.isEmpty()) {
                return false;
            }

            AbstractPiglin piglin = piglins.get(world.random.nextInt(piglins.size()));
            float health = piglin.getHealth();
            double take = Math.min(remain, health);

            piglin.hurtServer(world, piglin.damageSources().magic(), (float) take);
            remain -= take;

            if (!piglin.isAlive() && remain <= 0.0D) {
                break;
            }
        }

        return true;
    }

    private static int getInt(ItemStack stack, String key) {
        CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return nbt.getIntOr(key, 0);
    }

    private static void setInt(ItemStack stack, String key, int value) {
        CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        nbt.putInt(key, value);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }

    private static long getLong(ItemStack stack, String key) {
        CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return nbt.getLongOr(key, 0);
    }

    private static void setLong(ItemStack stack, String key, long value) {
        CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        nbt.putLong(key, value);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }

    private static boolean getBoolean(ItemStack stack, String key) {
        CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return nbt.getBooleanOr(key, false);
    }

    private static void setBoolean(ItemStack stack, String key, boolean value) {
        CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        nbt.putBoolean(key, value);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }

    private static void resetHeavyState(ItemStack stack) {
        setInt(stack, LAST_HEAVY_DRAIN_STEP_KEY, 0);
        setBoolean(stack, HEAVY_FAILED_KEY, false);
    }
}
