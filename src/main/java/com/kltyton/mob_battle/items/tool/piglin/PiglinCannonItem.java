package com.kltyton.mob_battle.items.tool.piglin;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.bullet.GoldenTrailProjectile;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.utils.EntityUtil;
import com.kltyton.mob_battle.utils.TaskSchedulerUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PiglinCannonItem extends Item {
    private static final String FAST_COUNTER_KEY = "FastCounter";
    private static final String LAST_FAST_FIRE_TICK_KEY = "LastFastFireTick";
    private static final String LAST_HEAVY_DRAIN_STEP_KEY = "LastHeavyDrainStep";
    private static final String HEAVY_FAILED_KEY = "HeavyFailed";

    private static final int FAST_FIRE_INTERVAL = 5;
    private static final int HEAVY_CHARGE_TICKS = 120;

    public PiglinCannonItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user.isSneaking()) {
            PiglinCannonModeUtil.Mode mode = PiglinCannonModeUtil.toggleMode(stack);
            if (!world.isClient) {
                user.sendMessage(Text.literal(mode == PiglinCannonModeUtil.Mode.FAST_FIRE ? "切换为速射形态" : "切换为重击模式"), true);
            }
            return ActionResult.SUCCESS;
        }

        resetHeavyState(stack);
        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);

        if (!(entity instanceof PlayerEntity player)) {
            return;
        }

        if (!(player.isUsingItem() && player.getActiveItem() == stack)) {
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
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return false;
        }
        if (!(user instanceof PlayerEntity player)) {
            return false;
        }

        PiglinCannonModeUtil.Mode mode = PiglinCannonModeUtil.getMode(stack);
        if (mode != PiglinCannonModeUtil.Mode.HEAVY_BLAST) {
            return true;
        }

        int usedTicks = this.getMaxUseTime(stack, user) - remainingUseTicks;
        boolean failed = getBoolean(stack, HEAVY_FAILED_KEY);

        if (!failed && usedTicks >= HEAVY_CHARGE_TICKS) {
            Mob_battle.LOGGER.info("Heavy blast");
            fireHeavyBlast(serverWorld, player, stack);
        }

        resetHeavyState(stack);
        return true;
    }


    private void handleFastFireTick(ServerWorld world, PlayerEntity player, ItemStack stack) {
        long now = world.getTime();
        long lastFire = getLong(stack, LAST_FAST_FIRE_TICK_KEY);

        if (now - lastFire < FAST_FIRE_INTERVAL) {
            return;
        }

        boolean fired = fireFastShot(world, player, stack);
        if (fired) {
            setLong(stack, LAST_FAST_FIRE_TICK_KEY, now);
        } else {
            player.clearActiveItem();
        }
    }

    private boolean fireFastShot(ServerWorld world, PlayerEntity player, ItemStack stack) {
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

        projectile.pickupType = GoldenTrailProjectile.PickupPermission.DISALLOWED;
        projectile.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 4.0F, 0.35F);
        projectile.setDamage(90.0F);
        projectile.setTrueDamage(true, false);

        projectile.setStrengthen(strengthen);
        projectile.setBloodStrengthen(bloodStrengthen);

        world.spawnEntity(projectile);

        if (strengthen) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, SoundCategory.PLAYERS, 0.7F, 1.25F);
        } else {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 0.7F, 1.4F);
        }

        return true;
    }

    private void handleHeavyChargeTick(ServerWorld world, PlayerEntity player, ItemStack stack) {
        int elapsed = this.getMaxUseTime(stack, player) - player.getItemUseTimeLeft();
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
                player.clearActiveItem();
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.8F, 0.8F);
                return;
            }

            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5F, 0.7F + step * 0.05F);
        }
    }

    private void fireHeavyBlast(ServerWorld world, PlayerEntity player, ItemStack stack) {
        Vec3d start = player.getEyePos();
        Vec3d dir = player.getRotationVec(1.0F).normalize();
        Vec3d end = start.add(dir.multiply(30.0D));
        Mob_battle.LOGGER.info("Heavy blast: " + start + " -> " + end);

        spawnHeavyLaserLine(world, start, end);

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0F, 0.6F);

        TaskSchedulerUtil.runLater(20, () -> {
            Mob_battle.LOGGER.info("Exploding heavy blast: " + start + " -> " + end);
            if (!player.isAlive() || player.getWorld() != world) {
                return;
            }
            explodeHeavyLaser(world, player, start, end);
        });
    }

    private void spawnHeavyLaserLine(ServerWorld world, Vec3d start, Vec3d end) {
        Mob_battle.LOGGER.info("Spawning heavy laser line: " + start + " -> " + end);
        Vec3d diff = end.subtract(start);
        int steps = 60;

        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            Vec3d pos = start.add(diff.multiply(t));

            world.spawnParticles(
                    new DustParticleEffect(0xFFD54A, 1.8F),
                    pos.x, pos.y, pos.z,
                    4,
                    0.08, 0.08, 0.08,
                    0.0
            );

            if (i % 2 == 0) {
                world.spawnParticles(
                        ParticleTypes.GLOW,
                        pos.x, pos.y, pos.z,
                        2,
                        0.03, 0.03, 0.03,
                        0.0
                );
            }
        }
    }

    private void explodeHeavyLaser(ServerWorld world, PlayerEntity player, Vec3d start, Vec3d end) {
        Mob_battle.LOGGER.info("Exploding heavy laser: " + start + " -> " + end);
        Vec3d diff = end.subtract(start);
        int steps = 30;
        Set<LivingEntity> hitEntities = new HashSet<>();

        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            Vec3d pos = start.add(diff.multiply(t));

            world.spawnParticles(
                    ParticleTypes.EXPLOSION,
                    pos.x, pos.y, pos.z,
                    1,
                    0.0, 0.0, 0.0,
                    0.0
            );

            Box box = new Box(
                    pos.x - 1.5, pos.y - 1.5, pos.z - 1.5,
                    pos.x + 1.5, pos.y + 1.5, pos.z + 1.5
            );

            List<LivingEntity> list = world.getEntitiesByClass(
                    LivingEntity.class,
                    box,
                    entity -> entity.isAlive() && entity != player
            );
            hitEntities.addAll(list);
        }

        for (LivingEntity entity : hitEntities) {
            entity.damage(world, entity.getDamageSources().explosion(player, player), 220.0F);
            entity.damage(world, entity.getDamageSources().mobProjectile(player, player), 100.0F);
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.PLAYERS, 1.2F, 0.8F);
    }



    private boolean consumeOneGoldBlock(PlayerEntity player) {
        if (player.isInCreativeMode()) {
            return true;
        }

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack inv = player.getInventory().getStack(i);
            if (inv.isOf(ModItems.TRAIN_BULLET)) {
                inv.decrement(1);
                return true;
            }
        }
        return false;
    }

    private boolean drainPiglinHealth(ServerWorld world, PlayerEntity player, double amount) {
        if (player.isCreative()) return true;
        double remain = amount;

        while (remain > 0.0D) {
            List<PiglinEntity> piglins = new ArrayList<>(
                    EntityUtil.getNearbyEntity(player, PiglinEntity.class, 8.0, false, EntityUtil.TeamFilter.ALL)
            );

            if (piglins.isEmpty()) {
                return false;
            }

            PiglinEntity piglin = piglins.get(world.random.nextInt(piglins.size()));
            float health = piglin.getHealth();
            double take = Math.min(remain, health);

            piglin.damage(world, piglin.getDamageSources().magic(), (float) take);
            remain -= take;

            if (!piglin.isAlive() && remain <= 0.0D) {
                break;
            }
        }

        return true;
    }

    private static int getInt(ItemStack stack, String key) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        return nbt.getInt(key, 0);
    }

    private static void setInt(ItemStack stack, String key, int value) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        nbt.putInt(key, value);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    private static long getLong(ItemStack stack, String key) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        return nbt.getLong(key, 0);
    }

    private static void setLong(ItemStack stack, String key, long value) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        nbt.putLong(key, value);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    private static boolean getBoolean(ItemStack stack, String key) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        return nbt.getBoolean(key, false);
    }

    private static void setBoolean(ItemStack stack, String key, boolean value) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        nbt.putBoolean(key, value);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    private static void resetHeavyState(ItemStack stack) {
        setInt(stack, LAST_HEAVY_DRAIN_STEP_KEY, 0);
        setBoolean(stack, HEAVY_FAILED_KEY, false);
    }
}