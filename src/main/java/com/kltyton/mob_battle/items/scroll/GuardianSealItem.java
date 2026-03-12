package com.kltyton.mob_battle.items.scroll;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.skull.king.SkullKingEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class GuardianSealItem extends Item {
    private final boolean isBlackSeal;

    public GuardianSealItem(Settings settings, boolean isBlackSeal) {
        super(settings);
        this.isBlackSeal = isBlackSeal;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            if (this.isBlackSeal) {
                if (user.getInventory().count(Items.BONE_BLOCK) >= 64) {
                    user.getInventory().remove(itemStack -> itemStack.isOf(Items.BONE_BLOCK), 64, user.getInventory());
                    spawnSkullKing(serverWorld, user);
                } else {
                    user.sendMessage(Text.literal("材料不足！使用充盈法印需要 64 个骨块"), true);
                    return ActionResult.FAIL;
                }
            } else {
                spawnSkullKing(serverWorld, user);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    /**
     * 提取出的通用召唤逻辑
     */
    private void spawnSkullKing(ServerWorld world, PlayerEntity user) {
        SkullKingEntity skeleton = ModEntities.SKULL_KING.create(world, SpawnReason.MOB_SUMMONED);
        if (skeleton != null) {
            skeleton.refreshPositionAndAngles(user.getBlockPos(), user.getYaw(), 0);
            // 设置属性
            var maxHealthAttr = skeleton.getAttributeInstance(EntityAttributes.MAX_HEALTH);
            if (maxHealthAttr != null) {
                maxHealthAttr.setBaseValue(1000.0);
            }
            skeleton.setHealth(1000.0f);
            world.spawnEntity(skeleton);
            // 队伍尝试
            EntityUtil.joinSameTeam(skeleton, user);
        }
    }
}
