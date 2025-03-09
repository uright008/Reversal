package cn.stars.reversal.value.impl;

import cn.stars.reversal.module.Module;
import cn.stars.reversal.value.Value;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoolValue extends Value {

    public boolean enabled;

    public BoolValue(final String name, final Module parent, final boolean enabled) {
        this.name = name;
        this.localizedName = name;
        parent.settings.add(this);
        parent.settingsMap.put(name.toLowerCase(), this);
        this.enabled = enabled;
    }

    public BoolValue(final String name, final String localizedName, final Module parent, final boolean enabled) {
        this.name = name;
        this.localizedName = localizedName;
        parent.settings.add(this);
        parent.settingsMap.put(name.toLowerCase(), this);
        this.enabled = enabled;
    }

    public void toggle() {
        enabled = !enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
