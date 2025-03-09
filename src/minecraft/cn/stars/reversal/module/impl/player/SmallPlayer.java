/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025 Aerolite Society, Some rights reserved.
 */
package cn.stars.reversal.module.impl.player;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;

@ModuleInfo(name = "SmallPlayer", localizedName = "module.SmallPlayer.name", description = "Make the player become a child", localizedDescription = "module.SmallPlayer.desc", category = Category.PLAYER)
public class SmallPlayer extends Module {
    public final BoolValue self = new BoolValue("Self", this, true);
}
