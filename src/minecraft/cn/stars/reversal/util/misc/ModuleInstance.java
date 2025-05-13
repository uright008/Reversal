package cn.stars.reversal.util.misc;

import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.impl.client.ClientSettings;
import cn.stars.reversal.module.impl.client.Interface;
import cn.stars.reversal.module.impl.client.PostProcessing;
import cn.stars.reversal.module.impl.hud.HUD;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NumberValue;
import cn.stars.reversal.value.impl.TextValue;
import lombok.NonNull;
import lombok.SneakyThrows;

@SuppressWarnings("all")
@NonNull
public class ModuleInstance {
    public static Module getModule(String moduleName) {
        return Reversal.moduleManager.getModule(moduleName);
    }
    @SneakyThrows
    public static <T extends Module> T getModule(Class<T> clazz) {
        return (T) Reversal.moduleManager.getByClass(clazz);
    }
    public static ModeValue getMode(String moduleName, String settingName) throws ClassCastException {
        return (ModeValue) Reversal.moduleManager.getSetting(moduleName, settingName);
    }
    public static BoolValue getBool(String moduleName, String settingName) throws ClassCastException {
        return (BoolValue) Reversal.moduleManager.getSetting(moduleName, settingName);
    }
    public static TextValue getText(String moduleName, String settingName) throws ClassCastException {
        return (TextValue) Reversal.moduleManager.getSetting(moduleName, settingName);
    }
    public static NumberValue getNumber(String moduleName, String settingName) throws ClassCastException {
        return (NumberValue) Reversal.moduleManager.getSetting(moduleName, settingName);
    }

    public static boolean canDrawHUD() {
        if (getModule(HUD.class).isEnabled()) {
            if (getModule(HUD.class).display_when_debugging.enabled) {
                return true;
            } else return !RainyAPI.mc.gameSettings.showDebugInfo;
        }
        return false;
    }

    public static PostProcessing getPostProcessing() {
        return ModuleInstance.getModule(PostProcessing.class);
    }

    public static ClientSettings getClientSettings() {
        return ModuleInstance.getModule(ClientSettings.class);
    }

    public static Interface getInterface() {
        return ModuleInstance.getModule(Interface.class);
    }

    /**
     * 客户端特殊功能
     */
    public static boolean isSpecialModule(Module module) {
        return module.getModuleInfo().category().equals(Category.CLIENT);
    }
}
