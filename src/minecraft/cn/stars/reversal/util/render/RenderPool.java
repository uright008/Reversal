package cn.stars.reversal.util.render;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.reversal.pool.impl.PooledRunnable;

import java.util.List;


public class RenderPool {
    public static PooledRunnable get(Runnable runnable) {
        return Reversal.runnablePool.obtain().setup(runnable).autoRelease();
    }
    public static void add(Runnable runnable) {
        Reversal.runnablePool.obtain().setup(runnable).autoRelease().run();
    }

    public static void releaseSafely(List<Runnable> runnables) {
        for (Runnable runnable : runnables) {
            if (runnable instanceof PooledRunnable) {
                Reversal.runnablePool.release((PooledRunnable) runnable);
            }
        }
    }
}
