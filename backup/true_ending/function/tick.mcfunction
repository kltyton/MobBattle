# 只保留时钟递增（粒子文件常用 count）
scoreboard players add 20tick trueEnding_clock 1
scoreboard players add 10tick trueEnding_clock 1
scoreboard players add 5tick  trueEnding_clock 1
scoreboard players add 2tick  trueEnding_clock 1
scoreboard players add 1min trueEnding_clock 1

# ─────────────── 环境粒子 ───────────────
# 无龙时的环境粒子
execute if score ambience trueEnding_settings matches 1 in the_end positioned 0 80 0 if entity @p[distance=..128] unless entity @e[type=ender_dragon,distance=..256] run function true_ending:ambience/a_main_no_dragon

# 有龙时的环境粒子（核心）
execute in the_end positioned 0 80 0 if entity @p[distance=..128] if entity @e[type=ender_dragon,distance=..256] run function true_ending:ambience/a_main

# dragonparticle marker 推进（粒子波纹）
execute as @e[type=marker,tag=trueEnding_dragonparticle] at @s run function true_ending:ambience/dragon_entity

# ─────────────── 技能炫酷粒子（marker 驱动） ───────────────
execute as @e[type=marker,tag=trueEnding_shockwave]  at @s run function true_ending:boss/shockwave/root
execute as @e[type=marker,tag=trueEnding_shockwave2] at @s run function true_ending:boss/shockwave/root2
execute as @e[type=marker,tag=trueEnding_pad]        at @s run function true_ending:boss/shockwave/pad
execute as @e[type=marker,tag=trueEnding_ultrafireball] at @s run function true_ending:boss/ultra_fireball

# ─────────────── 重生动画 ───────────────
# 检测水晶生成 → 启动重生粒子序列
execute if score respawn_ender_dragon trueEnding_storage matches 0.. in the_end positioned 0 65 0 unless entity @e[type=end_crystal,distance=..16,limit=1] run scoreboard players reset respawn_ender_dragon trueEnding_storage
execute unless score respawn_ender_dragon trueEnding_storage matches 0.. in the_end positioned 0 65 0 if data entity @e[type=end_crystal,distance=..16,limit=1] beam_target run scoreboard players add respawn_ender_dragon trueEnding_storage 0
execute if score respawn_ender_dragon trueEnding_storage matches 0.. in the_end positioned 0 65 0 if entity @p[distance=..128] run function true_ending:respawning/a_main

# ─────────────── 出口传送门粒子（可选，如果想保留） ───────────────
execute in the_end positioned 0 80 0 as @e[type=marker,tag=trueEnding_exitportal,distance=..128] at @s run function true_ending:ambience/exitportal