package cn.stars.reversal.module.impl.player;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.event.impl.PreMotionEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.NumberValue;
import cn.stars.reversal.ui.notification.NotificationType;

@ModuleInfo(name = "HealthWarn", localizedName = "module.HealthWarn.name", description = "Give a warning to you on low health",
        localizedDescription = "module.HealthWarn.desc", category = Category.PLAYER)
public class HealthWarn extends Module {
    private final NumberValue health = new NumberValue("Health", this, 10, 1, 20, 1);
    private boolean canWarn;
    @Override
    public void onEnable() {
        canWarn = true;
    }
    @Override
    public void onDisable() {
        canWarn = true;
    }
    @Override
    public void onPreMotion(PreMotionEvent event) {
        if (mc.thePlayer.getHealth() <= health.getValue()) {
            if (canWarn) {
                Reversal.notificationManager.registerNotification("You dont have enough health!","HP Warning", 3000, NotificationType.WARNING, 5);
                canWarn = false;
            }
        } else {
            canWarn = true;
        }
    }
}
