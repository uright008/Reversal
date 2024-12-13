package cn.stars.reversal.config;

import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.music.MusicUtil;
import cn.stars.reversal.music.api.MusicAPI;
import cn.stars.reversal.music.api.user.User;
import cn.stars.reversal.util.misc.FileUtil;
import com.google.gson.JsonObject;
import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public final class MusicHandler {

    public void save() {
        FileUtil.saveFile("MusicAccount.txt", true, MusicUtil.gson.fromJson(MusicUtil.gson.toJson(MusicAPI.user), JsonObject.class) + "\r\n");
    }

    public void load() {
        final String config = FileUtil.loadFile("MusicAccount.txt");
        if (config == null || !RainyAPI.hasJavaFX) {
            return;
        }
        MusicAPI.user = MusicUtil.gson.fromJson(config, User.class);
    }
}
