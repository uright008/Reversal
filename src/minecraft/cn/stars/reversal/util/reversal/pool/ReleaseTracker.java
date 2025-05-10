package cn.stars.reversal.util.reversal.pool;

public interface ReleaseTracker {
    void track(TrackablePooledObject obj);
    void releaseAll();
}