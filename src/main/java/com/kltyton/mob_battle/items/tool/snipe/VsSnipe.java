package com.kltyton.mob_battle.items.tool.snipe;

import com.kltyton.mob_battle.entity.bullet.BulletEntity;
import com.kltyton.mob_battle.entity.bullet.ITrueDamageProjectile;
import com.kltyton.mob_battle.items.ModFabricItem;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.sounds.ModSounds;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class VsSnipe extends ProjectileWeaponItem implements ModFabricItem {
    private boolean charged = false;
    private boolean loaded = false;

    private static final CrossbowItem.ChargingSounds DEFAULT_LOADING_SOUNDS = new CrossbowItem.ChargingSounds(
            Optional.of(ModSounds.GUN_RELOAD_SOUND_EVENT_REFERENCE),
            null,
            null
    );

    public VsSnipe(Item.Properties settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return stack -> stack.is(ModItems.COMPRESSED_IRON_INGOT);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.is(ModItems.COMPRESSED_IRON_INGOT);
    }

    /**
     * Fabric 1.21.x 里用于控制“这个物品是否接受某个附魔”的核心入口。
     * 这里放行：
     * - 弓：力量 / 冲击 / 火矢
     * - 弩：快速装填 / 多重射击 / 穿透
     * 明确排除：无限
     *
     * 如果你本地 Yarn 映射的方法名不是 canBeEnchantedWith，而是 supportsEnchantment，
     * 只需要把这个 override 的方法名改成你环境里的那个即可，方法体不变。
     */
    @Override
    public boolean canBeEnchantedWith(ItemStack stack, Holder<Enchantment> enchantment, EnchantingContext context) {
        if (isInfinity(enchantment)) {
            return false;
        }

        if (isSupportedBowOrCrossbowEnchantment(enchantment)) {
            return true;
        }

        return super.canBeEnchantedWith(stack, enchantment, context);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        ChargedProjectiles chargedProjectilesComponent = itemStack.get(DataComponents.CHARGED_PROJECTILES);
        if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
            this.shootAll(world, user, hand, itemStack, getSpeed(chargedProjectilesComponent), 1.0F, null);
            return InteractionResult.CONSUME;
        } else if (!user.getProjectile(itemStack).isEmpty()) {
            this.charged = false;
            this.loaded = false;
            user.startUsingItem(hand);
            return InteractionResult.CONSUME;
        } else {
            return InteractionResult.FAIL;
        }
    }

    private static float getSpeed(ChargedProjectiles stack) {
        return stack.contains(Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        int i = this.getUseDuration(stack, user) - remainingUseTicks;
        return getPullProgress(i, stack, user) >= 1.0F && isCharged(stack);
    }

    private static boolean loadProjectiles(LivingEntity shooter, ItemStack crossbow) {
        List<ItemStack> list = draw(crossbow, shooter.getProjectile(crossbow), shooter);
        if (!list.isEmpty()) {
            crossbow.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(list));
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCharged(ItemStack stack) {
        ChargedProjectiles chargedProjectilesComponent = stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        return !chargedProjectilesComponent.isEmpty();
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        Vector3f vector3f;
        if (target != null) {
            double d = target.getX() - shooter.getX();
            double e = target.getZ() - shooter.getZ();
            double f = Math.sqrt(d * d + e * e);
            double g = target.getY(0.3333333333333333) - projectile.getY() + f * 0.2F;
            vector3f = calcVelocity(shooter, new Vec3(d, g, e), yaw);
        } else {
            Vec3 vec3d = shooter.getUpVector(1.0F);
            Quaternionf quaternionf = new Quaternionf().setAngleAxis((yaw * (float) (Math.PI / 180.0)), vec3d.x, vec3d.y, vec3d.z);
            Vec3 vec3d2 = shooter.getViewVector(1.0F);
            vector3f = vec3d2.toVector3f().rotate(quaternionf);
        }

        projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), speed, divergence);
        projectile.setNoGravity(true);

        float h = getSoundPitch(shooter.getRandom(), index);
        shooter.level().playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), ModSounds.GUN_SHOT_SOUND_EVENT, shooter.getSoundSource(), 1.0F, h);
    }

    private static Vector3f calcVelocity(LivingEntity shooter, Vec3 direction, float yaw) {
        Vector3f vector3f = direction.toVector3f().normalize();
        Vector3f vector3f2 = new Vector3f(vector3f).cross(new Vector3f(0.0F, 1.0F, 0.0F));
        if (vector3f2.lengthSquared() <= 1.0E-7) {
            Vec3 vec3d = shooter.getUpVector(1.0F);
            vector3f2 = new Vector3f(vector3f).cross(vec3d.toVector3f());
        }

        Vector3f vector3f3 = new Vector3f(vector3f).rotateAxis((float) (Math.PI / 2), vector3f2.x, vector3f2.y, vector3f2.z);
        return new Vector3f(vector3f).rotateAxis(yaw * (float) (Math.PI / 180.0), vector3f3.x, vector3f3.y, vector3f3.z);
    }
    protected Projectile createArrowEntityBase(Level world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        AbstractArrow persistentProjectileEntity = new BulletEntity(world, shooter, projectileStack.copyWithCount(1), weaponStack);
        if (critical) {
            persistentProjectileEntity.setCritArrow(true);
        }
        persistentProjectileEntity.setNoGravity(true);
        return persistentProjectileEntity;
    }

    @Override
    protected Projectile createProjectile(Level world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        Projectile projectileEntity = createArrowEntityBase(world, shooter, weaponStack, projectileStack, critical);

        if (projectileEntity instanceof AbstractArrow persistentProjectileEntity) {
            persistentProjectileEntity.setSoundEvent(SoundEvents.CROSSBOW_HIT);
            persistentProjectileEntity.setNoGravity(true);

            // 基础伤害
            double damage = 500.0D;

            // 力量（弓附魔）
            int powerLevel = getEnchantmentLevel(world, weaponStack, Enchantments.POWER);
            if (powerLevel > 0) {
                damage += powerLevel * 0.5D + 0.5D;
            }
            ((ITrueDamageProjectile) persistentProjectileEntity).setTrueDamage(true, false);
            persistentProjectileEntity.setBaseDamage(damage);

/*            // 冲击（弓附魔）
            int punchLevel = getEnchantmentLevel(world, weaponStack, Enchantments.PUNCH);
            if (punchLevel > 0) {
                persistentProjectileEntity.setPunch(punchLevel);
            }*/

            // 火矢（弓附魔）
            int flameLevel = getEnchantmentLevel(world, weaponStack, Enchantments.FLAME);
            if (flameLevel > 0) {
                persistentProjectileEntity.igniteForSeconds(100);
            }

            // 穿透（弩附魔）
            int piercingLevel = getEnchantmentLevel(world, weaponStack, Enchantments.PIERCING);
            if (piercingLevel > 0) {
                persistentProjectileEntity.setPierceLevel((byte) piercingLevel);
            }
        }

        projectileEntity.setNoGravity(true);
        return projectileEntity;
    }

    public void shootAll(Level world, LivingEntity shooter, InteractionHand hand, ItemStack stack, float speed, float divergence, @Nullable LivingEntity target) {
        if (world instanceof ServerLevel serverWorld) {
            ChargedProjectiles chargedProjectilesComponent = stack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
            if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
                this.shoot(serverWorld, shooter, hand, stack, chargedProjectilesComponent.getItems(), speed, divergence, shooter instanceof Player, target);
                if (shooter instanceof ServerPlayer serverPlayerEntity) {
                    CriteriaTriggers.SHOT_CROSSBOW.trigger(serverPlayerEntity, stack);
                    serverPlayerEntity.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                }
            }
        }
    }

    private static float getSoundPitch(RandomSource random, int index) {
        return index == 0 ? 1.0F : getSoundPitch((index & 1) == 1, random);
    }

    private static float getSoundPitch(boolean flag, RandomSource random) {
        float f = flag ? 0.63F : 0.43F;
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
    }

    @Override
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClientSide) {
            CrossbowItem.ChargingSounds loadingSounds = this.getLoadingSounds(stack);
            float f = (float)(stack.getUseDuration(user) - remainingUseTicks) / getPullTime(stack, user);
            if (f < 0.2F) {
                this.charged = false;
                this.loaded = false;
            }

            if (f >= 0.2F && !this.charged) {
                this.charged = true;
                loadingSounds.start()
                        .ifPresent(sound -> world.playSound(null, user.getX(), user.getY(), user.getZ(), sound.value(), SoundSource.PLAYERS, 0.5F, 1.0F));
            }

            if (f >= 0.5F && !this.loaded) {
                this.loaded = true;
/*                loadingSounds.mid()
                        .ifPresent(sound -> world.playSound(null, user.getX(), user.getY(), user.getZ(), (SoundEvent)sound.value(), SoundCategory.PLAYERS, 0.5F, 1.0F));*/
            }

            if (f >= 1.0F && !isCharged(stack) && loadProjectiles(user, stack)) {
/*                loadingSounds.end()
                        .ifPresent(
                                sound -> world.playSound(
                                        null,
                                        user.getX(),
                                        user.getY(),
                                        user.getZ(),
                                        (SoundEvent)sound.value(),
                                        user.getSoundCategory(),
                                        1.0F,
                                        1.0F / (world.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
                                )
                        );*/
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    public static int getPullTime(ItemStack stack, LivingEntity user) {
        float f = EnchantmentHelper.modifyCrossbowChargingTime(stack, user, 1.25F);
        return Mth.floor(f * 20.0F);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.CROSSBOW;
    }

    CrossbowItem.ChargingSounds getLoadingSounds(ItemStack stack) {
        return DEFAULT_LOADING_SOUNDS;
    }

    private static float getPullProgress(int useTicks, ItemStack stack, LivingEntity user) {
        float f = (float)useTicks / getPullTime(stack, user);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public boolean useOnRelease(ItemStack stack) {
        return stack.is(this);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 8;
    }
    public boolean isLeftClick = false;
    @Override
    public void onLeftClickStart(Player player, ItemStack stack, boolean isServer) {
        if (player.isShiftKeyDown()) {
            player.playSound(SoundEvents.SPYGLASS_USE, 1.0F, 1.0F);
            isLeftClick = true;
        }
    }

    @Override
    public void onLeftClickStop(Player player, ItemStack stack, boolean isServer) {
        isLeftClick = false;
        player.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
    }

    private static boolean isSupportedBowOrCrossbowEnchantment(Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.POWER)
                || enchantment.is(Enchantments.PUNCH)
                || enchantment.is(Enchantments.FLAME)
                || enchantment.is(Enchantments.QUICK_CHARGE)
                || enchantment.is(Enchantments.MULTISHOT)
                || enchantment.is(Enchantments.PIERCING);
    }

    private static boolean isInfinity(Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.INFINITY);
    }

    private static int getEnchantmentLevel(Level world, ItemStack stack, net.minecraft.resources.ResourceKey<Enchantment> key) {
        Holder<Enchantment> entry = world.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(key);
        return EnchantmentHelper.getItemEnchantmentLevel(entry, stack);
    }

    public static enum ChargeType implements StringRepresentable {
        NONE("none"),
        ARROW("arrow"),
        ROCKET("rocket");

        public static final Codec<CrossbowItem.ChargeType> CODEC = StringRepresentable.fromEnum(CrossbowItem.ChargeType::values);
        private final String name;

        private ChargeType(final String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public record LoadingSounds(Optional<Holder<SoundEvent>> start, Optional<Holder<SoundEvent>> mid, Optional<Holder<SoundEvent>> end) {
        public static final Codec<CrossbowItem.ChargingSounds> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                SoundEvent.CODEC.optionalFieldOf("start").forGetter(CrossbowItem.ChargingSounds::start),
                                SoundEvent.CODEC.optionalFieldOf("mid").forGetter(CrossbowItem.ChargingSounds::mid),
                                SoundEvent.CODEC.optionalFieldOf("end").forGetter(CrossbowItem.ChargingSounds::end)
                        )
                        .apply(instance, CrossbowItem.ChargingSounds::new)
        );
    }
}
