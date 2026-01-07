package com.kltyton.mob_battle.effect;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SummonCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class InsectBiteEffect extends StatusEffect {

    public InsectBiteEffect() {
        super(
                StatusEffectCategory.HARMFUL, // 设置为有害效果
                0x8B0000 // 暗红色
        );
    }
    @Override
    public void onEntityDamage(ServerWorld world, LivingEntity entity,
                               int amplifier, DamageSource source, float amount) {
        super.onEntityDamage(world, entity, amplifier, source, amount);
        // 生成位置计算
        BlockPos pos = entity.getBlockPos().add(
                world.random.nextInt(5) - 2,
                0,
                world.random.nextInt(5) - 2
        );

        // 创建自定义坚守者
        // 使用正确的生成方式
        Registry<EntityType<?>> entityRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.ENTITY_TYPE);
        RegistryEntry<EntityType<?>> wardenEntry = entityRegistry.getEntry(EntityType.WARDEN);


        try {
            // 模拟summon命令的生成逻辑
            Entity warden = SummonCommand.summon(
                    new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ofCenter(pos), Vec2f.ZERO,
                            world, 4, "", Text.literal(""), world.getServer(), null),
                    (RegistryEntry.Reference<EntityType<?>>) wardenEntry,
                    Vec3d.ofCenter(pos),
                    new NbtCompound(),
                    true
            );

            if (warden instanceof WardenEntity) {
                setupWardenAttributes((WardenEntity) warden);
                ((MobEntity)warden).setTarget(entity); // 设置攻击目标
            }
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }

    protected void setupWardenAttributes(WardenEntity warden) {
        // 设置最大生命值
        EntityAttributeInstance maxHealth = warden.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(100.0);
            warden.setHealth(100.0f);
        }

        // 设置攻击伤害
        EntityAttributeInstance attackDamage = warden.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.setBaseValue(20.0);
        }
    }
}
