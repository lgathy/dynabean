package com.doctusoft.dynabean;

import java.util.*;

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
     * @param <T> The generic type of the dynabean to instantiate
     * @return the new, empty dynabean instance
     */
    <T> T create(Class<T> beanInterfaceClass);

    /**
     * Creates a new dynabean instance of the given beanInterfaceClass, but unlike {@link #create(Class)} the caller can
     * specify custom initial values for the properties of that instance.
     *
     * It is supported to set initial values for read-only properties as well. No type checks are done on the values,
     * thus if misused, calling the getters of the returned instance could potentially throw {@link ClassCastException}.
     *
     * Properties without an initial value will act as in case of an empty dynabean instance: unless set otherwise by
     * calling the setters, they will return either null or the default of their primitive type.
     *
     * @param beanInterfaceClass the class of the interface to create an instance of
     * @param initialValues a map containing the initial values per the name of the properties
     * @param <T> The generic type of the dynabean to instantiate
     * @return a new dynabean instance initialized with values given in the properties map
     */
    <T> T createWithInitialValues(Class<T> beanInterfaceClass, Map<String, ?> initialValues);
    
}
