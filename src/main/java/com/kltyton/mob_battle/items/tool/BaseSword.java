package com.kltyton.mob_battle.items.tool;

import com.kltyton.mob_battle.items.ModMaterial;
import net.minecraft.item.Item;

public class BaseSword extends Item {
    public BaseSword(Settings settings) {
        super(settings.sword(ModMaterial.KLTYTON_TOOL_MATERIAL,36, -2.2f));
    }
}
