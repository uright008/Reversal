package cn.stars.reversal.util.reversal.pool;

public interface PooledObject {
    default void reset() {};

    int getPoolIndex();

    void setPoolIndex(int index);
}