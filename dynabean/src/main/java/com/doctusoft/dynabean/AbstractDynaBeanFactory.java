package com.doctusoft.dynabean;

import com.doctusoft.dynabean.BeanDefinition.GetterMethod;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.*;

import static com.doctusoft.dynabean.DynaBeanInstance.copyPropertyValue;
import static java.util.Objects.*;

/**
 * An extendable base class for the {@link DynaBeanFactory} interface. Subclasses must implement the
 * {@link #getOrComputeBeanDefinition(Class)} method to provide a fully functional implementation.
 */
public abstract class AbstractDynaBeanFactory implements DynaBeanFactory {
    
    private final ClassLoader classLoader;
    
    protected AbstractDynaBeanFactory() {
        this.classLoader = getClass().getClassLoader();
    }
    
    protected AbstractDynaBeanFactory(ClassLoader classLoader) {
        this.classLoader = requireNonNull(classLoader, "classLoader");
    }
    
    protected abstract BeanDefinition getOrComputeBeanDefinition(Class<?> beanInterfaceClass);
    
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> beanInterfaceClass) {
        BeanDefinition beanDefinition = getOrComputeBeanDefinition(beanInterfaceClass);
        return DynaBeanInstance.createProxy(beanDefinition, new TreeMap<String, Object>());
    }
    
    @SuppressWarnings("unchecked")
    public <T> T createWithInitialValues(Class<T> beanInterfaceClass, Map<String, ?> initialValues) {
        BeanDefinition beanDefinition = getOrComputeBeanDefinition(beanInterfaceClass);
        if (!beanDefinition.beanInterfaceClass.equals(beanInterfaceClass)) {
            throw new IllegalStateException(
                "Wrong beanDefinition returned: " + beanDefinition + " for: " + beanInterfaceClass);
        }
        TreeMap<String, Object> propertiesMap = new TreeMap<>(initialValues);
        TreeSet<String> propertyNames = new TreeSet<>();
        for (MethodDefinition methodDefinition : beanDefinition.getMethodDefinitions().values()) {
            if (methodDefinition instanceof GetterMethod) {
                propertyNames.add(((GetterMethod) methodDefinition).propertyName);
            }
        }
        for (Iterator<Entry<String, Object>> it = propertiesMap.entrySet().iterator(); it.hasNext(); ) {
            if (!propertyNames.contains(it.next().getKey())) {
                it.remove();
            }
        }
        return DynaBeanInstance.createProxy(beanDefinition, propertiesMap);
    }
    
    public <T> T createWithInitializer(Class<T> beanInterfaceClass, PropertyInitializer initializer) {
        BeanDefinition beanDefinition = getOrComputeBeanDefinition(beanInterfaceClass);
        if (!beanDefinition.beanInterfaceClass.equals(beanInterfaceClass)) {
            throw new IllegalStateException(
                "Wrong beanDefinition returned: " + beanDefinition + " for: " + beanInterfaceClass);
        }
        TreeMap<String, Object> propertiesMap = new TreeMap<>();
        for (Entry<Method, MethodDefinition> e : beanDefinition.getMethodDefinitions().entrySet()) {
            MethodDefinition methodDefinition = e.getValue();
            if (methodDefinition instanceof GetterMethod) {
                GetterMethod getter = (GetterMethod) methodDefinition;
                String propertyName = getter.propertyName;
                Method method = e.getKey();
                Object value = initializer.get(propertyName, method);
                propertiesMap.put(propertyName, value);
            }
        }
        return DynaBeanInstance.createProxy(beanDefinition, propertiesMap);
    }
    
    public <T> T copyProperties(Class<T> beanInterfaceClass, T instance) {
        requireNonNull(instance);
        BeanDefinition beanDefinition = getOrComputeBeanDefinition(beanInterfaceClass);

        Object[] noArgs = {};
        TreeMap<String, Object> propertiesMap = new TreeMap<>();
        DynaBeanInstance dynabean = DynaBeanInstance.asDynaBeanInstanceOrNull(instance);
        if (dynabean != null && dynabean.beanDefinition.equals(beanDefinition)) {
            return (T) dynabean.cloneProxy();
        }
        try {
            for (Entry<Method, MethodDefinition> entry : beanDefinition.getMethodDefinitions().entrySet()) {
                if (entry.getValue() instanceof GetterMethod) {
                    GetterMethod getter = (GetterMethod) entry.getValue();
                    Object propertyValue = entry.getKey().invoke(instance, noArgs);
                    if (propertyValue != null) {
                        propertiesMap.put(getter.propertyName, copyPropertyValue(propertyValue));
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to copy properties. " + e.getMessage(), e);
        }
        return DynaBeanInstance.createProxy(beanDefinition, propertiesMap);
    }

    protected BeanDefinition computeBeanDefinition(Class<?> beanInterfaceClass) {
        if (!beanInterfaceClass.isInterface()) {
            throw new IllegalArgumentException("Not an interface: " + beanInterfaceClass);
        }
        BeanDefinition.Builder builder = new BeanDefinition.Builder(classLoader, beanInterfaceClass);
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
