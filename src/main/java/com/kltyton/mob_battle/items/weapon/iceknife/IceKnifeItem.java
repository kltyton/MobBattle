package com.kltyton.mob_battle.items.weapon.iceknife;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.icesoldier.IceSoldierEntity;
import com.kltyton.mob_battle.entity.registry.IceSoldierEntityTypes;
import com.kltyton.mob_battle.items.ModFabricItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 冰刀的服务端技能边界。
 *
 * <p>右键只在服务端创建冰兵并扣除实际手持物品的耐久；左键剑气由
 * {@code PlayerAttackEvent} 的实体攻击入口与服务端原版挥手入口共同进入，
 * 客户端 {@code LeftClickEvent} 的后续网络包沿用同一去重边界，
 * 避免客户端直接决定实体、伤害或耐久。</p>
 */
public final class IceKnifeItem extends Item implements ModFabricItem {
    public static final int MAX_DURABILITY = 200;
    public static final int SOLDIER_COUNT = 8;
    public static final int SOLDIER_DURABILITY_COST = 5;
    public static final int SOLDIER_SUMMON_COOLDOWN_TICKS = 400;
    public static final float SWORD_ENERGY_DAMAGE = 50.0F;

    public IceKnifeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() != this) {
            return InteractionResult.FAIL;
        }
        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.FAIL;
        }
        if (level instanceof ServerLevel serverLevel) {
            if (!player.getAbilities().instabuild
                    && stack.getMaxDamage() - stack.getDamageValue() < SOLDIER_DURABILITY_COST) {
                return InteractionResult.FAIL;
            }
            summonSoldiers(serverLevel, player);
            player.getCooldowns().addCooldown(stack, SOLDIER_SUMMON_COOLDOWN_TICKS);
            if (!player.getAbilities().instabuild) {
                stack.hurtAndBreak(SOLDIER_DURABILITY_COST, player, hand);
            }
            level.playSound(null, player.blockPosition(), SoundEvents.GLASS_BREAK,
                    SoundSource.PLAYERS, 0.8F, 1.2F);
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * 在玩家周围固定创建八个冰兵。实体创建失败时不伪造数量，也不把客户端实体加入世界。
     */
    public static int summonSoldiers(ServerLevel level, Player owner) {
        int summoned = 0;
        for (int index = 0; index < SOLDIER_COUNT; index++) {
            IceSoldierEntity soldier = IceSoldierEntityTypes.ICE_SOLDIER.create(
                    level, EntitySpawnReason.MOB_SUMMONED);
            if (soldier == null) {
                continue;
            }
            double angle = Math.PI * 2.0D * index / SOLDIER_COUNT;
            Vec3 position = owner.position().add(Math.cos(angle) * 2.0D, 0.0D, Math.sin(angle) * 2.0D);
            soldier.snapTo(position.x, position.y, position.z, owner.getYRot(), owner.getXRot());
            soldier.setSummonOwner(owner);
            level.addFreshEntity(soldier);
            summoned++;
        }
        return summoned;
    }

    /**
     * 创建与冰人实体相同类型的剑气实体；命中合法性由现有 SkillProjectileEntity 服务端校验。
     */
    public static boolean shootSwordEnergy(ServerLevel level, Player owner) {
        var projectile = ModEntities.ICE_SWORD_ENERGY.create(level, EntitySpawnReason.MOB_SUMMONED);
        if (projectile == null) {
            return false;
        }
        Vec3 direction = owner.getViewVector(1.0F).normalize();
        Vec3 start = owner.getEyePosition().add(direction.scale(0.8D));
        projectile.configure(owner, start, direction.scale(0.75D),
                SWORD_ENERGY_DAMAGE, 0.0F, true, true, false, 45);
        projectile.setNoGravity(true);
        level.addFreshEntity(projectile);
        level.playSound(null, owner.blockPosition(), SoundEvents.GLASS_BREAK,
                SoundSource.PLAYERS, 0.8F, 1.2F);
        return true;
    }

    /** 服务端仅在攻击冷却完全恢复时发射剑气，空挥和实体攻击共用同一蓄力边界。 */
    @Override
    public void onLeftClickStart(Player player, ItemStack stack, boolean isServer) {
        if (!isServer
                || !(player.level() instanceof ServerLevel serverLevel)
                || stack.getItem() != this
                || player.getAttackStrengthScale(0.0F) < 1.0F) {
            return;
        }
        if (shootSwordEnergy(serverLevel, player)) {
            player.resetAttackStrengthTicker();
        }
    }
}
