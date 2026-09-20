package com.kltyton.mob_battle.entity.littleperson.zombie;

import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;

/**
 * 小人僵尸的领域标记。保留小人技能契约，同时区分感染前后的自然敌对关系。
 * 原版僵尸、亡灵与日照燃烧语义由实体类型标签提供，不依赖 Java 父类判定。
 */
public interface ZombieLittlePerson extends LittlePersonEntity {
}
