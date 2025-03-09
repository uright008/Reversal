/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025 Aerolite Society, Some rights reserved.
 */
package cn.stars.reversal.module.impl.misc;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.NoteValue;

@ModuleInfo(name = "CustomName", localizedName = "module.CustomName.name", description = "Customize your minecraft name", localizedDescription = "module.CustomName.desc", category = Category.MISC)
public class CustomName extends Module {
    private final NoteValue note = new NoteValue("使用指令 '.name <名称>' 来设置自定义名称!", this);
}
