package cn.stars.reversal.util.reversal.pool.impl;

import cn.stars.reversal.util.reversal.pool.ObjectPool;
import cn.stars.reversal.util.reversal.pool.PooledObject;
import cn.stars.reversal.util.reversal.pool.impl.params.Param;

public class RenderRunnable<T extends PooledObject> implements Runnable, PooledObject {
    private final transient ObjectPool<T> pool;
    private int poolIndex;
    public boolean autoRelease;

    public RenderRunnable(ObjectPool<T> pool) {
        this.pool = pool;
    }

    public RenderRunnable<T> setup(Param param) { return this; }

    public void render() {}

    public RenderRunnable<T> autoRelease() {
        autoRelease = true;
        return this;
    }

    @Override
    public void run() {
        try {
            this.render();
        } finally {
            if (autoRelease) {
                pool.release((T) this);
            }
        }
    }

    public void release() {
        pool.release((T) this);
    }

    @Override
    public void reset() {
    }

    @Override
    public int getPoolIndex() { return poolIndex; }

    @Override
    public void setPoolIndex(int index) { poolIndex = index; }

}