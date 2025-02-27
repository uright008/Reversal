package cn.stars.reversal.config;

import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.value.Value;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NumberValue;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.util.misc.FileUtil;
import cn.stars.reversal.util.render.ThemeUtil;

public class DefaultHandler {
    public static void loadConfigs() {
        final String config = FileUtil.loadFile("settings.txt");
        if (config == null) saveConfig(true);
        final String[] configLines = config.split("\r\n");

        for (final Module m : Reversal.moduleManager.getModuleList()) {
            if (m.isEnabled()) {
                m.toggleNoEvent();
            }
        }

        for (final Module m : Reversal.moduleManager.getModuleList()) {
            if (m.isEnabled()) {
                m.toggleNoEvent();
            }
        }

        boolean gotConfigVersion = false;
        for (final String line : configLines) {
            if (line == null) return;

            final String[] split = line.split("_");
            if (split[0].contains("Reversal")) {
                if (split[1].contains("Version")) {
                    gotConfigVersion = true;

                    final String configVersion = split[2];

                    if (!Reversal.VERSION.equalsIgnoreCase(configVersion)) {
                        Reversal.showMsg("This config was made in a different version of Reversal.");
                        Reversal.notificationManager.registerNotification(
                                "This config was made in a different version of Reversal.", NotificationType.WARNING
                        );
                    }
                }
            }


            if (split[0].contains("ClientName")) {
                ThemeUtil.setCustomClientName(split.length > 1 ? split[1] : "");
                continue;
            }

            if (split[0].contains("CustomPlayerName")) {
                Reversal.customName = split.length > 1 ? split[1] : "";
                continue;
            }

            if (split[0].contains("MainMenuBackground")) {
                RainyAPI.backgroundId = Integer.parseInt(split[1]);
                continue;
            }

            //    if (split[0].contains("PlayMusic")) {
            //        Minecraft.getMinecraft().riseMusicTicker.shouldKeepPlaying = Boolean.parseBoolean(split[1]);
            //        continue;
            //    }

            Module module = Reversal.moduleManager.getModule(split[1]);
            if (module != null) {

                if (split[0].contains("Toggle")) {
                    if (split[2].contains("true")) {
                        if (!module.isEnabled()) {
                            module.toggleNoEvent();
                        }
                    }
                }

                if (split[0].contains("PositionX")) {
                    module.setX(Integer.parseInt(split[2]));
                }
                if (split[0].contains("PositionY")) {
                    module.setY(Integer.parseInt(split[2]));
                }

                final Value setting = Reversal.moduleManager.getSetting(split[1], split[2]);

                if (split[0].contains("BoolValue") && setting instanceof BoolValue) {
                    if (split[3].contains("true")) {
                        ((BoolValue) setting).enabled = true;
                    }

                    if (split[3].contains("false")) {
                        ((BoolValue) setting).enabled = false;
                    }
                }

                if (split[0].contains("NumberValue") && setting instanceof NumberValue)
                    ((NumberValue) setting).setValue(Double.parseDouble(split[3]));

                if (split[0].contains("ModeValue") && setting instanceof ModeValue)
                    ((ModeValue) setting).set(split[3]);

                if (split[0].contains("Bind")) {
                    module.setKeyBind(Integer.parseInt(split[2]));
                }
            }
        }
        if (!gotConfigVersion) {
            Reversal.showMsg("This config was made in a different version of Reversal.");
            Reversal.notificationManager.registerNotification(
                    "This config was made in a different version of Reversal.", NotificationType.WARNING
            );
        }

        for (Module module : Reversal.moduleManager.moduleList) {
            module.onLoad();
        }
    }

    public static void saveConfig(boolean force) {
        final StringBuilder configBuilder = new StringBuilder();
        configBuilder.append("Reversal_Version_").append(Reversal.VERSION).append("\r\n");
        configBuilder.append("ClientName_").append(ThemeUtil.getCustomClientName()).append("\r\n");
        configBuilder.append("CustomPlayerName_").append(Reversal.customName).append("\r\n");
        configBuilder.append("MainMenuBackground_").append(RainyAPI.backgroundId).append("\r\n");
        configBuilder.append("DisableShader_").append(false).append("\r\n");

        if (!force) {
            for (final Module m : Reversal.moduleManager.getModuleList()) {
                final String moduleName = m.getModuleInfo().name();
                configBuilder.append("Toggle_").append(moduleName).append("_").append(m.isEnabled()).append("\r\n");

                if (m.getModuleInfo().category().equals(Category.HUD)) {
                    configBuilder.append("PositionX_").append(moduleName).append("_").append(m.getX()).append("\r\n");
                    configBuilder.append("PositionY_").append(moduleName).append("_").append(m.getY()).append("\r\n");
                }
                for (final Value s : m.getSettings()) {
                    if (s instanceof BoolValue) {
                        configBuilder.append("BoolValue_").append(moduleName).append("_").append(s.name).append("_").append(((BoolValue) s).enabled).append("\r\n");
                    }
                    if (s instanceof NumberValue) {
                        configBuilder.append("NumberValue_").append(moduleName).append("_").append(s.name).append("_").append(((NumberValue) s).value).append("\r\n");
                    }
                    if (s instanceof ModeValue) {
                        configBuilder.append("ModeValue_").append(moduleName).append("_").append(s.name).append("_").append(((ModeValue) s).getMode()).append("\r\n");
                    }
                }
                configBuilder.append("Bind_").append(moduleName).append("_").append(m.getKeyBind()).append("\r\n");
            }
        }

        FileUtil.saveFile("settings.txt", true, configBuilder.toString());
    }
}
