package com.kltyton.mob_battle.network.receiver.server;

import com.kltyton.mob_battle.command.CombatLogSystem;
import com.kltyton.mob_battle.entity.player.PlayerEntitySkill;
import com.kltyton.mob_battle.items.tool.sword.BloodKnifeItem;
import com.kltyton.mob_battle.items.tool.sword.PoisonKnifeItem;
import com.kltyton.mob_battle.network.packet.PlayerSkillPayload;
import com.kltyton.mob_battle.skill.server.SkillRequestPolicy;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

/**
 * 玩家自身技能指令接收器（服务端）。
 *
 * <p>只作用于 {@code context.player()}，负载中的 entityId 字段沿用旧实现
 * 语义继续忽略。Fabric play 阶段回调已经处于服务器线程，直接执行状态机，
 * 避免二次派发造成额外延迟与跨 payload 重排。
 *
 * <p>信任边界：指令表与旧实现逐项一致，接收器不新增权限或速率限制等
 * 推测性约束；技能状态机仍由 {@link PlayerEntitySkill} 内部裁决。
 */
public final class PlayerSkillReceiver {
    private PlayerSkillReceiver() {
    }

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(PlayerSkillPayload.ID,
                (payload, context) -> {
                    ServerPlayer player = context.player();
                    if (payload.skillName() == null
                            || payload.skillName().isBlank()
                            || payload.skillName().length() > SkillRequestPolicy.MAX_COMMAND_LENGTH) {
                        return;
                    }
                    CombatLogSystem.logSkill(player, payload.skillName());
                    switch (payload.skillName()) {
                        case "attack" -> PlayerEntitySkill.runAttackSkill(player);

                        case "attack2" -> PlayerEntitySkill.runAttackSkill_2(player);
                        case "attack2_run" -> PlayerEntitySkill.runAttackSkill_2Run(player);

                        case "left_whip" -> PlayerEntitySkill.runLeftWhipSkill(player);
                        case "left_whip_run" -> PlayerEntitySkill.runLeftWhipSkillRun(player);

                        case "top_knee" -> PlayerEntitySkill.runTopKneeSkill(player);
                        case "upper_hook" -> PlayerEntitySkill.runUpperHookSkill(player);
                        case "top_knee_run" -> PlayerEntitySkill.runTopKneeSkillRun(player);

                        case "collision_run" -> PlayerEntitySkill.runCollisionSkillRun(player);
                        case "collision_start" -> PlayerEntitySkill.startCollisionSkillState(player);
                        case "collision_end" -> PlayerEntitySkill.stopCollisionSkillState(player);

                        case "run_collision_run" -> PlayerEntitySkill.runRunCollisionSkillRun(player);
                        case "run_collision" -> PlayerEntitySkill.runRunCollisionSkill(player);

                        case "smashing_the_ground_run" -> PlayerEntitySkill.runSmashGroundSkillRun(player);
                        case "run_jump" -> PlayerEntitySkill.runJumpSkill(player);
                        case "smashing_the_ground" -> PlayerEntitySkill.runSmashGroundSkill(player);

                        case "scraping" -> PlayerEntitySkill.runScraping(player);
                        case "scraping_run" -> PlayerEntitySkill.runScrapingRun(player);
                        case "scraping_attack" -> PlayerEntitySkill.runScrapingAttack(player);
                        case "scraping_end" -> PlayerEntitySkill.runScrapingEnd(player);

                        case "retreat_step" -> PlayerEntitySkill.runRetreatStepRunSkill(player);

                        case "knife_run_attack" -> {
                            PoisonKnifeItem.releasePendingSkill(player);
                            BloodKnifeItem.releasePendingSkill(player);
                        }

                        case "stop" -> PlayerEntitySkill.stopSkill(player);
                        case "can_move" -> PlayerEntitySkill.canMove(player);
                    }
                }
        );
    }
}
