package cn.stars.reversal.util.reversal.pool;

import java.util.*;

public final class TrackingObjectPool<T extends TrackablePooledObject> extends ObjectPool<T> {
    private final Set<T> borrowedObjects = Collections.newSetFromMap(new WeakHashMap<>());
    private final Object lock = new Object();

    public TrackingObjectPool(PooledObjectFactory<T> factory, int capacity) {
        super(factory, capacity);
    }

    @Override
    public T obtain() {
        T obj = super.obtain();
        synchronized (lock) {
            borrowedObjects.add(obj);
        }
        obj.setTracker(new ReleaseTrackerImpl());
        return obj;
    }

    @Override
    public void release(T obj) {
        synchronized (lock) {
            if (borrowedObjects.remove(obj)) {
                super.release(obj);
            }
            // Don't handle released object.
        }
    }

    public void releaseAllBorrowed() {
        synchronized (lock) {
            List<T> toRelease = new ArrayList<>(borrowedObjects);
            for (T obj : toRelease) {
                if (borrowedObjects.remove(obj)) {
                    super.release(obj);
                }
            }
        }
    }

    private class ReleaseTrackerImpl implements ReleaseTracker {
        @Override
        public void track(TrackablePooledObject obj) {
            // 不再需要额外操作
        }

        @Override
        public void releaseAll() {
            TrackingObjectPool.this.releaseAllBorrowed();
        }
    }

    @Override
    public String toString() {
        return String.format("TrackingObjectPool{Max: %d, Used: %d, Available: %d}", getMaxSize(), getUsedSize(), getAvailableSize());
    }
}