/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025 Aerolite Society, Some rights reserved.
 */
package cn.stars.reversal.module.impl.addons;

import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import net.minecraft.client.resources.I18n;

@ModuleInfo(name = "MusicPlayer", localizedName = "module.MusicPlayer.name", description = "Play netease musics", localizedDescription = "module.MusicPlayer.desc", category = Category.ADDONS)
public class MusicPlayer extends Module {
    @Override
    protected void onEnable() {
        if (RainyAPI.hasJavaFX) mc.displayGuiScreen(Reversal.musicManager.screen);
        else Reversal.showMsg(I18n.format("module.MusicPlayer.msg"));
        this.setEnabled(false);
    }
}
