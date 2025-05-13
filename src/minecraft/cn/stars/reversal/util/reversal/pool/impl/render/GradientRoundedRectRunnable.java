package cn.stars.reversal.util.reversal.pool.impl.render;

import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.reversal.pool.ObjectPool;
import cn.stars.reversal.util.reversal.pool.impl.RenderRunnable;
import cn.stars.reversal.util.reversal.pool.impl.params.GradientRoundedRectParam;
import cn.stars.reversal.util.reversal.pool.impl.params.Param;

import java.awt.*;

public class GradientRoundedRectRunnable extends RenderRunnable<GradientRoundedRectRunnable> {
    double x,y,width,height,radius;
    Color color, color2;
    boolean vertical;

    public GradientRoundedRectRunnable(ObjectPool<GradientRoundedRectRunnable> pool) {
        super(pool);
    }

    @Override
    public RenderRunnable<GradientRoundedRectRunnable> setup(Param param) {
        GradientRoundedRectParam gradientRoundedRectParam = (GradientRoundedRectParam) param;
        this.x = gradientRoundedRectParam.x;
        this.y = gradientRoundedRectParam.y;
        this.width = gradientRoundedRectParam.width;
        this.height = gradientRoundedRectParam.height;
        this.radius = gradientRoundedRectParam.radius;
        this.color = gradientRoundedRectParam.color;
        this.color2 = gradientRoundedRectParam.color2;
        this.vertical = gradientRoundedRectParam.vertical;
        return this;
    }

    @Override
    public void render() {
        RenderUtil.roundedGradientRectangle(x, y, width, height, radius, color, color2, vertical);
    }
}
