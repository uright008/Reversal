package cn.stars.reversal.util.reversal.pool.impl.render;

import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.reversal.pool.ObjectPool;
import cn.stars.reversal.util.reversal.pool.impl.RenderRunnable;
import cn.stars.reversal.util.reversal.pool.impl.params.Param;
import cn.stars.reversal.util.reversal.pool.impl.params.RoundedRectParam;

import java.awt.*;

public class RoundedRectRunnable extends RenderRunnable<RoundedRectRunnable> {
    double x,y,width,height,radius;
    Color color;

    public RoundedRectRunnable(ObjectPool<RoundedRectRunnable> pool) {
        super(pool);
    }

    @Override
    public RenderRunnable<RoundedRectRunnable> setup(Param param) {
        RoundedRectParam roundedRectParam = (RoundedRectParam) param;
        this.x = roundedRectParam.x;
        this.y = roundedRectParam.y;
        this.width = roundedRectParam.width;
        this.height = roundedRectParam.height;
        this.radius = roundedRectParam.radius;
        this.color = roundedRectParam.color;
        return this;
    }

    @Override
    public void render() {
        RenderUtil.roundedRectangle(x, y, width, height, radius, color);
    }
}
