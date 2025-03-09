package cn.stars.reversal.value;

import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Value {
    public String name;
    public String localizedName;
    public boolean hidden;

    public float guiX;
    public float guiY;

    public Animation yAnimation = new Animation(Easing.EASE_OUT_EXPO, 500);
}
