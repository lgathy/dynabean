package com.doctusoft.dynabean;

/**
 * Marker interface that will be added on all instantiated dynabean instances.
 */
public interface DynaBean extends Cloneable {

    Object clone();
    
}
