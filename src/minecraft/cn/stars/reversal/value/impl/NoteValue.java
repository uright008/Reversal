package cn.stars.reversal.value.impl;

import cn.stars.reversal.module.Module;
import cn.stars.reversal.value.Value;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteValue extends Value {
    public NoteValue(final String name, final Module parent) {
        this.name = name;
        this.localizedName = name;
        parent.settings.add(this);
        parent.settingsMap.put(this.name.toLowerCase(), this);
    }

    public NoteValue(final String name, final String localizedName, final Module parent) {
        this.name = name;
        this.localizedName = localizedName;
        parent.settings.add(this);
        parent.settingsMap.put(this.name.toLowerCase(), this);
    }
}
