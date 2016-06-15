package com.doctusoft.dynabean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

import static java.util.Objects.*;

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
            String methodName = method.getName();
            if (methodName.equals("equals")) {
                args[0] = asDynaBeanInstanceOrNull(args[0]);
            }
            return method.invoke(this, args);
        }
        return method.getDefaultValue(); // TODO #3
    }

    public Object get(String propertyName) {
        return propertiesMap.get(propertyName);
    }

    public void set(String propertyName, Object value) {
        if (value == null) {
            propertiesMap.remove(propertyName);
        } else {
            propertiesMap.put(propertyName, value);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof DynaBeanInstance) {
            DynaBeanInstance other = (DynaBeanInstance) obj;
            return beanDefinition.equals(other.beanDefinition)
                && propertiesMap.equals(other.propertiesMap);
        }
        return false;
    }

    public int hashCode() {
        return 961 + 31 * beanDefinition.hashCode() + propertiesMap.hashCode();
    }

    static BeanProperties accessProperties(Object dynabean) {
        if (!isProxyWithDynaBeanMarker(dynabean)) {
            throw new IllegalArgumentException("Not a dynabean instance: " + dynabean);
        }
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(dynabean);
        if (!DynaBeanInstance.class.isInstance(invocationHandler)) {
            throw new IllegalArgumentException("Unrecognized invocationHandler: " + invocationHandler);
        }
        return (BeanProperties) invocationHandler;
    }

    static boolean isProxyWithDynaBeanMarker(Object instance) {
        return (instance instanceof DynaBean) && Proxy.isProxyClass(instance.getClass());
    }

    private static DynaBeanInstance asDynaBeanInstanceOrNull(Object instance) {
        if (!isProxyWithDynaBeanMarker(instance)) return null;
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
        return invocationHandler instanceof DynaBeanInstance ? (DynaBeanInstance) invocationHandler : null;
    }

}
