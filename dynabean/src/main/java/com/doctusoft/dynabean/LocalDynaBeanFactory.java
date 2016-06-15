package com.doctusoft.dynabean;

import java.util.*;

/**
 * An implementation of {@link DynaBeanFactory} which can safely be used locally or in a single threaded environment,
 * thus this implementation is NOT thread-safe. If you need to share the {@link DynaBeanFactory} instance between
 * multiple threads, use {@link SharedDynaBeanFactory} instead.
 */
public final class LocalDynaBeanFactory extends AbstractDynaBeanFactory {
    
    private final IdentityHashMap<Class<?>, BeanDefinition> beanDefinitionMap = new IdentityHashMap<>();
    
    protected BeanDefinition getOrComputeBeanDefinition(Class<?> beanInterfaceClass) {
        BeanDefinition def = beanDefinitionMap.get(beanInterfaceClass);
        if (def != null) {
            return def;
        }
        def = computeBeanDefinition(beanInterfaceClass);
        beanDefinitionMap.put(beanInterfaceClass, def);
        return def;
    }
    
}
