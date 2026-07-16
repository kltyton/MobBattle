package com.kltyton.mob_battle.items.scroll;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.skillentity.SkillProjectileEntity;
import com.kltyton.mob_battle.entity.skull.mage.NewSkullMageEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SkullMageScrollItem extends Item {
    public SkullMageScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if (!(world instanceof ServerLevel serverWorld)) {
            return InteractionResult.SUCCESS;
        }
        LivingEntity target = EntityUtil.getClosestNearbyEntity(user, LivingEntity.class, 40.0D, EntityUtil.TeamFilter.EXCLUDE_TEAM,
                living -> EntityUtil.isValidSummonCombatTarget(user, user, living), null);
        shootSkullHead(serverWorld, user);
        NewSkullMageEntity.summonSkeletons(user, target, user.position(), 2, 2);
        user.playSound(SoundEvents.WITHER_SHOOT, 1.0F, 1.0F);
        if (!user.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    private void shootSkullHead(ServerLevel world, Player user) {
        Vec3 direction = user.getViewVector(1.0F).normalize();
        SkillProjectileEntity projectile = ModEntities.SKELETON_HEAD_PROJECTILE.create(world, EntitySpawnReason.MOB_SUMMONED);
        if (projectile == null) {
            return;
        }
        projectile.configure(user, user.getEyePosition().add(direction.scale(0.8D)), direction.scale(1.35D),
                20.0F, 0.0F, false, false, true, 90);
        projectile.setExplosionRadius(3.0D);
        world.addFreshEntity(projectile);
    }
}
