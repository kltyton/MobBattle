package com.kltyton.mob_battle.entity.general;

import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.minecraft.entity.mob.MobEntity;

public class GeneralEntitySkillHelper {
    public static <E extends MobEntity> void handleSkillPayload(GeneralEntityOnlyOneSkill<E> skillInterface, SkillPayload payload) {
        if (skillInterface instanceof MobEntity mobEntity) {
            @SuppressWarnings("unchecked")
            E entity = (E) mobEntity;
            switch (payload.skillName()) {
                case "attack" -> skillInterface.runSkill(entity);
                case "stop_ai" -> mobEntity.setAiDisabled(true);
                case "start_ai" -> mobEntity.setAiDisabled(false);
                case "stop" -> {
                    skillInterface.stopSkill();
                    mobEntity.setAiDisabled(false);
                }
            }
        }
    }
    public static <E extends MobEntity> void handleSkillPayload(GeneralEntity<E> skillInterface, SkillPayload payload) {
        if (skillInterface instanceof MobEntity mobEntity) {
            @SuppressWarnings("unchecked")
            E entity = (E) mobEntity;
            switch (payload.skillName()) {
                case "attack" -> skillInterface.runSkill_1(entity);
                case "attack1_1" -> skillInterface.runSkill_1_1(entity);
                case "attack1_2" -> skillInterface.runSkill_1_2(entity);
                case "attack1_3" -> skillInterface.runSkill_1_3(entity);
                case "attack2" -> skillInterface.runSkill_2(entity);
                case "attack2_stop" -> skillInterface.stopSkill_2(entity);
                case "attack3" -> skillInterface.runSkill_3(entity);
                case "attack3_1" -> skillInterface.runSkill_3_1(entity);
                case "attack3_2" -> skillInterface.runSkill_3_2(entity);
                case "attack4" -> skillInterface.runSkill_4(entity);
                case "attack4_stop" -> skillInterface.stopSkill_4(entity);
                case "attack5" -> skillInterface.runSkill_5(entity);
                case "attack6" -> skillInterface.runSkill_6(entity);
                case "attack7" -> skillInterface.runSkill_7(entity);
                case "attack7_1" -> skillInterface.runSkill_7_1(entity);
                case "buff" -> skillInterface.runBuff(entity);
                case "stop_ai" -> mobEntity.setAiDisabled(true);
                case "start_ai" -> mobEntity.setAiDisabled(false);
                case "stop" -> {
                    skillInterface.setHasSkill(false);
                    mobEntity.setAiDisabled(false);
                }
            }
        }
    }
}
