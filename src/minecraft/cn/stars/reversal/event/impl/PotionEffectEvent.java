package cn.stars.reversal.event.impl;

import cn.stars.reversal.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.potion.PotionEffect;

@Getter
@Setter
@AllArgsConstructor
public class PotionEffectEvent extends Event {
    public PotionEffect effect;
}
