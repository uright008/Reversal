package cn.stars.reversal.util.render;

// Just a test.
public class PooledRenderUtil {
    /*private static final ObjectPool<RectRunnable> rectPool = new ObjectPool<>(RectRunnable::new, 128);
    private static final ObjectPool<RoundedRectRunnable> roundedRectPool = new ObjectPool<>(RoundedRectRunnable::new, 64);

    public static RenderRunnable<RectRunnable> rect(double x, double y, double width, double height, Color color) {
        return rectPool.obtain().setup(new RectParam(x, y, width, height, color)).autoRelease();
    }

    public static RenderRunnable<RoundedRectRunnable> roundedRect(double x, double y, double width, double height, double radius, Color color) {
        return roundedRectPool.obtain().setup(new RoundedRectParam(x, y, width, height, radius, color)).autoRelease();
    } */
}
