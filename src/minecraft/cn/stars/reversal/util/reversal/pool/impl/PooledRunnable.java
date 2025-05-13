package cn.stars.reversal.util.reversal.pool.impl;

import cn.stars.reversal.util.reversal.pool.ObjectPool;
import cn.stars.reversal.util.reversal.pool.PooledObject;

public class PooledRunnable implements Runnable, PooledObject {
    private final ObjectPool<PooledRunnable> pool;
    private Runnable task;
    private int poolIndex;
    private boolean autoRelease;

    public PooledRunnable(ObjectPool<PooledRunnable> pool) {
        this.pool = pool;
    }

    public PooledRunnable setup(Runnable task) {
        this.task = task;
        return this;
    }

    public PooledRunnable autoRelease() {
        autoRelease = true;
        return this;
    }

    @Override
    public void run() {
        try {
            task.run();
        } finally {
            if (autoRelease) {
                pool.release(this);
            }
        }
    }

    public void release() {
        pool.release(this);
    }

    @Override
    public void reset() { task = null; }

    @Override
    public int getPoolIndex() { return poolIndex; }

    @Override
    public void setPoolIndex(int index) { poolIndex = index; }

}