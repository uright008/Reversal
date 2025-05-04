package cn.stars.reversal.value.impl;

import cn.stars.reversal.module.Module;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.value.Value;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomValue extends Value {
    public Runnable runnable;
    public Animation hoverAnimation = new Animation(Easing.EASE_OUT_EXPO, 500);

    public CustomValue(final String name, final Module parent, final Runnable runnable) {
        this.name = name;
        this.localizedName = name;
        parent.settings.add(this);
        parent.settingsMap.put(name.toLowerCase(), this);
        this.runnable = runnable;
    }

    public CustomValue(final String name, final String localizedName, final Module parent, final Runnable runnable) {
        this.name = name;
        this.localizedName = localizedName;
        parent.settings.add(this);
        parent.settingsMap.put(this.name.toLowerCase(), this);
        this.runnable = runnable;
    }
}
