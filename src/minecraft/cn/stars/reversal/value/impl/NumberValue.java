package cn.stars.reversal.value.impl;

import cn.stars.reversal.module.Module;
import cn.stars.reversal.value.Value;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public final class NumberValue extends Value {
    public double value, minimum, maximum, increment;
    public double renderPercentage, percentage;
    List<String> replacements;

    public NumberValue(final String name, final Module parent, final double value, final double minimum, final double maximum, final double increment) {
        this.name = name;
        this.localizedName = name;
        parent.settings.add(this);
        parent.settingsMap.put(name.toLowerCase(), this);
        this.value = value;
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = increment;
    }

    public NumberValue(final String name, final String localizedName, final Module parent, final double value, final double minimum, final double maximum, final double increment) {
        this.name = name;
        this.localizedName = localizedName;
        parent.settings.add(this);
        parent.settingsMap.put(name.toLowerCase(), this);
        this.value = value;
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = increment;
    }

    public float getFloat() {
        return (float) this.value;
    }
    public int getInt() {
        return (int) this.value;
    }
}
