package cn.stars.reversal.value.impl;

import cn.stars.reversal.module.Module;
import cn.stars.reversal.value.Value;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextValue extends Value {
    public String text;

    public TextValue(final String name, final Module parent, final String text) {
        this.name = name;
        this.localizedName = name;
        parent.settings.add(this);
        parent.settingsMap.put(name.toLowerCase(), this);
        this.text = text;
    }

    public TextValue(final String name, final String localizedName, final Module parent, final String text) {
        this.name = name;
        this.localizedName = localizedName;
        parent.settings.add(this);
        parent.settingsMap.put(this.name.toLowerCase(), this);
        this.text = text;
    }
}
