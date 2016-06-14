package com.doctusoft.dynabean;

interface BeanProperties {
    
    Object get(String propertyName);
    
    void set(String propertyName, Object value);
    
}
