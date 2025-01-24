package com.github.skystardust.InputMethodBlocker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {
    public static Object getPrivateField(Class clazz,String name,Object object) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(object);
    }
    public static List<Field> getPrivateObjectList(Class clazz, Class type, Object object) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> list = new ArrayList<>();
        for (Field field : fields) {
            if (field.getType().equals(type)) {
                list.add(field);
            }
        }
        return list;
    }
}