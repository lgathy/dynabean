package com.doctusoft.dynabean;

interface MethodDefinition {
    
    Object invoke(Object proxy, BeanProperties beanProperties, Object... arguments) throws Throwable;
    
}
