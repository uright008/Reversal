package de.florianmichael.vialoadingbase.util;

import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import lombok.Getter;

@Getter
public class ProtocolVersionAnimation {
    private final Animation hoverAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final Animation selectAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
}
