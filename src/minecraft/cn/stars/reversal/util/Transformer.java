package cn.stars.reversal.util;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.module.impl.misc.CustomName;
import cn.stars.reversal.util.misc.ModuleInstance;
import net.minecraft.client.gui.GuiScreen;

public class Transformer implements GameInstance {
    public static GuiScreen transformMainMenu() {
        return Reversal.atomicMenu;
    }

    public static String constructString(String string) {
        String result = string;
        if (ModuleInstance.getModule(CustomName.class).isEnabled() && Reversal.customName != null) {
            result = result.replace(mc.session.getUsername(), Reversal.customName.replace("&", "§"));
        }
        return result;
    }

    public static String getIRCTitle(String name) {
        String userTitle = RainyAPI.ircUser.users.get(name.toLowerCase());
        if (userTitle != null && !userTitle.isEmpty()) {
            return "§7[" + userTitle.replace("&", "§") + "§r§7]";
        } else {
            return "";
        }
    }
}
