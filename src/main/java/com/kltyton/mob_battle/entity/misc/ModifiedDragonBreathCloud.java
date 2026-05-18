package com.kltyton.mob_battle.entity.misc;

import com.kltyton.mob_battle.entity.ModEntities;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ModifiedDragonBreathCloud extends AreaEffectCloudEntity {

    public ModifiedDragonBreathCloud(EntityType<? extends AreaEffectCloudEntity> type, World world) {
        super(type, world);
    }

    public ModifiedDragonBreathCloud(World world, double x, double y, double z) {
        this(ModEntities.MODIFIED_DRAGON_BREATH_CLOUD, world);
        this.setPosition(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) return;
        if (this.age >= 200) {
            this.discard();
        }
        // 每4 tick 造成一次伤害（性能好）
        if (this.age % 4 == 0) {
            ServerWorld serverWorld = (ServerWorld) this.getWorld();
            DamageSource source = this.getDamageSources().magic();  // 或 custom source

            for (LivingEntity living : serverWorld.getNonSpectatingEntities(
                    LivingEntity.class, this.getBoundingBox().expand(0.5))) {
                if (!living.isSpectator()) {
                    if (living.isTeammate(this.getOwner())) continue;
                    living.damage(serverWorld, source, 50.0F);  // 50点魔法伤害
                }
            }
        }
    }
}
