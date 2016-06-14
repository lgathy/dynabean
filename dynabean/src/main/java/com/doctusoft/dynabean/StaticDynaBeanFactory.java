package com.doctusoft.dynabean;

import java.util.concurrent.*;

public final class StaticDynaBeanFactory extends AbstractDynaBeanFactory {
    
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
