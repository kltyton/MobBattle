package com.kltyton.mob_battle.items.tool.sword;

import com.kltyton.mob_battle.animation.ModPlayerAnimationIds;
import com.kltyton.mob_battle.animation.PalMorePlayerAnimationServerHandler;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.skillentity.SkillProjectileEntity;
import com.kltyton.mob_battle.items.cooldown.StackBoundCooldowns;
import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.utils.TaskSchedulerUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BloodKnifeItem extends Item implements ModFabricItem {
    public static final String COOLDOWN_ID = "blood_knife";
    public static final int COOLDOWN_TICKS = 30 * 20;
    private static final int FALLBACK_RELEASE_TICKS = 14;
    private static final Set<UUID> PENDING_RELEASES = ConcurrentHashMap.newKeySet();

    public BloodKnifeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onLeftClickStart(Player player, ItemStack stack, boolean isServer) {
        if (StackBoundCooldowns.isCoolingDown(player, stack, COOLDOWN_ID, COOLDOWN_TICKS)) {
            return;
        }
        if (!isServer || !(player.level() instanceof ServerLevel)) {
            StackBoundCooldowns.start(player, stack, COOLDOWN_ID, COOLDOWN_TICKS);
            return;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            PENDING_RELEASES.add(serverPlayer.getUUID());
            PalMorePlayerAnimationServerHandler.play(serverPlayer, ModPlayerAnimationIds.BLOOD_KNIFE);
            TaskSchedulerUtil.runLater(FALLBACK_RELEASE_TICKS, () -> releasePendingSkill(serverPlayer));
        }
        StackBoundCooldowns.start(player, stack, COOLDOWN_ID, COOLDOWN_TICKS);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, EquipmentSlot slot) {
        StackBoundCooldowns.ensureGroup(stack, COOLDOWN_ID, COOLDOWN_TICKS);
    }

    public static void releasePendingSkill(ServerPlayer player) {
        if (!PENDING_RELEASES.remove(player.getUUID()) || !(player.level() instanceof ServerLevel world)) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof BloodKnifeItem)) {
            return;
        }
        for (float yawOffset : new float[]{-10.0F, 0.0F, 10.0F}) {
            SkillProjectileEntity projectile = ModEntities.BLOOD_SWORD_ENERGY.create(world, EntitySpawnReason.MOB_SUMMONED);
            if (projectile == null) {
                continue;
            }
            Vec3 direction = Vec3.directionFromRotation(player.getXRot(), player.getYRot() + yawOffset).normalize();
            Vec3 start = player.getEyePosition().add(direction.scale(0.8D));
            projectile.configure(player, start, direction.scale(0.85D), 100.0F, 0.0F, true, true, false, 45);
            projectile.setOwnerHealOnHit(0.2F);
            world.addFreshEntity(projectile);
        }
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 0.7F);
        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(4, player, InteractionHand.MAIN_HAND);
        }
    }
}
