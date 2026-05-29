package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntities;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class IceManEntity extends RequestedLittlePersonEntity {
    private static final RawAnimation ICE_ATTACK_ANIM_2 = RawAnimation.begin().thenPlay("attack2_1");
    private final List<Integer> iceBombIds = new ArrayList<>();
    private boolean clone;

    public IceManEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, 6);
        this.blockChance = 20;
        this.blockDamageCap = 99.0F;
        this.autoSkillRange = 10.0D;
        setCooldownSeconds(10, 25, 25, 35, 30, 60);
        clearSkillCooldowns();
    }

    public static AttributeSupplier.Builder createLittlePersonAttributes() {
        return createRequestedAttributes(6000.0D, 50.0D, 0.48D, 40.0D, 0.0D);
    }

    @Override
    protected RawAnimation attackAnimation(int attackNumber) {
        if (attackNumber == 2) {
            return ICE_ATTACK_ANIM_2;
        }
        return super.attackAnimation(attackNumber);
    }

    @Override
    protected boolean canUseSkill(String skillName, LivingEntity target) {
        if (this.clone && "attack7".equals(skillName)) {
            return false;
        }
        if ("attack5".equals(skillName) && this.distanceTo(target) > 3.0D) {
            return false;
        }
        return super.canUseSkill(skillName, target);
    }

    @Override
    protected double skillRange(String skillName) {
        return switch (skillName) {
            case "attack3" -> 5.0D;
            case "attack5" -> 3.0D;
            default -> 10.0D;
        };
    }

    @Override
    protected void runAttack() {
        damageTarget(50.0F, 0.0F);
        //原来的克隆体20物理,8魔法
/*        if (this.clone) {
            damageTarget(20.0F, 8.0F);
        } else {
            damageTarget(50.0F, 0.0F);
        }*/
    }

    @Override
    protected void runSkill(int attack, int phase) {
        switch (attack) {
            case 2 -> damageTarget(0.0F, 20.0F);
            case 3 -> summonIceFangs();
            case 4 -> {
                if (phase == 0) {
                    summonIceBombs();
                } else {
                    dropIceBombs();
                }
            }
            case 5 -> shootIceSwordEnergy();
            case 6 -> areaDamage(3.0D, 100.0F, 0.0F);
            case 7 -> summonClones();
            default -> {
            }
        }
    }

    private void summonIceFangs() {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        Vec3 pos = this.position().add(this.getViewVector(1.0F).normalize().scale(3.0D));
        SkillVisualEntity fangs = ModEntities.ICE_FANGS.create(world, EntitySpawnReason.MOB_SUMMONED);
        if (fangs != null) {
            fangs.snapTo(pos.x, this.getY(), pos.z, this.getYRot(), 0.0F);
            fangs.configure(this, 80.0F, 8, 25, 1.0D, 0);
            world.addFreshEntity(fangs);
        }
    }

    private void summonIceBombs() {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        this.iceBombIds.clear();
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * 2.0D * i / 8.0D;
            Vec3 pos = this.position().add(Math.cos(angle) * 2.2D, this.getBbHeight() + 1.6D, Math.sin(angle) * 2.2D);
            SkillProjectileEntity bomb = ModEntities.ICE_BOMB.create(world, EntitySpawnReason.MOB_SUMMONED);
            if (bomb == null) {
                continue;
            }
            bomb.configure(this, pos, Vec3.ZERO, 90.0F, 0.0F, false, false, true, 90);
            bomb.setNoGravity(true);
            bomb.setExplosionRadius(3.0D);
            world.addFreshEntity(bomb);
            this.iceBombIds.add(bomb.getId());
        }
    }

    private void dropIceBombs() {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        for (int id : this.iceBombIds) {
            Entity entity = world.getEntity(id);
            if (entity instanceof SkillProjectileEntity bomb) {
                bomb.dropDown();
            }
        }
        this.iceBombIds.clear();
    }

    private void shootIceSwordEnergy() {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        SkillProjectileEntity projectile = ModEntities.ICE_SWORD_ENERGY.create(world, EntitySpawnReason.MOB_SUMMONED);
        if (projectile == null) {
            return;
        }
        Vec3 direction = this.getViewVector(1.0F).normalize();
        Vec3 start = this.getEyePosition().add(direction.scale(0.8D));
        projectile.configure(this, start, direction.scale(0.75D), 80.0F, 20.0F, true, true, false, 45);
        world.addFreshEntity(projectile);
        this.playSound(SoundEvents.GLASS_BREAK, 0.8F, 1.2F);
    }

    private void summonClones() {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        for (int i = 0; i < 20; i++) {
            IceManEntity cloneEntity = ModEntities.ICE_MAN.create(world, EntitySpawnReason.MOB_SUMMONED);
            if (cloneEntity == null) {
                continue;
            }
            double angle = Math.PI * 2.0D * i / 20.0D;
            Vec3 pos = this.position().add(Math.cos(angle) * 4.0D, 0.0D, Math.sin(angle) * 4.0D);
            cloneEntity.snapTo(pos.x, pos.y, pos.z, this.getYRot(), this.getXRot());
            cloneEntity.clone = true;
            cloneEntity.lifeTicks = 40 * 20;
            cloneEntity.setSummonOwner(this);
            cloneEntity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100.0D);
            cloneEntity.setHealth(100.0F);
            world.addFreshEntity(cloneEntity);
        }
    }
}
