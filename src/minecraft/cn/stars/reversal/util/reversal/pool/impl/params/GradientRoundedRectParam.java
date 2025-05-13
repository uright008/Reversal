package cn.stars.reversal.util.reversal.pool.impl.params;

import lombok.AllArgsConstructor;

import java.awt.*;

@AllArgsConstructor
public class GradientRoundedRectParam implements Param {
    public double x, y, width, height, radius;
    public Color color, color2;
    public boolean vertical;
}