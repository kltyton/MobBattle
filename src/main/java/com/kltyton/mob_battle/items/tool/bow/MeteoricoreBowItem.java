package com.kltyton.mob_battle.items.tool.bow;

import com.kltyton.mob_battle.items.tool.BaseBow;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MeteoricoreBowItem extends BaseBow {
    public MeteoricoreBowItem(Properties settings) {
        super(settings);
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        super.shootProjectile(shooter, projectile, index, speed, divergence, yaw, target);
        projectile.setNoGravity(true);
        if (projectile instanceof Arrow arrowEntity) {
            arrowEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 2));
        }
    }
    @Override
    protected Projectile createProjectile(Level world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        Projectile persistentProjectileEntity = super.createProjectile(world, shooter, weaponStack, projectileStack, critical);
        persistentProjectileEntity.setNoGravity(true);
        if (persistentProjectileEntity instanceof Arrow arrowEntity) {
            arrowEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 2));
        }
        return persistentProjectileEntity;
    }
}
