package com.kltyton.mob_battle.items.scroll;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SummonVexBookItem extends Item {
    private final int summonCount;
    private final double magicDamage;
    private final double attackDamage;

    public SummonVexBookItem(Properties settings, int summonCount, double magicDamage, double attackDamage) {
        super(settings);
        this.summonCount = summonCount;
        this.magicDamage = magicDamage;
        this.attackDamage = attackDamage;
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        user.playSound(SoundEvents.EVOKER_PREPARE_ATTACK, 1f, 1f);
        if (!world.isClientSide) {
            for (int i = 0; i < summonCount; i++) {
                Vex vex = EntityType.VEX.create(world, EntitySpawnReason.MOB_SUMMONED);
                if (vex != null) {
                    // 在玩家周围随机位置生成
                    double offsetX = -3.0 + world.random.nextDouble() * 6.0;
                    double offsetZ = -3.0 + world.random.nextDouble() * 6.0;
                    vex.snapTo(user.getX() + offsetX, user.getY() + 1, user.getZ() + offsetZ, user.getYRot(), 0);
                    // 设置攻击力
                    if (attackDamage != 0.0) vex.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(attackDamage);
                    if (magicDamage != 0.0) {
                        AttributeInstance reductionInstance = vex.getAttribute(ModEntityAttributes.MAGIC_DAMAGE);
                        if (reductionInstance != null) {
                            reductionInstance.setBaseValue(this.magicDamage);
                        }
                    }
                    world.addFreshEntity(vex);
                    EntityUtil.joinSameTeam(vex, user);
                    itemStack.hurtWithoutBreaking(1, user);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }
}
