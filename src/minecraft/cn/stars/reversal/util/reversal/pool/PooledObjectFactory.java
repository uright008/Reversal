package cn.stars.reversal.util.reversal.pool;

public interface PooledObjectFactory<T extends PooledObject> {
    T create(ObjectPool<T> pool);

    default void onBorrow(T obj) { obj.reset(); }

    default void onRelease(T obj) { obj.reset(); }
}