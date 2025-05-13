package cn.stars.reversal.util.reversal.pool.impl.render;

import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.reversal.pool.ObjectPool;
import cn.stars.reversal.util.reversal.pool.impl.RenderRunnable;
import cn.stars.reversal.util.reversal.pool.impl.params.Param;
import cn.stars.reversal.util.reversal.pool.impl.params.RectParam;

import java.awt.*;

public class RectRunnable extends RenderRunnable<RectRunnable> {
    double x,y,width,height;
    Color color;

    public RectRunnable(ObjectPool<RectRunnable> pool) {
        super(pool);
    }

    @Override
    public RenderRunnable<RectRunnable> setup(Param param) {
        RectParam rectParam = (RectParam) param;
        this.x = rectParam.x;
        this.y = rectParam.y;
        this.width = rectParam.width;
        this.height = rectParam.height;
        this.color = rectParam.color;
        return this;
    }

    @Override
    public void render() {
        RenderUtil.rect(x, y, width, height, color);
    }

    @Override
    public RenderRunnable<RectRunnable> autoRelease() {
        return super.autoRelease();
    }
}
