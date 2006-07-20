package org.marketcetera.oms.jcycloneSample;

import org.jcyclone.core.queue.IElement;

/**
 * Subclass to encapsulate events for JCyclone
 * @author Toli Kuznets
 * @version $Id$
 */
public class SampleEvent implements IElement {
    private String data;
    public SampleEvent(String inData) {
        data = inData;
    }

    public String getData() {return data; }

    public String toString() {
        return getData();
    }
}
