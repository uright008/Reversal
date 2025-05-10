package cn.stars.reversal.util.reversal.pool;

public interface TrackablePooledObject extends PooledObject {
    void setTracker(ReleaseTracker tracker);
}