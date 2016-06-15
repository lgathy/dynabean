package com.doctusoft.dynabean;

/**
 * This interface standardizes how we can access properties of a bean regardless of its internal implementation.
 */
interface BeanProperties {
    
    Object get(String propertyName);
    
    void set(String propertyName, Object value);
    
}
