package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.entity.OwnedSummon;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class EntityUtil {
    public static boolean isCreativeOrSpectator(Entity entity) {
        return entity instanceof Player player && (player.isCreative() || player.isSpectator());
    }

    public static boolean isValidCombatTarget(LivingEntity source, LivingEntity target) {
        return target != null
                && target != source
                && target.isAlive()
                && !isCreativeOrSpectator(target)
                && !target.isAlliedTo(source);
    }

    @Nullable
    public static Entity getSummonOwner(Entity entity) {
        if (entity instanceof OwnedSummon ownedSummon) {
            return ownedSummon.getSummonOwner();
        }
        return null;
    }

    @Nullable
    public static Entity getKnownOwner(Entity entity) {
        Entity summonOwner = getSummonOwner(entity);
        if (summonOwner != null) {
            return summonOwner;
        }
        if (entity instanceof Projectile projectile) {
            return projectile.getOwner();
        }
        if (entity instanceof OwnableEntity tameable) {
            return tameable.getOwner();
        }
        if (entity instanceof TraceableEntity ownable) {
            return ownable.getOwner();
        }
        return null;
    }

    public static boolean isFriendlyToSummon(Entity summon, @Nullable Entity owner, Entity target) {
        if (target == null || target == summon) {
            return true;
        }
        if (target.isAlliedTo(summon) || summon.isAlliedTo(target)) {
            return true;
        }

        Entity resolvedOwner = owner == null ? getKnownOwner(summon) : owner;
        if (resolvedOwner == null) {
            return false;
        }
        if (isSameOrTeammate(target, resolvedOwner)) {
            return true;
        }

        Entity rootOwner = getSummonOwner(resolvedOwner);
        if (rootOwner != null && isSameOrTeammate(target, rootOwner)) {
            return true;
        }

        Entity targetOwner = getKnownOwner(target);
        if (targetOwner == null) {
            return false;
        }
        return isSameOrTeammate(targetOwner, resolvedOwner)
                || rootOwner != null && isSameOrTeammate(targetOwner, rootOwner);
    }

    public static boolean isValidSummonCombatTarget(Entity summon, @Nullable Entity owner, LivingEntity target) {
        return target != null
                && target.isAlive()
                && !isCreativeOrSpectator(target)
                && !isFriendlyToSummon(summon, owner, target);
    }

    public static boolean shouldBlockOwnedSummonDamage(Entity damagingEntity, LivingEntity target) {
        Entity owner = getSummonOwner(damagingEntity);
        if (owner == null && damagingEntity instanceof Projectile projectile) {
            Entity projectileOwner = projectile.getOwner();
            Entity projectileOwnerSummoner = projectileOwner == null ? null : getSummonOwner(projectileOwner);
            return projectileOwnerSummoner != null && isFriendlyToSummon(projectileOwner, projectileOwnerSummoner, target);
        }
        if (owner == null) {
            owner = getKnownOwner(damagingEntity);
        }
        return owner != null && isFriendlyToSummon(damagingEntity, owner, target);
    }

    private static boolean isSameOrTeammate(Entity target, Entity owner) {
        return target == owner || target.isAlliedTo(owner) || owner.isAlliedTo(target);
    }

    /**
     * 灏嗗疄浣?targetA 鍔犲叆鍒板疄浣?sourceB 鎵€鍦ㄧ殑闃熶紞涓?
     * @param targetA 瑕佸姞鍏ラ槦浼嶇殑瀹炰綋
     * @param sourceB 鍙傝€冮槦浼嶇殑瀹炰綋
     */
    public static void joinSameTeam(Entity targetA, Entity sourceB) {
        // 1. 鑾峰彇鍙傝€冨疄浣?B 鐨勯槦浼?
        Team abstractTeam = sourceB.getTeam();

        // 鍙湁褰?B 纭疄鍦ㄤ竴涓槦浼嶄腑鏃舵墠鎵ц鎿嶄綔
        if (abstractTeam instanceof PlayerTeam team) {
            Scoreboard scoreboard = sourceB.level().getScoreboard();

            // 2. 鑾峰彇瀹炰綋 A 鐨勬爣璇嗙
            // 鍦ㄥ師鐗堣鍒嗘澘涓紝鐜╁浣跨敤鍚嶇О锛岃€屽疄浣撲娇鐢?UUID 瀛楃涓?
            String entryA = targetA.getStringUUID();

            // 3. 灏?A 娣诲姞鍒伴槦浼嶄腑
            // 璇ユ柟娉曚細鑷姩灏?A 浠庝箣鍓嶇殑闃熶紞涓Щ闄?
            scoreboard.addPlayerToTeam(entryA, team);
        }
    }



    public enum TeamFilter {
        ALL,             // 鎵€鏈変汉
        EXCLUDE_TEAM,    // 鎺掗櫎闃熷弸
        ONLY_TEAM        // 鍙湅闃熷弸
    }

    /**
     * 鑾峰彇鎸囧畾瀹炰綋鍛ㄥ洿鎸囧畾鑼冨洿鍐呯殑鏌愮被瀹炰綋鐨勬暟閲忥紙鐞冨舰鑼冨洿锛岀簿纭窛绂伙級
     *
     * @param center      涓績瀹炰綋
     * @param clazz       瑕佺粺璁＄殑瀹炰綋绫伙紙渚嬪 LivingEntity.class锛?
     * @param filterClass 棰濆绛涢€夌被锛堜緥濡?HostileEntity.class锛夛紝瀹炰綋蹇呴』鏄绫荤殑瀹炰緥鎵嶄細璁℃暟銆?
     *                    浼犲叆 Object.class 琛ㄧず涓嶈繘琛岄澶栫瓫閫夛紙绛夊悓浜庢棤绛涢€夛級銆?
     * @param radius      鑼冨洿鍗婂緞锛堝崟浣嶏細鏍硷級
     * @param includeSelf 鏄惁鍖呭惈涓績瀹炰綋鑷韩锛堝鏋滄槸鍚屼竴绫荤殑璇濓級
     * @param teamFilter           闃熶紞绛涢€?
     * @param <T>         瀹炰綋绫诲瀷
     * @return 绗﹀悎鏉′欢鐨勫疄浣撴暟閲?
     */
    public static <T extends LivingEntity> int getNearbyEntityCount(LivingEntity center, Class<T> clazz, Class<?> filterClass, double radius, boolean includeSelf, TeamFilter teamFilter) {
        List<T> nearbyEntities = getNearbyEntity(center, clazz, filterClass, radius, includeSelf, teamFilter);
        if (nearbyEntities == null) return 0;
        else return nearbyEntities.size();
    }

    /**
     * 鑾峰彇鎸囧畾瀹炰綋鍛ㄥ洿鎸囧畾鑼冨洿鍐呯殑鏌愮被瀹炰綋鐨勯泦鍚堬紙鐞冨舰鑼冨洿锛岀簿纭窛绂伙級
     *
     * @param center               涓績瀹炰綋
     * @param clazz                瑕佺粺璁＄殑瀹炰綋绫?
     * @param filterClass          棰濆绛涢€夌被
     * @param radius               鑼冨洿鍗婂緞
     * @param includeSelf          鏄惁鍖呭惈涓績瀹炰綋鑷韩
     * @param teamFilter           闃熶紞绛涢€?
     * @param <T>                  瀹炰綋绫诲瀷
     * @return 绗﹀悎鏉′欢鐨勫疄浣撳垪琛?
     */
    public static <T extends LivingEntity> List<T> getNearbyEntity(LivingEntity center, Class<T> clazz, Class<?> filterClass, double radius, boolean includeSelf, TeamFilter teamFilter) {
        return getNearbyEntity(center, clazz, filterClass, radius, includeSelf, teamFilter, null, null);
    }

    public static <T extends LivingEntity> List<T> getNearbyEntity(LivingEntity center, Class<T> clazz, Class<?> filterClass, AABB box, boolean includeSelf, TeamFilter teamFilter, TargetingConditions targetPredicate) {
        return getNearbyEntity(center, clazz, filterClass,1, box, includeSelf, teamFilter, null, targetPredicate);
    }

    /**
     * 鑾峰彇鎸囧畾瀹炰綋鍛ㄥ洿鎸囧畾鑼冨洿鍐呯殑鏌愮被瀹炰綋鐨勯泦鍚堬紙鐞冨舰鑼冨洿锛岀簿纭窛绂伙級
     *
     * @param center               涓績瀹炰綋
     * @param clazz                瑕佺粺璁＄殑瀹炰綋绫?
     * @param radius               鑼冨洿鍗婂緞
     * @param includeSelf          鏄惁鍖呭惈涓績瀹炰綋鑷韩
     * @param teamFilter           闃熶紞绛涢€?
     * @param <T>                  瀹炰綋绫诲瀷
     * @return 绗﹀悎鏉′欢鐨勫疄浣撳垪琛?
     */
    public static <T extends LivingEntity> List<T> getNearbyEntity(LivingEntity center, Class<T> clazz, double radius, boolean includeSelf, TeamFilter teamFilter) {
        return getNearbyEntity(center, clazz, Object.class, radius, includeSelf, teamFilter);
    }

    public static <T extends LivingEntity> List<T> getNearbyEntity(LivingEntity center, Class<T> clazz, double radius, boolean includeSelf, TeamFilter teamFilter, TargetingConditions targetPredicate) {
        return getNearbyEntity(center, clazz, Object.class, radius, includeSelf, teamFilter, null, targetPredicate);
    }

    public static <T extends LivingEntity> List<T> getNearbyEntity(LivingEntity center, Class<T> clazz, double radius, boolean includeSelf, TeamFilter teamFilter, Predicate<T> extraPredicate) {
        return getNearbyEntity(center, clazz, Object.class, radius, includeSelf, teamFilter, extraPredicate, null);
    }
    public static <T extends LivingEntity> List<T> getNearbyEntity(LivingEntity center, Class<T> clazz, Class<?> filterClass, double radius, boolean includeSelf, TeamFilter teamFilter, Predicate<T> extraPredicate, TargetingConditions targetPredicate) {
        return EntityUtil.getNearbyEntity(center, clazz, filterClass, radius, null, includeSelf, teamFilter, extraPredicate, targetPredicate);
    }
    public static <T extends LivingEntity> List<T> getNearbyEntity(LivingEntity center, Class<T> clazz, Class<?> filterClass, double radius, AABB box, boolean includeSelf, TeamFilter teamFilter, Predicate<T> extraPredicate, TargetingConditions targetPredicate) {
        Level world = center.level();
        if (world.isClientSide()) {
            return List.of();
        }
        ServerLevel serverWorld = (ServerLevel) world;
        double radiusSq = radius * radius;
        if (box == null) box = center.getBoundingBox().inflate(radius);
        Predicate<T> predicate = entity -> {
            if (!entity.isAlive()) return false;
            if (isCreativeOrSpectator(entity)) return false;
            if (!includeSelf && entity == center) return false;
            if (!filterClass.isInstance(entity)) return false;
            boolean isTeammate = entity.isAlliedTo(center);
            if (teamFilter == TeamFilter.EXCLUDE_TEAM && isTeammate) return false;
            if (teamFilter == TeamFilter.ONLY_TEAM && !isTeammate) return false;
            return entity.distanceToSqr(center) <= radiusSq;
        };

        if (extraPredicate != null) predicate = predicate.and(extraPredicate);
        List<T> finalEntities = world.getEntitiesOfClass(clazz, box, predicate);
        if (targetPredicate != null) {
            finalEntities.removeIf(entity -> !targetPredicate.test(serverWorld, center, entity));
        }

        return finalEntities;
    }
    /**
     * 鑾峰彇鑼冨洿鍐呮渶杩戠殑鍚堟硶瀹炰綋 (鎺掗櫎鑷韩)
     */
    @Nullable
    public static <T extends LivingEntity> T getClosestNearbyEntity(LivingEntity center, Class<T> clazz, double radius, TeamFilter teamFilter) {
        return getClosestNearbyEntity(center, clazz, radius, teamFilter, null, null);
    }
    @Nullable
    public static <T extends LivingEntity> T getClosestNearbyEntity(LivingEntity center, Class<T> clazz, double radius, TeamFilter teamFilter, Predicate<T> extraPredicate, TargetingConditions targetPredicate) {
        List<T> entities = getNearbyEntity(center, clazz, Object.class, radius, false, teamFilter, extraPredicate, targetPredicate);
        T closest = null;
        double minDistanceSq = -1.0;

        for (T entity : entities) {
            double distSq = entity.distanceToSqr(center);
            if (minDistanceSq == -1.0 || distSq < minDistanceSq) {
                minDistanceSq = distSq;
                closest = entity;
            }
        }
        return closest;
    }

    /**
     * 鑾峰彇瑙嗙嚎鑼冨洿鍐咃紙鎵囧舰/閿ュ舰鍖哄煙锛夌殑瀹炰綋
     * 閫傜敤浜庢í鎵敾鍑绘垨瀹氬悜鎶撳彇
     */
    public static <T extends LivingEntity> List<T> getEntitiesInCone(LivingEntity center, Class<T> clazz, double radius, float arcDegrees, TeamFilter teamFilter) {
        return getEntitiesInCone(center, clazz, radius, arcDegrees, teamFilter, null, null);
    }
    public static <T extends LivingEntity> List<T> getEntitiesInCone(LivingEntity center, Class<T> clazz, double radius, float arcDegrees, TeamFilter teamFilter, Predicate<T> extraPredicate, TargetingConditions targetPredicate) {
        Vec3 lookDir = center.getViewVector(1.0F);
        Predicate<T> basePredicate = entity -> {
            Vec3 toEntity = entity.position().subtract(center.position()).normalize();
            double dotProduct = lookDir.dot(toEntity);
            double angle = Math.acos(dotProduct) * (180.0 / Math.PI);
            return angle <= (arcDegrees / 2.0);
        };
        return getNearbyEntity(center, clazz, Object.class, radius, false, teamFilter, extraPredicate == null ? basePredicate : basePredicate.and(extraPredicate), targetPredicate);
    }
    /**
     * 閲嶈浇鐗堟湰锛氶粯璁や笉鍖呭惈鑷韩锛屽彲鑷畾涔夎寖鍥?
     */
    public static <T extends LivingEntity> int getNearbyEntityCount(LivingEntity center, Class<T> clazz, double radius) {
        return getNearbyEntityCount(center, clazz, Object.class, radius, false, TeamFilter.ONLY_TEAM);
    }
    public static <T extends LivingEntity> int getNearbyEntityCount(LivingEntity center, Class<T> clazz, Class<?> filterClass, double radius) {
        return getNearbyEntityCount(center, clazz, filterClass, radius, false, TeamFilter.ONLY_TEAM);
    }
    /**
     * 鍦ㄦ寚瀹氫腑蹇冧綅缃檮杩戝鎵惧畨鍏ㄧ殑瀹炰綋鐢熸垚浣嶇疆锛堥伩鍏嶅崱澧欍€佹诞绌恒€佹棤瀹炰綋鍐茬獊锛?
     *
     * @param world          鏈嶅姟绔笘鐣?
     * @param entityTemplate 宸插垱寤轰絾灏氭湭鐢熸垚鐨勫疄浣撴ā鏉匡紙鐢ㄤ簬鑾峰彇 boundingBox 鍜屽昂瀵革級
     * @param center         涓績浣嶇疆锛堥€氬父鏄洰鏍囩帺瀹剁殑浣嶇疆锛?
     * @param horizontalRange 姘村钩鎼滅储鑼冨洿锛堝崟浣嶏細鏍硷級
     * @param verticalRange   鍨傜洿鎼滅储鑼冨洿锛堝崟浣嶏細鏍硷紝閫氬父杈冨皬锛?
     * @param maxAttempts     鏈€澶ч殢鏈哄皾璇曟鏁?
     * @param requireGround   鏄惁瑕佹眰搴曢儴鏈夊浐浣撴柟鍧楁敮鎾戯紙true=閬垮厤鐢熸垚鍦ㄧ┖涓洿鎺ユ帀钀斤級
     * @return Optional<Vec3d> 瀹夊叏浣嶇疆锛屽鏋滄湭鎵惧埌杩斿洖 empty锛堣皟鐢ㄦ柟鍙嚜琛?fallback锛?
     */
    public static Optional<Vec3> findSafeSpawnPosition(
            ServerLevel world,
            Entity entityTemplate,
            Vec3 center,
            double horizontalRange,
            double verticalRange,
            int maxAttempts,
            boolean requireGround) {

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // 闅忔満鍋忕Щ
            double offsetX = (world.getRandom().nextDouble() - 0.5) * horizontalRange * 2;
            double offsetZ = (world.getRandom().nextDouble() - 0.5) * horizontalRange * 2;
            double offsetY = (world.getRandom().nextDouble() - 0.5) * verticalRange * 2; // 鍨傜洿鑼冨洿閫氬父杈冨皬

            Vec3 candidatePos = center.add(offsetX, offsetY, offsetZ);

            // 涓存椂璁剧疆浣嶇疆浠ヨ幏鍙栨纭殑 boundingBox
            entityTemplate.setPos(candidatePos);
            AABB boundingBox = entityTemplate.getBoundingBox();

            // 妫€鏌ュ簳閮ㄦ槸鍚︽湁鏀拺
            boolean hasGround = true;
            if (requireGround) {
                BlockPos bottomPos = BlockPos.containing(candidatePos.x, boundingBox.minY - 0.01, candidatePos.z);
                hasGround = world.getBlockState(bottomPos).isRedstoneConductor(world, bottomPos);
            }

            // 绌洪棿绌恒€佹棤鍥轰綋鏂瑰潡纰版挒銆佹棤鍏朵粬瀹炰綋鍗犵敤
            if (hasGround
                    && world.noCollision(entityTemplate, boundingBox) // 鏇翠弗鏍肩殑妫€鏌ワ紙鑰冭檻瀹炰綋纰版挒鎺╃爜锛?
                    && world.getEntities(entityTemplate, boundingBox).isEmpty()) {

                return Optional.of(candidatePos);
            }
        }

        return Optional.empty();
    }

    /**
     * 閲嶈浇锛氶粯璁ゅ弬鏁帮紙姘村钩6鏍硷紝鍨傜洿卤2鏍硷紝灏濊瘯50娆★紝瑕佹眰鍦伴潰鏀拺锛?
     */
    public static Optional<Vec3> findSafeSpawnPosition(ServerLevel world, Entity entityTemplate, Vec3 center) {
        return findSafeSpawnPosition(world, entityTemplate, center, 6.0, 2.0, 50, true);
    }

    /**
     * 閲嶈浇锛氫笉瑕佹眰鍦伴潰鏀拺锛堥琛屽疄浣撴垨鍏佽鐭殏鎺夎惤锛?
     */
    public static Optional<Vec3> findSafeSpawnPositionNoGround(ServerLevel world, Entity entityTemplate, Vec3 center) {
        return findSafeSpawnPosition(world, entityTemplate, center, 6.0, 2.0, 50, false);
    }

}

