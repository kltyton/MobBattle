package com.kltyton.mob_battle.items.food;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.meteorite.MeteoriteEntity;
import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UseRemainderComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ThousandBlossomedImmortalFruit extends Item {
    public ThousandBlossomedImmortalFruit(Item.Settings settings) {
        super(settings);
    }
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        itemStack.set(DataComponentTypes.USE_REMAINDER,  new UseRemainderComponent(new ItemStack(ModItems.THOUSAND_BLOSSOMED_IMMORTAL_FRUIT)));
        return super.use(world, user, hand);
    }
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient) {
            // 在玩家上方 30 格生成陨石
            MeteoriteEntity meteorite = new MeteoriteEntity(ModEntities.METEORITE, world, user, 5.0f, false, 0);
            Vec3d spawnPos = user.getPos().add(0, 30, 0);
            meteorite.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
            meteorite.setVelocity(0, -1.5, 0);
            world.spawnEntity(meteorite);
        }
        return super.finishUsing(stack, world, user);
    }
}
