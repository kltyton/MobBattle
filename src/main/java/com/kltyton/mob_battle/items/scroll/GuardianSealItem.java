package com.kltyton.mob_battle.items.scroll;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.skull.king.SkullKingEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class GuardianSealItem extends Item {

    public GuardianSealItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            SkullKingEntity skeleton = ModEntities.SKULL_KING.create(world, SpawnReason.MOB_SUMMONED);
            if (skeleton != null) {
                skeleton.refreshPositionAndAngles(user.getBlockPos(), user.getYaw(), 0);
                skeleton.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(1000.0);
                skeleton.setHealth(1000.0f);
                world.spawnEntity(skeleton);
                // 队伍尝试（同上）
                EntityUtil.joinSameTeam(skeleton, user);
            }
        }
        return ActionResult.SUCCESS;
    }
}
