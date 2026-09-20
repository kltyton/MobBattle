package com.kltyton.mob_battle.items.shield;

import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;

/**
 * 小人盾牌的物品行为与服务端自动格挡策略。
 *
 * <p>右键格挡完全使用 Minecraft 的 {@link DataComponents#BLOCKS_ATTACKS} 组件。
 * 自动格挡只在服务端伤害入口读取真实手部物品栈，客户端不能伪造格挡结果。</p>
 */
public final class LittlePersonShieldItem extends ShieldItem {
    /** 自动格挡概率。 */
    public static final float AUTOMATIC_BLOCK_CHANCE = 0.08F;
    /** 自动格挡允许的原始伤害上限（含边界）。 */
    public static final float AUTOMATIC_BLOCK_DAMAGE_LIMIT = 50.0F;

    public LittlePersonShieldItem(Properties properties) {
        super(properties);
    }

    /** 注册服务端自动格挡事件；调用方应在模组初始化阶段调用一次。 */
    public static void initAutomaticBlocking() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(LittlePersonShieldItem::allowAutomaticBlock);
    }

    /** 判断原始伤害是否仍在自动格挡的伤害上限内。 */
    public static boolean isAutomaticBlockDamageWithinLimit(float amount) {
        return amount <= AUTOMATIC_BLOCK_DAMAGE_LIMIT;
    }

    private static boolean allowAutomaticBlock(LivingEntity entity, DamageSource source, float amount) {
        if (!(entity instanceof Player player) || !(player.level() instanceof ServerLevel level) || amount <= 0.0F) {
            return true;
        }

        Entity attacker = source.getEntity();
        if (attacker instanceof LivingEntity livingAttacker && player.isAlliedTo(livingAttacker)) {
            return true;
        }

        ItemStack shield = ItemStack.EMPTY;
        InteractionHand hand = InteractionHand.MAIN_HAND;
        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
        if (mainHand.getItem() instanceof LittlePersonShieldItem) {
            shield = mainHand;
        } else if (offHand.getItem() instanceof LittlePersonShieldItem) {
            shield = offHand;
            hand = InteractionHand.OFF_HAND;
        }
        if (shield.isEmpty() || source.is(DamageTypeTags.BYPASSES_SHIELD)) {
            return true;
        }
        if (source.getDirectEntity() instanceof AbstractArrow arrow && arrow.getPierceLevel() > 0) {
            return true;
        }

        Vec3 sourcePosition = source.getSourcePosition();
        if (sourcePosition == null) {
            return true;
        }
        Vec3 vectorToSource = sourcePosition.subtract(player.position());
        vectorToSource = new Vec3(vectorToSource.x, 0.0D, vectorToSource.z);
        if (vectorToSource.lengthSqr() < 1.0E-7D) {
            return true;
        }
        vectorToSource = vectorToSource.normalize();
        Vec3 viewVector = player.calculateViewVector(0.0F, player.getYHeadRot());
        double angle = Math.acos(Mth.clamp(vectorToSource.dot(viewVector), -1.0D, 1.0D));
        BlocksAttacks blocksAttacks = shield.get(DataComponents.BLOCKS_ATTACKS);
        if (blocksAttacks == null || blocksAttacks.bypassedBy().map(types -> types.contains(source.typeHolder())).orElse(false)) {
            return true;
        }
        if (!isAutomaticBlockDamageWithinLimit(amount)) {
            return true;
        }

        if (player.getRandom().nextFloat() >= AUTOMATIC_BLOCK_CHANCE) {
            return true;
        }
        float blockedDamage = blocksAttacks.resolveBlockedDamage(source, amount, angle);
        if (blockedDamage <= 0.0F) {
            return true;
        }

        blocksAttacks.hurtBlockingItem(level, shield, player, hand, blockedDamage);
        blocksAttacks.onBlocked(level, player);
        player.awardStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(blockedDamage * 10.0F));
        return false;
    }
}
