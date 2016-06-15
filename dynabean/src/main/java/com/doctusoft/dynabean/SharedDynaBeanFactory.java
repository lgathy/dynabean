package com.doctusoft.dynabean;

import java.util.concurrent.*;

/**
 * A thread-safe implementation of the {@link DynaBeanFactory} interface. This implementation stores the bean
 * definitions in a {@link ConcurrentHashMap} for best performance and uses a <b>minimal-lock approach</b>:
 * during calculation of yet unknown dynabean type definitions locking is only applied while changes are made to the
 * shared map. This can result in calculation of the same {@link BeanDefinition} instance being run multiple times in
 * concurrent threads, but it is guaranteed that all threads will always use the same {@link BeanDefinition} instance
 * for creating the dynabean instances (the concurrently re-calculated definition instances are dropped).
 */
public final class SharedDynaBeanFactory extends AbstractDynaBeanFactory {
    
    private final ConcurrentHashMap<Class<?>, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(64);
    
    protected BeanDefinition getOrComputeBeanDefinition(Class<?> beanInterfaceClass) {
        BeanDefinition def = beanDefinitionMap.get(beanInterfaceClass);
        if (def != null) {
            return def;
        }
        def = computeBeanDefinition(beanInterfaceClass);
        BeanDefinition old = beanDefinitionMap.putIfAbsent(beanInterfaceClass, def);
        return old == null ? def : old;
    }
    
}
