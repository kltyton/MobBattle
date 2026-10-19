# Mob Battle Datagen 规范

## 资源所有权

`build.gradle` 通过 Fabric Loom 的 `configureDataGeneration { client = true }` 配置数据生成。生成入口是 `Mob_battleDataGenerator`，输出位于 `src/main/generated` 并参与模组资源构建。

- `src/main/generated`：语言、模型、方块状态、标签、战利品表、配方等可重复生成的数据。
- `src/main/resources`：纹理、动画、几何、音频、着色器、配置和没有 Datagen 所有者的手工资源。
- 两个目录不得拥有同一路径。重复路径会形成隐式覆盖，导致本地与打包行为依赖资源顺序。
- 历史命名空间 `mob_battle_old` 已移除，不是运行时兼容命名空间，也不得重新生成。

## Provider 分层

| Provider | 所有权 |
| --- | --- |
| `ModModelGenerator` | 物品模型、方块模型和方块状态 |
| `ModEnglishLangProvider` / `ModChineseLangProvider` | `en_us`、`zh_cn` 翻译 |
| `ModBlockTagGenerator` | 方块标签 |
| `ModItemTagGenerator` | 物品标签 |
| `ModEntityTagGenerator` | 实体类型标签 |
| `ModBlockLootTableGenerator` | 方块战利品表 |
| `ModEntityLootTableGenerator` | 实体战利品表 |
| `ModRecipeGenerator` | 配方 |

`FacingBlockModels` 维护有朝向方块的统一模型规则；`HandwrittenItemModels` 把早期只能手写表达的物品模型描述符集中成类型化数据；`LanguageProviderSupport` 负责双语 Provider 的共享读取与校验。类名描述数据职责，不使用模糊工具包。

## 手写 JSON 迁移

迁移单个资源时必须按以下顺序：

1. 记录原路径、命名空间和完整语义，包括父模型、纹理层、display 变换、方块朝向、标签值、掉落条件和 `random_sequence`。
2. 在正确 Provider 中用目标版本 API 表达同一语义。无法直接表达的模型先抽成类型化描述，不允许复制原始 JSON 字符串拼接。
3. 运行 `runDatagen`，逐字段比较生成结果与原资源。
4. 只有在生成路径和语义确认一致后，才删除 `src/main/resources` 中的重叠文件。
5. 再运行一次 Datagen；两次生成树哈希必须一致。

禁止：

- 同一路径同时保留手工版和生成版。
- 生成器覆盖动画、几何、纹理或音频等手工资产。
- 为了“让输出看起来一样”直接把整段 JSON 作为字符串写入 Provider。
- 删除旧资源后不检查注册 ID、翻译键或资源引用。

## 新增内容

新增实体、物品或方块时，同一改动应补齐适用的数据：

- 语言：至少 `en_us` 与 `zh_cn`。
- 模型/方块状态：由 `ModModelGenerator` 生成。
- 标签：进入对应领域 Provider。
- 战利品：实体或方块必须明确“有掉落”或“空掉落”的设计。
- 配方：由 `ModRecipeGenerator` 生成，并保持解锁条件明确。

## 验证

```bash
./gradlew.bat runDatagen --no-daemon
```

验收清单：

1. 所有生成任务成功。
2. 连续两次运行后的 `src/main/generated` 文件清单与 SHA-256 完全一致。
3. 所有 `.json` 均可解析且无 UTF-8 BOM。
4. 手工资源与生成资源的相对路径交集为零。
5. `assets/mob_battle_old` 与 `data/mob_battle_old` 均不存在。
6. `git diff` 中没有 Provider 所有权之外的意外生成文件。
7. 最终 `build` 和 `runClient` 使用本次生成结果通过。
