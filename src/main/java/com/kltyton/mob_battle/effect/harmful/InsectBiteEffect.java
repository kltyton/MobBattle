package com.kltyton.mob_battle.effect.harmful;

import com.kltyton.mob_battle.utils.EntityUtil;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class InsectBiteEffect extends MobEffect {

    public InsectBiteEffect() {
        super(
                MobEffectCategory.HARMFUL, // 设置为有害效果
                0x8B0000 // 暗红色
        );
    }
    @Override
    public void onMobHurt(ServerLevel world, LivingEntity entity,
                               int amplifier, DamageSource source, float amount) {
        super.onMobHurt(world, entity, amplifier, source, amount);
        // 生成位置计算
        BlockPos pos = entity.blockPosition().offset(
                world.random.nextInt(5) - 2,
                0,
                world.random.nextInt(5) - 2
        );
        Registry<EntityType<?>> entityRegistry = world.registryAccess().lookupOrThrow(Registries.ENTITY_TYPE);
        Holder<EntityType<?>> wardenEntry = entityRegistry.wrapAsHolder(EntityType.WARDEN);


        try {
            // 模拟summon命令的生成逻辑
            Entity warden = SummonCommand.createEntity(
                    new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(pos), Vec2.ZERO,
                            world, 4, "", Component.literal(""), world.getServer(), null),
                    (Holder.Reference<EntityType<?>>) wardenEntry,
                    Vec3.atCenterOf(pos),
                    new CompoundTag(),
                    true
            );

            if (warden instanceof Warden) {
                warden.setPos(EntityUtil.findSafeSpawnPosition(world, warden, pos.getCenter()).orElse(pos.getCenter()));
                setupWardenAttributes((Warden) warden);
                ((Mob)warden).setTarget(entity); // 设置攻击目标
            }
        } catch (CommandSyntaxException e) {
            //e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected void setupWardenAttributes(Warden warden) {
        // 设置最大生命值
        AttributeInstance maxHealth = warden.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(100.0);
            warden.setHealth(100.0f);
        }

        // 设置攻击伤害
        AttributeInstance attackDamage = warden.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.setBaseValue(20.0);
        }
    }
}
