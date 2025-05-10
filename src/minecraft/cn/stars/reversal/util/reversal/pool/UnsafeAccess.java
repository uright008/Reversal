package cn.stars.reversal.util.reversal.pool;

import java.lang.reflect.Field;

final class UnsafeAccess {
    static final sun.misc.Unsafe UNSAFE;
    
    static {
        try {
            Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (sun.misc.Unsafe) f.get(null);
        } catch (Exception ex) { throw new Error(ex); }
    }
}