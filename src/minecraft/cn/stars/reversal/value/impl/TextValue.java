package cn.stars.reversal.value.impl;

import cn.stars.reversal.module.Module;
import cn.stars.reversal.value.Value;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// TODO: Impl this in ClickGUI. Maybe a text field to input text or using command to set text.
public class TextValue extends Value {

    public String text;

    public TextValue(final String name, final Module parent, final String text) {
        this.name = name;
        parent.settings.add(this);
        parent.settingsMap.put(name.toLowerCase(), this);
        this.text = text;
    }
}
