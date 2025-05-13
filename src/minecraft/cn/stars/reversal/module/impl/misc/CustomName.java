/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025 Aerolite Society, Some rights reserved.
 */
package cn.stars.reversal.module.impl.misc;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.TextValue;

@ModuleInfo(name = "CustomName", localizedName = "module.CustomName.name", description = "Customize your minecraft name", localizedDescription = "module.CustomName.desc", category = Category.MISC)
public class CustomName extends Module {
    public final TextValue textValue = new TextValue("Name", this, "Reversal User");
}
