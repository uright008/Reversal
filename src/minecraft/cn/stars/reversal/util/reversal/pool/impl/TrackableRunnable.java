package cn.stars.reversal.util.reversal.pool.impl;

import cn.stars.reversal.util.reversal.pool.ObjectPool;
import cn.stars.reversal.util.reversal.pool.ReleaseTracker;
import cn.stars.reversal.util.reversal.pool.TrackablePooledObject;

public class TrackableRunnable implements Runnable, TrackablePooledObject {
    private final ObjectPool<TrackableRunnable> pool;
    private Runnable task;
    private int poolIndex;
    private ReleaseTracker tracker;
    private boolean autoRelease;

    public TrackableRunnable(ObjectPool<TrackableRunnable> pool) {
        this.pool = pool;
    }

    public TrackableRunnable setup(Runnable task) {
        this.task = task;
        return this;
    }

    public TrackableRunnable autoRelease() {
        autoRelease = true;
        return this;
    }

    @Override
    public void run() {
        try {
            if (task != null) {
                task.run();
            }
        } finally {
            if (autoRelease) {
                release();
            }
        }
    }

    public void release() {
        if (tracker != null) {
            tracker.releaseAll();
        }
        pool.release(this);
    }

    @Override
    public void reset() {
        task = null;
        tracker = null;
    }

    @Override
    public int getPoolIndex() { return poolIndex; }

    @Override
    public void setPoolIndex(int index) { poolIndex = index; }

    @Override
    public void setTracker(ReleaseTracker tracker) {
        this.tracker = tracker;
        tracker.track(this);
    }
}