package com.doctusoft.dynabean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * Internal implementation class of the proxy invoker of a dynabean instance.
 */
final class DynaBeanInstance implements InvocationHandler, BeanProperties {

    private final BeanDefinition beanDefinition;

    private final TreeMap<String, Object> propertiesMap;

    DynaBeanInstance(BeanDefinition beanDefinition) {
        this.beanDefinition = requireNonNull(beanDefinition);
        this.propertiesMap = new TreeMap<>();
    }

    DynaBeanInstance(BeanDefinition beanDefinition, TreeMap<String, Object> propertiesMap) {
        this.beanDefinition = requireNonNull(beanDefinition);
        this.propertiesMap = requireNonNull(propertiesMap);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodDefinition methodDefinition = beanDefinition.getMethodDefinition(method);
        if (methodDefinition != null) {
            return methodDefinition.invoke(proxy, this, args);
        }
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(this, args); // TODO #1
        }
        return method.getDefaultValue(); // TODO #3
    }

    public Object get(String propertyName) {
        return propertiesMap.get(propertyName);
    }

    public void set(String propertyName, Object value) {
        propertiesMap.put(propertyName, value);
    }

    static BeanProperties accessProperties(Object dynabean) {
        if (!DynaBean.class.isInstance(dynabean) && Proxy.isProxyClass(dynabean.getClass())) {
            throw new IllegalArgumentException("Not a dynabean instance: " + dynabean);
        }
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(dynabean);
        if (!DynaBeanInstance.class.isInstance(invocationHandler)) {
            throw new IllegalArgumentException("Unrecognized invocationHandler: " + invocationHandler);
        }
        return (BeanProperties) invocationHandler;
    }

}
