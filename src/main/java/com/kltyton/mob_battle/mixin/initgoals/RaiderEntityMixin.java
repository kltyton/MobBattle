package com.kltyton.mob_battle.mixin.initgoals;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raider.class)
public abstract class RaiderEntityMixin extends PatrollingMonster {
    protected RaiderEntityMixin(EntityType<? extends PatrollingMonster> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "registerGoals", at =@At(value = "HEAD"))
    private void initGoals(CallbackInfo ci) {
        // 攻击所有活体实体，但排除自己和凋零
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
                this,
                LivingEntity.class,
                0,
                true,
                true,
                this::isValidTarget
        ));
    }

    @Unique
    private boolean isValidTarget(LivingEntity target, ServerLevel world) {
        if ((Object) this instanceof Witch witchEntity) {
            return !(target instanceof Raider) && !witchEntity.isAlliedTo(target) && !(target instanceof Silverfish) && !(target instanceof Creeper);
        }
        return !(target instanceof Raider) && !this.isAlliedTo(target);
    }
}
