/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2024 Starlight, All rights reserved.
 */
package cn.stars.reversal.module.impl.client;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;

@ModuleInfo(name = "NameTag", chineseName = "名称标签", description = "Edit the name tag on head", chineseDescription = "修改头上显示名字的标签", category = Category.CLIENT)
public class NameTag extends Module {
    public final BoolValue self = new BoolValue("Self", this, false);
    public final BoolValue background = new BoolValue("Background", this, true);

    @Override
    public void onUpdateAlways() {
        if (this.enabled) this.enabled = false;
    }
}
