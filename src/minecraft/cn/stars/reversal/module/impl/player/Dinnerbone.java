/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025Aerolite Society, All rights reserved.
 */
package cn.stars.reversal.module.impl.player;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;

@ModuleInfo(name = "Dinnerbone", localizedName = "玩家倒立", description = "Make the player upside down", localizedDescription = "使玩家模型倒立", category = Category.PLAYER)
public class Dinnerbone extends Module {
    public final BoolValue self = new BoolValue("Self", this, true);
}
