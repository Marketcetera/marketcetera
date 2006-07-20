package org.marketcetera.jcyclone;

import org.jcyclone.core.queue.IElement;

/**
 * Wrapper class to represent different types of messages being
 * passed between the JCyclone stages
 *
 * We may want to make it non-abstract later, when we find more use
 * cases for a generic message to be sent through the stage queue
 *
 * @author gmiller
 * @version $Id$
 */

public abstract class StageElement implements IElement {
    private Object mElement;
    
    public StageElement(Object element){
        mElement = element;
    }

    /** Return the actual element that we wrapperd */
    public Object getElement(){
        return mElement;
    }
}
