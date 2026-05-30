package com.kltyton.mob_battle.mixin.silverfishentity;

import com.kltyton.mob_battle.entity.silverfish.silverfish.LongWhipSilverfishEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Silverfish.class)
public abstract class SilverfishEntityMixin extends Monster {
    protected SilverfishEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }
    @Redirect(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 4))
    public void doNotInfestStoneForModSilverfish(GoalSelector instance, int priority, Goal goal) {
        if ("mob_battle".equals(BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()).getNamespace())) {
            return;
        }
        instance.addGoal(priority, goal);
    }
    @Redirect(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 5))
    public void initGoals(GoalSelector instance, int priority, Goal goal) {
        instance.addGoal(priority,  new HurtByTargetGoal(this, Creeper.class, Witch.class).setAlertOthers(Creeper.class, Witch.class));
    }
    @Redirect(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 6))
    public void initGoals2(GoalSelector instance, int priority, Goal goal) {
        instance.addGoal(priority, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, (entity, world) -> !(entity instanceof Witch) && !(entity instanceof Creeper) && !(entity instanceof Silverfish)));
    }
    /**
     * @author kltyton
     * @reason fuck 长鞭魔虫
     */
    @Overwrite
    public void tick() {
        if (!((Object)this instanceof LongWhipSilverfishEntity)) this.yBodyRot = this.getYRot();
        super.tick();
    }
}
