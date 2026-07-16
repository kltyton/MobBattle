package com.kltyton.mob_battle.items.tool.sword;

import com.kltyton.mob_battle.entity.projectile.ElementalSwordProjectileEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ElementalSwordItem extends Item {
    private static final float THROW_POWER = 1.5F;

    private final int projectileMode;
    private final Item visualItem;
    private final int durabilityCost;
    private final int cooldownTicks;

    public ElementalSwordItem(Properties properties, int projectileMode, Item visualItem, int durabilityCost, int cooldownTicks) {
        super(properties);
        this.projectileMode = projectileMode;
        this.visualItem = visualItem;
        this.durabilityCost = durabilityCost;
        this.cooldownTicks = cooldownTicks;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (cooldownTicks > 0 && player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.FAIL;
        }
        if (level instanceof ServerLevel world) {
            shoot(world, player, projectileMode, new ItemStack(visualItem), THROW_POWER);
            if (!player.getAbilities().instabuild && durabilityCost > 0) {
                stack.hurtAndBreak(durabilityCost, player, hand);
            }
        }
        if (cooldownTicks > 0) {
            player.getCooldowns().addCooldown(stack, cooldownTicks);
        }
        level.playSound(null, player.blockPosition(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.8F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    protected static void shoot(ServerLevel world, Player player, int mode, ItemStack visualStack, float power) {
        Vec3 direction = player.getViewVector(1.0F).normalize();
        ElementalSwordProjectileEntity projectile = new ElementalSwordProjectileEntity(world, player, visualStack, mode);
        projectile.setPos(player.getEyePosition().add(direction.scale(0.8D)));
        projectile.shoot(direction.x, direction.y, direction.z, power, 0.0F);
        world.addFreshEntity(projectile);
    }
}
