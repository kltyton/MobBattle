package com.kltyton.mob_battle.items.food;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.meteorite.MeteoriteEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ThousandBlossomedImmortalFruit extends Item {
    public ThousandBlossomedImmortalFruit(Item.Properties settings) {
        super(settings);
    }
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);

        // --- 核心修改点 ---
        // 1. 复制一份当前的物品堆（包含所有 Data Components，如自定义名称、附魔等）
        ItemStack remainder = itemStack.copy();
        // 2. 确保返还的物品数量为 1（防止吃一个变一组）
        remainder.setCount(1);
        // 3. 将这个带有属性的副本设为该物品堆的“剩余物品”组件
        itemStack.set(DataComponents.USE_REMAINDER, new UseRemainder(remainder));

        return super.use(world, user, hand);
    }
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (!world.isClientSide) {
            // ------------------
            // 在玩家上方 30 格生成陨石
            MeteoriteEntity meteorite = new MeteoriteEntity(ModEntities.METEORITE, world, user, 5.0f, false, 0);
            Vec3 spawnPos = user.position().add(0, 30, 0);
            meteorite.snapTo(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
            meteorite.setDeltaMovement(0, -1.5, 0);
            world.addFreshEntity(meteorite);
        }
        return super.finishUsingItem(stack, world, user);
    }
}
