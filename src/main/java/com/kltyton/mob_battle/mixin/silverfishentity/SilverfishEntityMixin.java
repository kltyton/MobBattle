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
        instance.addGoal(priority, new HurtByTargetGoal(this, Witch.class).setAlertOthers(Witch.class));
    }
    @Redirect(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 6))
    public void initGoals2(GoalSelector instance, int priority, Goal goal) {
        instance.addGoal(priority, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, (entity, world) -> !(entity instanceof Witch) && !(entity instanceof Silverfish)));
    }
    /**
     * 覆盖 Minecraft 26.1.2 银鱼的极短 tick 包装，仅对长鞭魔虫跳过
     * {@code yBodyRot = getYRot()}，使其身体朝向由自定义动画控制，其余实体仍执行原版
     * 赋值并统一调用父类 tick。目标赋值是父类字段写入，当前映射下用字段级重定向会
     * 对字节码 owner 与写入次数形成更脆弱的依赖；上游调整 tick 实现时必须重新核对。
     *
     * @author kltyton
     * @reason 保留长鞭魔虫动画朝向控制，同时维持其他银鱼的 Minecraft 26.1.2 tick 语义。
     */
    @Overwrite
    public void tick() {
        if (!((Object)this instanceof LongWhipSilverfishEntity)) this.yBodyRot = this.getYRot();
        super.tick();
    }
}
