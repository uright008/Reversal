package cn.stars.reversal.util.reversal.pool.impl.params;

import lombok.AllArgsConstructor;

import java.awt.*;

@AllArgsConstructor
public class RoundedRectParam implements Param {
    public double x, y, width, height, radius;
    public Color color;
}