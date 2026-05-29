package com.kltyton.mob_battle.event.item;

import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class GuardianSealItemEvent {
    public static void init() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof WitherSkeletonKingEntity) {
                if (damageSource.getEntity() instanceof ServerPlayer player) {
                    transformTotemToEmerald(player);
                }
            }
        });
    }
    private static void transformTotemToEmerald(ServerPlayer player) {
        // 3. 遍历玩家背包寻找不死图腾
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ModItems.GUARDIAN_SEAL)) {
                int count = stack.getCount();
                player.getInventory().setItem(i, new ItemStack(ModItems.FILLING_SEAL, count));
                break;
            }
        }
    }
}
