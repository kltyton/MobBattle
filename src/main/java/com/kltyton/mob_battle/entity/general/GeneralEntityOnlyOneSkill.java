package com.kltyton.mob_battle.entity.general;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import net.minecraft.world.entity.Mob;
import com.geckolib.animatable.GeoEntity;

/**
 * 单技能通用实体的兼容领域契约。
 *
 * <p>为只有一个主动技能的实体提供统一的技能状态与关键帧指令处理，并通过
 * {@code SkillEntity} 接入服务端安全边界。
 *
 * @param <T> 实现该契约的具体 Mob 类型
 */
public interface GeneralEntityOnlyOneSkill<T extends Mob> extends ModSkillEntityType, GeoEntity {
    void runSkill(T entity);
    boolean hasSkill();
    void setHasSkill(boolean skill);
    default void stopSkill() {
        this.setHasSkill(false);
    }
    /**
     * 服务端技能指令分发契约（单技能实体层级）。
     *
     * <p>{@code attack} 执行唯一技能 {@code runSkill}，{@code stop_ai}/{@code start_ai}
     * 控制 AI，{@code stop} 结束技能并恢复 AI。只有被识别并执行的指令返回 {@code true}；
     * 未知指令返回 {@code false} 且不改变实体状态。
     */
    @Override
    default boolean handleSkillPayload(String skillName) {
        if (!(this instanceof Mob mobEntity)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        T entity = (T) mobEntity;
        switch (skillName) {
            case "attack" -> runSkill(entity);
            case "stop_ai" -> mobEntity.setNoAi(true);
            case "start_ai" -> mobEntity.setNoAi(false);
            case "stop" -> {
                stopSkill();
                mobEntity.setNoAi(false);
            }
            default -> {
                return false;
            }
        }
        return true;
    }
}
