package com.kltyton.mob_battle.items.tool;

import com.kltyton.mob_battle.items.ModFabricItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BaseBow extends BowItem implements ModFabricItem {

    public BaseBow(Properties settings) {
        super(settings);
    }
    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        super.shootProjectile(shooter, projectile, index, speed, divergence, yaw, target);
    }
    @Override
    protected Projectile createProjectile(Level world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        return super.createProjectile(world, shooter, weaponStack, projectileStack, critical);
    }
}
