package com.doctusoft.dynabean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

import static java.util.Objects.*;

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
        Invoker invoker = new Invoker(beanDefinition);
        Object dynaBeanInstance = Proxy.newProxyInstance(classLoader, interfaces, invoker);
        return (T) dynaBeanInstance;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T buildWithProperties(Class<T> beanInterfaceClass, Map<String, ?> properties) {
        BeanDefinition beanDefinition = getOrComputeBeanDefinition(beanInterfaceClass);
        Class<?>[] interfaces = { beanInterfaceClass, DynaBean.class };
        Invoker invoker = new Invoker(beanDefinition, properties);
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

    /**
     * Internal implementation class of the proxy invoker of a dynabean instance.
     */
    private static final class Invoker implements InvocationHandler, BeanProperties {
        
        private final BeanDefinition beanDefinition;
        
        private final TreeMap<String, Object> propertiesMap;
        
        private Invoker(BeanDefinition beanDefinition) {
            this.beanDefinition = requireNonNull(beanDefinition);
            this.propertiesMap = new TreeMap<>();
        }
        
        private Invoker(BeanDefinition beanDefinition, Map<String, ?> properties) {
            this.beanDefinition = requireNonNull(beanDefinition);
            this.propertiesMap = new TreeMap<>(properties);
        }
        
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            MethodDefinition methodDefinition = beanDefinition.getMethodDefinition(method);
            if (methodDefinition != null) {
                return methodDefinition.invoke(proxy, this, args);
            }
            if (method.getDeclaringClass().equals(Object.class)) {
                return method.invoke(this, args);
            }
            return method.getDefaultValue(); // TODO?
        }
        
        public Object get(String propertyName) {
            return propertiesMap.get(propertyName);
        }
        
        public void set(String propertyName, Object value) {
            propertiesMap.put(propertyName, value);
        }
    }
}
