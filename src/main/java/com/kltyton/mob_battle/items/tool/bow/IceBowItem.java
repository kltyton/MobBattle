package com.kltyton.mob_battle.items.tool.bow;

import com.kltyton.mob_battle.entity.bullet.IceArrowEntity;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.tool.BaseBow;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EnchantableComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class IceBowItem extends BaseBow {
    private static final int AMMO_COST = 5;
    private static final int SHOT_COUNT = 10;
    private static final float BASE_PROJECTILE_DAMAGE = 120.0F;

    public IceBowItem(Settings settings) {
        super(settings.component(DataComponentTypes.ENCHANTABLE, new EnchantableComponent(1)));
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return stack -> stack.isOf(ModItems.ICE_ARROW_ITEM);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {

        if (!user.isInCreativeMode() && countIceArrows(user) < AMMO_COST) {
            return ActionResult.FAIL;
        }

        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }
    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) {
            return false;
        }

        int useTicks = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float pull = BowItem.getPullProgress(useTicks);

        if (pull < 0.1F) {
            return false;
        }

        boolean fullyCharged = pull >= 1.0F;

        if (!player.isInCreativeMode() && countIceArrows(player) < AMMO_COST) {
            return false;
        }

        if (world instanceof ServerWorld serverWorld) {
            shootShotgun(serverWorld, player, stack, pull, fullyCharged);
        }

        if (!player.isInCreativeMode()) {
            consumeIceArrows(player, AMMO_COST);
        }

        stack.damage(1, player, LivingEntity.getSlotForHand(player.getActiveHand()));

        world.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT,
                SoundCategory.PLAYERS,
                1.0F,
                1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + pull * 0.5F
        );

        if (fullyCharged) {
            world.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.BLOCK_GLASS_BREAK,
                    SoundCategory.PLAYERS,
                    0.9F,
                    1.15F
            );
        }

        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        nbt.putBoolean(FULL_CHARGE_SOUND_PLAYED_KEY, false);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

        player.incrementStat(Stats.USED.getOrCreateStat(this));
        return true;
    }
    private static final String FULL_CHARGE_SOUND_PLAYED_KEY = "FullChargeSoundPlayed";
    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);

        if (!(entity instanceof PlayerEntity player)) {
            return;
        }

        boolean usingThisBow = player.isUsingItem() && player.getActiveItem() == stack;

        if (!usingThisBow) {
            stack.remove(DataComponentTypes.CUSTOM_DATA);
            return;
        }

        int useTicks = this.getMaxUseTime(stack, player) - player.getItemUseTimeLeft();
        float pull = BowItem.getPullProgress(useTicks);

        boolean alreadyPlayed = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT)
                .copyNbt()
                .getBoolean(FULL_CHARGE_SOUND_PLAYED_KEY)
                .orElse(false);

        if (pull >= 1.0F) {
            if (!alreadyPlayed) {
                world.playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.BLOCK_GLASS_BREAK,
                        SoundCategory.PLAYERS,
                        0.9F,
                        1.4F
                );

                NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
                nbt.putBoolean(FULL_CHARGE_SOUND_PLAYED_KEY, true);
                stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
            }
        } else {
            if (alreadyPlayed) {
                NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
                nbt.putBoolean(FULL_CHARGE_SOUND_PLAYED_KEY, false);
                stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
            }
        }
    }
    private void shootShotgun(ServerWorld world, PlayerEntity shooter, ItemStack bowStack, float pull, boolean fullyCharged) {
        for (int i = 0; i < SHOT_COUNT; i++) {
            IceArrowEntity arrow = new IceArrowEntity(world, shooter, new ItemStack(ModItems.ICE_ARROW_ITEM), bowStack);

            float yawOffset = (world.random.nextFloat() - 0.5F) * 18.0F;
            float pitchOffset = (world.random.nextFloat() - 0.5F) * 10.0F;

            arrow.setVelocity(
                    shooter,
                    shooter.getPitch() + pitchOffset,
                    shooter.getYaw() + yawOffset,
                    0.0F,
                    pull * 3.0F,
                    1.6F
            );

            arrow.setCritical(fullyCharged);
            arrow.pickupType = IceArrowEntity.PickupPermission.DISALLOWED;
            arrow.setIceTipped(fullyCharged);

            // TODO: 固定 120 点弹射物伤害
            arrow.setDamage(BASE_PROJECTILE_DAMAGE);
            arrow.setTrueDamage(true, false);

            world.spawnEntity(arrow);
        }
    }

    private static int countIceArrows(PlayerEntity player) {
        int total = 0;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(ModItems.ICE_ARROW_ITEM)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private static void consumeIceArrows(PlayerEntity player, int amount) {
        int remain = amount;

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isOf(ModItems.ICE_ARROW_ITEM)) continue;

            int remove = Math.min(remain, stack.getCount());
            stack.decrement(remove);
            remain -= remove;

            if (remain <= 0) {
                return;
            }
        }
    }
}