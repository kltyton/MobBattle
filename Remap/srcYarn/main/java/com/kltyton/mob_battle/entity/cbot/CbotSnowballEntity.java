package com.kltyton.mob_battle.entity.cbot;

import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CbotSnowballEntity extends ProjectileEntity {
    private float physicalDamage;
    private float magicDamage;
    private final ItemStack displayStack = new ItemStack(Items.SNOWBALL);

    public CbotSnowballEntity(EntityType<? extends CbotSnowballEntity> entityType, World world) {
        super(entityType, world);
    }

    public void configure(LivingEntity owner, Vec3d position, Vec3d velocity, float physicalDamage, float magicDamage) {
        this.setOwner(owner);
        this.setPosition(position);
        this.setVelocity(velocity);
        this.physicalDamage = physicalDamage;
        this.magicDamage = magicDamage;
        this.velocityModified = true;
    }

    public ItemStack getDisplayStack() {
        return this.displayStack;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    @Override
    protected void readCustomData(ReadView view) {
    }

    @Override
    protected void writeCustomData(WriteView view) {
    }

    @Override
    public void tick() {
        super.tick();
        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().add(0.0D, -0.03D, 0.0D));
        if (this.getWorld().isClient()) {
            return;
        }
        if (this.age > 80 || this.getWorld().getBlockState(this.getBlockPos()).isSolidBlock(this.getWorld(), this.getBlockPos())) {
            this.discard();
            return;
        }
        Entity owner = this.getOwner();
        Box box = this.getBoundingBox().expand(0.35D);
        for (LivingEntity target : ((ServerWorld) this.getWorld()).getEntitiesByClass(LivingEntity.class, box,
                living -> EntityUtil.isValidSummonCombatTarget(this, owner, living))) {
            if (this.physicalDamage > 0.0F) {
                target.timeUntilRegen = 0;
                if (owner instanceof LivingEntity livingOwner) {
                    target.damage((ServerWorld) this.getWorld(), this.getDamageSources().mobProjectile(this, livingOwner), this.physicalDamage);
                } else {
                    target.damage((ServerWorld) this.getWorld(), this.getDamageSources().thrown(this, this), this.physicalDamage);
                }
            }
            if (this.magicDamage > 0.0F) {
                target.timeUntilRegen = 0;
                target.damage((ServerWorld) this.getWorld(), this.getDamageSources().indirectMagic(this, owner == null ? this : owner), this.magicDamage);
            }
            this.discard();
            return;
        }
    }
}
