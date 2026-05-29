package com.kltyton.mob_battle.items.tool;

import com.kltyton.mob_battle.items.ModFabricItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BaseBow extends BowItem implements ModFabricItem {

    public BaseBow(Settings settings) {
        super(settings);
    }
    @Override
    protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        super.shoot(shooter, projectile, index, speed, divergence, yaw, target);
    }
    @Override
    protected ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        return super.createArrowEntity(world, shooter, weaponStack, projectileStack, critical);
    }
}
