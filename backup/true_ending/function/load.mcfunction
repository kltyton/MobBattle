# pivot marker（如果某些粒子需要中心参考点，可保留；否则可删）
execute in the_end positioned 0 80 0 run kill @e[distance=..10,type=marker,tag=trueEnding_pivot]
execute in the_end positioned 0 80 0 run summon marker ~ ~ ~ {Tags:["trueEnding_pivot"]}

# 只保留必要的计分板（粒子依赖这些）
scoreboard objectives add trueEnding_clock dummy
scoreboard objectives add trueEnding_count dummy        # 粒子计数器
scoreboard objectives add trueEnding_constants dummy
scoreboard objectives add trueEnding_storage dummy      # respawn_ender_dragon 等用
scoreboard objectives add trueEnding_settings dummy     # ambience 开关用

# 常量（粒子循环常用）
scoreboard players set 2 trueEnding_constants 2
scoreboard players set 10 trueEnding_constants 10
scoreboard players set 100 trueEnding_constants 100
scoreboard players set 1000 trueEnding_constants 1000

# 时钟调度（必须保留，让 tick 循环运行）
schedule function true_ending:clocks/20tick 20t
schedule function true_ending:clocks/2tick  2t
schedule function true_ending:clocks/10tick 10t
schedule function true_ending:clocks/5tick  5t
# schedule function true_ending:clocks/1min 1200t   ← 如果没有 1min 内容，可删除这行

# 默认设置（只保留 ambience 默认开启）
function true_ending:other/default_settings