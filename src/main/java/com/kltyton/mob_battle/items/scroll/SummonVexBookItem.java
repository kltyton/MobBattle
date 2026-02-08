package com.kltyton.mob_battle.items.scroll;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SummonVexBookItem extends Item {
    private final int summonCount;
    private final double magicDamage;
    private final double attackDamage;

    public SummonVexBookItem(Settings settings, int summonCount, double magicDamage, double attackDamage) {
        super(settings);
        this.summonCount = summonCount;
        this.magicDamage = magicDamage;
        this.attackDamage = attackDamage;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!world.isClient) {
            for (int i = 0; i < summonCount; i++) {
                VexEntity vex = EntityType.VEX.create(world, SpawnReason.MOB_SUMMONED);
                if (vex != null) {
                    // 在玩家周围随机位置生成
                    double offsetX = -3.0 + world.random.nextDouble() * 6.0;
                    double offsetZ = -3.0 + world.random.nextDouble() * 6.0;
                    vex.refreshPositionAndAngles(user.getX() + offsetX, user.getY() + 1, user.getZ() + offsetZ, user.getYaw(), 0);
                    // 设置攻击力
                    if (attackDamage != 0.0) vex.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(attackDamage);
                    if (magicDamage != 0.0) {
                        EntityAttributeInstance reductionInstance = vex.getAttributeInstance(ModEntityAttributes.MAGIC_DAMAGE);
                        if (reductionInstance != null) {
                            reductionInstance.setBaseValue(this.magicDamage);
                        }
                    }
                    world.spawnEntity(vex);
                    EntityUtil.joinSameTeam(vex, user);
                    itemStack.damage(1, user);
                }
            }
        }

        return ActionResult.SUCCESS;
    }
}
