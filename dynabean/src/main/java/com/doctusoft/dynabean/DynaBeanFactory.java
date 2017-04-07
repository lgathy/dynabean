package com.doctusoft.dynabean;

import java.util.Map;

/**
 * Standard interface of the factory capable of creating dynabeans.
 */
public interface DynaBeanFactory {

    /**
     * This method instantiates a new, empty dynabean instance implementing the given beanInterfaceClass. All getters
     * called on the returned instance without setting any properties (by calling its setters) will return null, or
     * in case of primitive return types: their defaults.
     *
     * @param beanInterfaceClass must refer to an interface, otherwise {@link IllegalArgumentException} will be thrown
     * @param <T>                The generic type of the dynabean to instantiate
     * @return the new, empty dynabean instance
     */
    <T> T create(Class<T> beanInterfaceClass);
    
    /**
     * Creates a new dynabean instance of the given beanInterfaceClass, but unlike {@link #create(Class)} the caller can
     * specify custom initial values for the properties of that instance.
     * <p>
     * It is supported to set initial values for read-only properties as well. No type checks are done on the values,
     * thus if misused, calling the getters of the returned instance could potentially throw {@link ClassCastException}.
     * <p>
     * To get the initial values the given initializer will be called for each property with a getter method. The name
     * of the property and its getter {@link java.lang.reflect.Method} will be passed to the initializer and the
     * returned object will be used as the initial value of that property. The type of the property can be determined
     * as the return type of the getter method. Where no initial value is required for the property.
     *
     * @param beanInterfaceClass the class of the interface to create an instance of
     * @param initializer        a callback to determine the initial value for the properties
     * @param <T>                The generic type of the dynabean to instantiate
     * @return a new dynabean instance initialized with values given in the properties map
     */
    <T> T createWithInitializer(Class<T> beanInterfaceClass, PropertyInitializer initializer);
    
    /**
     * Creates a new dynabean instance of the given beanInterfaceClass, but unlike {@link #create(Class)} the caller can
     * specify custom initial values for the properties of that instance.
     * <p>
     * It is supported to set initial values for read-only properties as well. No type checks are done on the values,
     * thus if misused, calling the getters of the returned instance could potentially throw {@link ClassCastException}.
     * <p>
     * Properties without an initial value will act as in case of an empty dynabean instance: unless set otherwise by
     * calling the setters, they will return either null or the default of their primitive type.
     *
     * @param beanInterfaceClass the class of the interface to create an instance of
     * @param initialValues      a map containing the initial values per the name of the properties
     * @param <T>                The generic type of the dynabean to instantiate
     * @return a new dynabean instance initialized with values given in the properties map
     */
    <T> T createWithInitialValues(Class<T> beanInterfaceClass, Map<String, ?> initialValues);

    /**
     * Creates a new dynabean instance pre-initialized with the copied property values of the given instance.
     * <ul>
     * <li>All property values implementing the Cloneable interface should be cloned.</li>
     * <li>Lists and Sets among the property values should be copied as well.</li>
     * </ul>
     *
     * @param beanInterfaceClass the class of the interface to create an instance of
     * @param instance           the original instance from which the property values are copied
     * @param <T>                The generic type of the dynabean to instantiate
     * @return the newly created dynabean instance
     */
    <T> T copyProperties(Class<T> beanInterfaceClass, T instance);
    
}
