package com.doctusoft.dynabean;

import java.lang.reflect.Proxy;
import java.util.*;

/**
 * An extendable base class for the {@link DynaBeanFactory} interface. Subclasses must implement the
 * {@link #getOrComputeBeanDefinition(Class)} method to provide a fully functional implementation.
 */
public abstract class AbstractDynaBeanFactory implements DynaBeanFactory {
    
    private final ClassLoader classLoader = getClass().getClassLoader();
    
    protected abstract BeanDefinition getOrComputeBeanDefinition(Class<?> beanInterfaceClass);
    
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> beanInterfaceClass) {
        BeanDefinition beanDefinition = getOrComputeBeanDefinition(beanInterfaceClass);
        Class<?>[] interfaces = { beanInterfaceClass, DynaBean.class };
        DynaBeanInstance invoker = new DynaBeanInstance(beanDefinition);
        Object dynaBeanInstance = Proxy.newProxyInstance(classLoader, interfaces, invoker);
        return (T) dynaBeanInstance;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T buildWithProperties(Class<T> beanInterfaceClass, Map<String, ?> properties) {
        BeanDefinition beanDefinition = getOrComputeBeanDefinition(beanInterfaceClass);
        Class<?>[] interfaces = { beanInterfaceClass, DynaBean.class };
        DynaBeanInstance invoker = new DynaBeanInstance(beanDefinition, properties);
        Object dynaBeanInstance = Proxy.newProxyInstance(classLoader, interfaces, invoker);
        return (T) dynaBeanInstance;
    }
    
    protected BeanDefinition computeBeanDefinition(Class<?> beanInterfaceClass) {
        if (!beanInterfaceClass.isInterface()) {
            throw new IllegalArgumentException("Not an interface: " + beanInterfaceClass);
        }
        BeanDefinition.Builder builder = BeanDefinition.builder(beanInterfaceClass);
        for (Class<?> superInterface : collectAllSuperInterface(beanInterfaceClass)) {
            builder.mergeSuperclassDefinition(getOrComputeBeanDefinition(superInterface));
        }
        return builder.build();
    }
    
    private static Collection<Class<?>> collectAllSuperInterface(Class<?> interfaceClass) {
        LinkedHashSet<Class<?>> collector = new LinkedHashSet<>();
        collectAllSuperInterface(collector, interfaceClass);
        return collector;
    }
    
    private static void collectAllSuperInterface(LinkedHashSet<Class<?>> collector, Class<?> interfaceClass) {
        for (Class<?> superInterface : interfaceClass.getInterfaces()) {
            if (collector.add(superInterface)) {
                collectAllSuperInterface(collector, superInterface);
            }
        }
    }

}
