package com.doctusoft.dynabean;

import java.lang.reflect.Method;

public interface PropertyInitializer {
    
    Object get(String propertyName, Method getter);
    
}
