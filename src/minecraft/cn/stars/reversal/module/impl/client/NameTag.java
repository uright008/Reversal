/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025 Aerolite Society, Some rights reserved.
 */
package cn.stars.reversal.module.impl.client;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;

@ModuleInfo(name = "NameTag", localizedName = "module.NameTag.name", description = "Edit the name tag on head", localizedDescription = "module.NameTag.desc", category = Category.CLIENT)
public class NameTag extends Module {
    public final BoolValue self = new BoolValue("Self", this, false);
    public final BoolValue background = new BoolValue("Background", this, true);

    @Override
    public void onUpdateAlways() {
        if (this.enabled) this.enabled = false;
    }
}
