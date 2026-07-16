package com.kltyton.mob_battle.items.tool.sword;

import com.kltyton.mob_battle.entity.littleperson.skillentity.ironmanbullet.IronManBulletEntity;
import com.kltyton.mob_battle.items.cooldown.StackBoundCooldowns;
import com.kltyton.mob_battle.items.ModFabricItem;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class IronManMissileLauncherItem extends Item implements ModFabricItem {
    public static final String COOLDOWN_ID = "iron_man_missile_launcher";
    public static final int COOLDOWN_TICKS = 60 * 20;
    private static final int BULLET_COUNT = 12;

    public IronManMissileLauncherItem(Properties properties) {
        super(properties);
    }

    @Override
    public void addStatusEffect(LivingEntity target, LivingEntity attacker) {
        if (!(attacker instanceof Player player) || !(player.level() instanceof ServerLevel world)) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof IronManMissileLauncherItem)
                || StackBoundCooldowns.isCoolingDown(player, stack, COOLDOWN_ID, COOLDOWN_TICKS)) {
            return;
        }

        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 8 * 20, 4), player);
        spawnBullets(world, player, target);
        StackBoundCooldowns.start(player, stack, COOLDOWN_ID, COOLDOWN_TICKS);
        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(1, player, InteractionHand.MAIN_HAND);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, EquipmentSlot slot) {
        StackBoundCooldowns.ensureGroup(stack, COOLDOWN_ID, COOLDOWN_TICKS);
    }

    private static void spawnBullets(ServerLevel world, Player player, LivingEntity target) {
        Vec3 center = player.position().add(0.0D, 1.2D, 0.0D);
        Vec3 toTarget = target.position().subtract(player.position());
        Direction.Axis mainAxis = Math.abs(toTarget.x) > Math.abs(toTarget.z)
                ? Direction.Axis.X
                : Direction.Axis.Z;
        for (int i = 0; i < BULLET_COUNT; i++) {
            double angle = (Math.PI * 2.0D * i) / BULLET_COUNT;
            Vec3 start = center.add(Math.cos(angle) * 2.0D, 0.25D + (i % 3) * 0.35D, Math.sin(angle) * 2.0D);
            IronManBulletEntity bullet = new IronManBulletEntity(world, player, target, mainAxis);
            bullet.setPos(start.x, start.y, start.z);
            bullet.retarget(target, mainAxis);
            world.addFreshEntity(bullet);
        }
        world.playSound(null, player.blockPosition(), SoundEvents.SNOW_GOLEM_SHOOT, SoundSource.PLAYERS, 1.3F, 0.75F);
    }
}
