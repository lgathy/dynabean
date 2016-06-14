package com.doctusoft.dynabean;

import java.util.*;

public interface DynaBeanFactory {
    
    <T> T create(Class<T> beanInterfaceClass);
    
    <T> T buildWithProperties(Class<T> beanInterfaceClass, Map<String, ?> properties);
    
}
