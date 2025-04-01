package cn.stars.reversal.module;

import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;

public enum Category {
    COMBAT,
    MOVEMENT,
    PLAYER,
    RENDER,
    MISC,
    WORLD,
    HUD,
    ADDONS,
    CLIENT;

    public final Animation alphaAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);

    // curiosity font
    public static String getCategoryIcon(Category c) {
        switch (c) {
            case COMBAT: {
                return "A";
            }
            case MOVEMENT: {
                return "B";
            }
            case PLAYER: {
                return "C";
            }
            case RENDER: {
                return "D";
            }
            case MISC: {
                return "E";
            }
            case WORLD: {
                return "F";
            }
            case HUD: {
                return "G";
            }
            case ADDONS: {
                return "H";
            }
            case CLIENT: {
                return "e";
            }
        }
        return "A";
    }
}