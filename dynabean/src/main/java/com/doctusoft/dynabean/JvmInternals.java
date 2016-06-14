package com.doctusoft.dynabean;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.*;

import static java.util.Objects.*;

final class JvmInternals {
    
    private JvmInternals() {}
    
    static boolean isDefaultMethod(Method method) {
        return ((method.getModifiers() & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC)
            && method.getDeclaringClass().isInterface();
    }
    
    static <T extends AccessibleObject> T makeAccessible(T object) {
        object.setAccessible(true);
        return object;
    }
    
    static <T> Constructor<T> spyConstructorOrNull(Class<T> clazz, Class<?>... parameterTypes) {
        requireNonNull(clazz);
        try {
            return makeAccessible(clazz.getDeclaredConstructor(parameterTypes));
        } catch (NoSuchMethodException | SecurityException e) {
            return null;
        }
    }
    
    private static final Constructor<Lookup> LOOKUP_CONSTRUCTOR =
        spyConstructorOrNull(MethodHandles.Lookup.class, Class.class, int.class);

    static Lookup privateLookupOrNull(Class<?> clazz) {
        requireNonNull(clazz);
        if (LOOKUP_CONSTRUCTOR == null) {
            return null;
        }
        try {
            return LOOKUP_CONSTRUCTOR.newInstance(clazz, MethodHandles.Lookup.PRIVATE);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        } catch (InstantiationException | IllegalArgumentException e) {
            throw new AssertionError("JvmInternals is broken: cannot use MethodHandles.Lookup", e);
        }
    }
    
    static Lookup privateLookupIfPossible(Class<?> clazz) {
        Lookup privateLookup = privateLookupOrNull(clazz);
        return privateLookup == null ? MethodHandles.lookup().in(clazz) : privateLookup;
    }
    
}
