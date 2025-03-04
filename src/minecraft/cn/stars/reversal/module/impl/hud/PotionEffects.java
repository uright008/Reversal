/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2024 Starlight, All rights reserved.
 */
package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.event.impl.UpdateEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.render.ThemeType;
import cn.stars.reversal.util.render.ThemeUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
// TODO: complete this.
@ModuleInfo(name = "PotionEffects", chineseName = "效果显示", description = "Display your potion effects on the screen", chineseDescription = "在屏幕上显示药水效果", category = Category.HUD)
public class PotionEffects extends Module {
    private final ArrayList<PotionData> potions = new ArrayList<>();
    public PotionEffects() {
        setX(100);
        setY(100);
        setCanBeEdited(true);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        int x = getX() + 1;
        int y = getY() + 1;
        int i = 0;
        for (PotionData potion : potions) {
        //    regular18.drawString(I18n.format(potionEffect.getEffectName()) + " " + getId(potionEffect.getAmplifier()) + " (" + getTime(potionEffect.getDuration() / 20) + ")", x, y + i * 10, ThemeUtil.getThemeColor(ThemeType.ARRAYLIST, i).getRGB());
            i++;
        }
        setWidth(150);
        setHeight(i * 10 + 10);
    }
    private String getId(int num) {
        if (num < 0 || num > 10) {
            return String.valueOf(num);
        }

        switch (num) {
            case 0: return "I";
            case 1: return "II";
            case 2: return "III";
            case 3: return "IV";
            case 4: return "V";
            case 5: return "VI";
            case 6: return "VII";
            case 7: return "VIII";
            case 8: return "IX";
            case 9: return "X";
            default: return String.valueOf(num);
        }
    }

    private String getTime(int totalSeconds) {
        // 计算分钟和秒数
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        // 格式化输出，确保秒数为两位数
        return String.format("%d:%02d", minutes, seconds);
    }

    class PotionData {
        public PotionEffect effect;
        public String name;
        public int id;
        public int amplifier;
        public int duration;

        public PotionData(PotionEffect effect, String name, int amplifier, int duration) {
            this.effect = effect;
            this.name = name;
            this.amplifier = amplifier;
            this.duration = duration;
        }
    }
}
