package cn.stars.reversal.util.reversal.pool;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class ObjectPool<T extends PooledObject> {
    private static final sun.misc.Unsafe UNSAFE = getUnsafe();
    private static final int BASE = UNSAFE.arrayBaseOffset(long[].class);
    private static final int SCALE = UNSAFE.arrayIndexScale(long[].class);

    private final PooledObjectFactory<T> factory;
    private final T[] pool;
    private final long[] availabilityMap;
    private final int capacity;
    private int nextWord;
    private int remaining;

    public ObjectPool(PooledObjectFactory<T> factory, int capacity) {
        // 确保容量是2的幂次方且是64的倍数
        this.capacity = 1 << (32 - Integer.numberOfLeadingZeros(capacity - 1));
        this.factory = factory;
        this.pool = (T[]) new PooledObject[this.capacity];
        this.availabilityMap = new long[this.capacity >>> 6];
        this.remaining = this.capacity;

        Arrays.fill(availabilityMap, 0xFFFFFFFFFFFFFFFFL);
        preallocateObjects();
    }

    public T obtain() {
        if (remaining == 0) throw new IllegalStateException("Pool is exhausted");

        // 直接通过UNSAFE操作内存
        final int wordIdx = nextWord & ((availabilityMap.length - 1));
        long word = UNSAFE.getLongVolatile(availabilityMap, BASE + (wordIdx * SCALE));

        while (true) {
            final int trailingZeros = Long.numberOfTrailingZeros(word);
            if (trailingZeros != 64) {
                final int bitIdx = trailingZeros;
                final int index = (wordIdx << 6) + bitIdx;

                // CAS操作更新位图
                final long updated = word & ~(1L << bitIdx);
                if (UNSAFE.compareAndSwapLong(availabilityMap,
                        BASE + (wordIdx * SCALE), word, updated)) {

                    remaining--;
                    nextWord = (wordIdx + 1) & (availabilityMap.length - 1);
                    final T obj = pool[index];
                    factory.onBorrow(obj);
                    return obj;
                }
                word = UNSAFE.getLongVolatile(availabilityMap, BASE + (wordIdx * SCALE));
            } else {
                nextWord = (wordIdx + 1) & (availabilityMap.length - 1);
                word = UNSAFE.getLongVolatile(availabilityMap,
                        BASE + (nextWord * SCALE));
            }
        }
    }

    public void release(T obj) {
        final int index = obj.getPoolIndex();
        final int wordIdx = index >>> 6;
        final int bitIdx = index & 63;

        long word;
        do {
            word = UNSAFE.getLongVolatile(availabilityMap, BASE + (wordIdx * SCALE));
        } while (!UNSAFE.compareAndSwapLong(
                availabilityMap,
                BASE + (wordIdx * SCALE),
                word,
                word | (1L << bitIdx)
        ));

        remaining++;
        factory.onRelease(obj);
    }

    private void preallocateObjects() {
        for (int i = 0; i < capacity; i++) {
            T obj = factory.create(this);
            obj.setPoolIndex(i);
            pool[i] = obj;
        }
    }

    private static sun.misc.Unsafe getUnsafe() {
        try {
            Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (sun.misc.Unsafe) f.get(null);
        } catch (Exception ex) { throw new Error(ex); }
    }

    public int getMaxSize() {
        return capacity;
    }

    public int getUsedSize() {
        return capacity - remaining;
    }

    public int getAvailableSize() {
        return remaining;
    }

    @Override
    public String toString() {
        return String.format("ObjectPool{Max: %d, Used: %d, Available: %d}", getMaxSize(), getUsedSize(), getAvailableSize());
    }
}