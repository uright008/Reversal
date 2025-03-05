/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025Aerolite Society, All rights reserved.
 */
package cn.stars.reversal.module.impl.player;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;

@ModuleInfo(name = "SmallPlayer", chineseName = "玩家缩小", description = "Make the player become a child", chineseDescription = "让玩家变成小孩", category = Category.PLAYER)
public class SmallPlayer extends Module {
    public final BoolValue self = new BoolValue("Self", this, true);
}
