package com.doctusoft.dynabean;

import java.util.*;

import static java.util.Objects.*;

/**
 * Adopted from Google guava (com.google.common.base.Defaults) to avoid dependency.
 */
final class Primitives {
    
    private Primitives() {}

    private static final Map<Class<?>, Object> DEFAULTS;
    
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_TYPE;

    static {
        Internal internal = new Internal();
        internal.put(boolean.class, Boolean.class, false);
        internal.put(char.class, Character.class, '\0');
        internal.put(byte.class, Byte.class, (byte) 0);
        internal.put(short.class, Short.class, (short) 0);
        internal.put(int.class, Integer.class, 0);
        internal.put(long.class, Long.class, 0L);
        internal.put(float.class, Float.class, 0f);
        internal.put(double.class, Double.class, 0d);
        DEFAULTS = Collections.unmodifiableMap(internal.defaultsMap);
        PRIMITIVE_TO_WRAPPER_TYPE = Collections.unmodifiableMap(internal.wrappersMap);
    }

    @SuppressWarnings("unchecked")
    public static <T> T defaultValue(Class<T> type) {
        return (T) DEFAULTS.get(requireNonNull(type));
    }
    
    public static Class<?> primitiveToWrapperType(Class<?> primitiveType) {
        requireNonNull(primitiveType);
        return PRIMITIVE_TO_WRAPPER_TYPE.get(primitiveType);
    }
    
    public static Class<?> wrap(Class<?> type) {
        requireNonNull(type);
        Class<?> wrapper = PRIMITIVE_TO_WRAPPER_TYPE.get(type);
        return wrapper == null ? type : wrapper;
    }
    
    private static final class Internal {
        
        private final Map<Class<?>, Object> defaultsMap = new HashMap<>();
        private final Map<Class<?>, Class<?>> wrappersMap = new HashMap<>();
        
        private <T> void put(Class<T> type, Class<?> wrapperClass, T defaultValue) {
            defaultsMap.put(type, defaultValue);
            wrappersMap.put(type, wrapperClass);
        }

    }
    
}
