package com.kltyton.mob_battle.items.scroll;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.skull.king.SkullKingEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class GuardianSealItem extends Item {
    private final boolean isBlackSeal;

    public GuardianSealItem(Properties settings, boolean isBlackSeal) {
        super(settings);
        this.isBlackSeal = isBlackSeal;
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if (!world.isClientSide && world instanceof ServerLevel serverWorld) {
            if (this.isBlackSeal) {
                if (user.getInventory().countItem(Items.BONE_BLOCK) >= 64) {
                    user.getInventory().clearOrCountMatchingItems(itemStack -> itemStack.is(Items.BONE_BLOCK), 64, user.getInventory());
                    spawnSkullKing(serverWorld, user);
                } else {
                    user.displayClientMessage(Component.literal("材料不足！使用充盈法印需要 64 个骨块"), true);
                    return InteractionResult.FAIL;
                }
            } else {
                spawnSkullKing(serverWorld, user);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    /**
     * 提取出的通用召唤逻辑
     */
    private void spawnSkullKing(ServerLevel world, Player user) {
        SkullKingEntity skeleton = ModEntities.SKULL_KING.create(world, EntitySpawnReason.MOB_SUMMONED);
        if (skeleton != null) {
            skeleton.snapTo(user.blockPosition(), user.getYRot(), 0);
            // 设置属性
            var maxHealthAttr = skeleton.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttr != null) {
                maxHealthAttr.setBaseValue(1000.0);
            }
            skeleton.setHealth(1000.0f);
            world.addFreshEntity(skeleton);
            // 队伍尝试
            EntityUtil.joinSameTeam(skeleton, user);
        }
    }
}
