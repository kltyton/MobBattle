package com.kltyton.mob_battle.items.tool.bow;

import com.kltyton.mob_battle.entity.bullet.IceArrowEntity;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.tool.BaseBow;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class IceBowItem extends BaseBow {
    private static final int AMMO_COST = 5;
    private static final int SHOT_COUNT = 10;
    private static final float BASE_PROJECTILE_DAMAGE = 120.0F;

    public IceBowItem(Properties settings) {
        super(settings.component(DataComponents.ENCHANTABLE, new Enchantable(1)));
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.is(ModItems.ICE_ARROW_ITEM);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {

        if (!user.hasInfiniteMaterials() && countIceArrows(user) < AMMO_COST) {
            return InteractionResult.FAIL;
        }

        user.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }
    @Override
    public boolean releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof Player player)) {
            return false;
        }

        int useTicks = this.getUseDuration(stack, user) - remainingUseTicks;
        float pull = BowItem.getPowerForTime(useTicks);

        if (pull < 0.1F) {
            return false;
        }

        boolean fullyCharged = pull >= 1.0F;

        if (!player.hasInfiniteMaterials() && countIceArrows(player) < AMMO_COST) {
            return false;
        }

        if (world instanceof ServerLevel serverWorld) {
            shootShotgun(serverWorld, player, stack, pull, fullyCharged);
        }

        if (!player.hasInfiniteMaterials()) {
            consumeIceArrows(player, AMMO_COST);
        }

        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));

        world.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ARROW_SHOOT,
                SoundSource.PLAYERS,
                1.0F,
                1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + pull * 0.5F
        );

        if (fullyCharged) {
            world.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.GLASS_BREAK,
                    SoundSource.PLAYERS,
                    0.9F,
                    1.15F
            );
        }

        CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        nbt.putBoolean(FULL_CHARGE_SOUND_PLAYED_KEY, false);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

        player.awardStat(Stats.ITEM_USED.get(this));
        return true;
    }
    private static final String FULL_CHARGE_SOUND_PLAYED_KEY = "FullChargeSoundPlayed";
    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);

        if (!(entity instanceof Player player)) {
            return;
        }

        boolean usingThisBow = player.isUsingItem() && player.getUseItem() == stack;

        if (!usingThisBow) {
            stack.remove(DataComponents.CUSTOM_DATA);
            return;
        }

        int useTicks = this.getUseDuration(stack, player) - player.getUseItemRemainingTicks();
        float pull = BowItem.getPowerForTime(useTicks);

        boolean alreadyPlayed = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag()
                .getBoolean(FULL_CHARGE_SOUND_PLAYED_KEY)
                .orElse(false);

        if (pull >= 1.0F) {
            if (!alreadyPlayed) {
                world.playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.GLASS_BREAK,
                        SoundSource.PLAYERS,
                        0.9F,
                        1.4F
                );

                CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                nbt.putBoolean(FULL_CHARGE_SOUND_PLAYED_KEY, true);
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
            }
        } else {
            if (alreadyPlayed) {
                CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                nbt.putBoolean(FULL_CHARGE_SOUND_PLAYED_KEY, false);
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
            }
        }
    }
    private void shootShotgun(ServerLevel world, Player shooter, ItemStack bowStack, float pull, boolean fullyCharged) {
        Vec3 look = shooter.getViewVector(1.0F);
        Vec3 eyePos = shooter.getEyePosition();
        Vec3 spawnPos = eyePos.add(look.scale(0.6)); // 在玩家前方生成，避免打到自己

        for (int i = 0; i < SHOT_COUNT; i++) {
            IceArrowEntity arrow = new IceArrowEntity(world, shooter, new ItemStack(ModItems.ICE_ARROW_ITEM), bowStack);

            arrow.setOwner(shooter);
            arrow.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

            boolean centerShot = i == 0;
            float yawOffset = centerShot ? 0.0F : (world.random.nextFloat() - 0.5F) * 18.0F;
            float pitchOffset = centerShot ? 0.0F : (world.random.nextFloat() - 0.5F) * 10.0F;
            float divergence = centerShot ? 0.0F : 1.6F;

            arrow.shootFromRotation(
                    shooter,
                    shooter.getXRot() + pitchOffset,
                    shooter.getYRot() + yawOffset,
                    0.0F,
                    pull * 3.0F,
                    divergence
            );

            arrow.setCritArrow(fullyCharged);
            arrow.pickup = net.minecraft.world.entity.projectile.AbstractArrow.Pickup.DISALLOWED;
            arrow.setIceTipped(fullyCharged);
            arrow.setBaseDamage(BASE_PROJECTILE_DAMAGE);
            arrow.setTrueDamage(true, false);

            world.addFreshEntity(arrow);
        }
    }


    private static int countIceArrows(Player player) {
        int total = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ModItems.ICE_ARROW_ITEM)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private static void consumeIceArrows(Player player, int amount) {
        int remain = amount;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.is(ModItems.ICE_ARROW_ITEM)) continue;

            int remove = Math.min(remain, stack.getCount());
            stack.shrink(remove);
            remain -= remove;

            if (remain <= 0) {
                return;
            }
        }
    }
}
