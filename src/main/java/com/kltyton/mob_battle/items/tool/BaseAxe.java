package com.kltyton.mob_battle.items.tool;

import com.kltyton.mob_battle.items.ModMaterial;
import net.minecraft.item.AxeItem;

public class BaseAxe extends AxeItem {

    public BaseAxe(Settings settings) {
        super(ModMaterial.KLTYTON_TOOL_MATERIAL, 46, -3.3f, settings.axe(ModMaterial.KLTYTON_TOOL_MATERIAL,50,0.7f));
    }
}
