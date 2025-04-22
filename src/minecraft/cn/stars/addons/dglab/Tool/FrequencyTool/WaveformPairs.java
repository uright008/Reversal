package cn.stars.addons.dglab.Tool.FrequencyTool;

import lombok.Getter;

@Getter
class WaveformPairs {
    private int time, Strength;

    public void setTime(int time) {
        this.time = Math.max(time, 0);
    }

    public void setStrength(int strength) {
        Strength = Math.min(100, Math.max(strength, 0));
    }
}