package com.kltyton.mob_battle.entity.general;

import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.minecraft.world.entity.Mob;

/**
 * 通用/单技能实体层级在旧服务端网络层的分发适配。
 *
 * <p>指令表与各接口的 {@code handleSkillPayload} 默认实现保持一致；返回 {@code true}
 * 表示该载荷已被识别并执行，返回 {@code false} 表示实体不认识的指令。
 *
 * @deprecated 统一网络入口已直接调用 {@code SkillEntity.handleSkillPayload}；
 * 仅为附属代码的源码兼容保留。
 */
@Deprecated(forRemoval = false)
public final class GeneralEntitySkillHelper {
    private GeneralEntitySkillHelper() {
    }

    public static <E extends Mob> boolean handleSkillPayload(GeneralEntityOnlyOneSkill<E> skillInterface, SkillPayload payload) {
        return skillInterface.handleSkillPayload(payload.skillName());
    }
    public static <E extends Mob> boolean handleSkillPayload(GeneralEntity<E> skillInterface, SkillPayload payload) {
        return skillInterface.handleSkillPayload(payload.skillName());
    }
}
