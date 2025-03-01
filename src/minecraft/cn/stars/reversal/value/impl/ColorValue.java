package cn.stars.reversal.value.impl;

import cn.stars.reversal.module.Module;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.util.render.ThemeType;
import cn.stars.reversal.util.render.ThemeUtil;
import cn.stars.reversal.value.Value;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class ColorValue extends Value {
    private float hue = 0;
    private float saturation = 1;
    private float brightness = 1;
    private boolean themeColor = true;
    private boolean dontShowThemeColor;

    public ColorValue(String name, final Module parent, final boolean dontShowThemeColor) {
        this.name = name;
        this.dontShowThemeColor = dontShowThemeColor;
        parent.settings.add(this);
        parent.settingsMap.put(name.toLowerCase(), this);
        this.setColor(new Color(20,250,255));
    }

    public ColorValue(String name, final Module parent) {
        this.name = name;
        this.dontShowThemeColor = false;
        parent.settings.add(this);
        parent.settingsMap.put(name.toLowerCase(), this);
        this.setColor(new Color(20,250,255));
    }

    public ColorValue(final String name, final Module parent, Color defaultColor, final boolean dontShowThemeColor) {
        this.name = name;
        this.dontShowThemeColor = dontShowThemeColor;
        parent.settings.add(this);
        parent.settingsMap.put(name.toLowerCase(), this);
        this.setColor(defaultColor);
    }

    public Color getColor() {
        return !dontShowThemeColor && themeColor ? ThemeUtil.getThemeColor(ThemeType.ARRAYLIST) : Color.getHSBColor(hue, saturation, brightness);
    }

    public Color getAltColor() {
        return ColorUtil.darker(getColor(), .6f);
    }


    public void setColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
    }

    public void setColor(float hue, float saturation, float brightness) {
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
    }

    public String getHexCode() {
        Color color = getColor();
        return String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

}
