package com.kltyton.mob_battle.event.item;

import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class GuardianSealItemEvent {
    public static void init() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof WitherSkeletonKingEntity) {
                if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
                    transformTotemToEmerald(player);
                }
            }
        });
    }
    private static void transformTotemToEmerald(ServerPlayerEntity player) {
        // 3. 遍历玩家背包寻找不死图腾
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(ModItems.GUARDIAN_SEAL)) {
                int count = stack.getCount();
                player.getInventory().setStack(i, new ItemStack(ModItems.FILLING_SEAL, count));
                break;
            }
        }
    }
}
