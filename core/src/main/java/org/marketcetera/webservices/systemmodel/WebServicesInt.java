package org.marketcetera.webservices.systemmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonRootName;
import org.marketcetera.webservices.systemmodel.impl.JsonMarshallingProvider;

/* $License$ */

/**
 * Provides a web-services capable implementation of an integer value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="int")
@XmlAccessorType(XmlAccessType.NONE)
@JsonRootName(value="int")
public class WebServicesInt
{
    /**
     * Create a new WebServicesInt instance.
     */
    public WebServicesInt() {}
    /**
     * Create a new WebServicesInt instance.
     *
     * @param inData a <code>WebServicesInt</code> value
     */
    public WebServicesInt(String inData)
    {
        WebServicesInt unmarshalledValue = JsonMarshallingProvider.getInstance().getService().unmarshal(inData,
                                                                                                        WebServicesInt.class);
        setValue(unmarshalledValue.getValue());
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        if(JsonMarshallingProvider.getInstance() == null ||
           JsonMarshallingProvider.getInstance().getService() == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("WebServicesInt [value=").append(value).append("]");
            return builder.toString();
        }
        return JsonMarshallingProvider.getInstance().getService().marshal(this);
    }
    /**
     * Get the value value.
     *
     * @return an <code>int</code> value
     */
    public int getValue()
    {
        return value;
    }
    /**
     * Sets the value value.
     *
     * @param inValue an <code>int</code> value
     */
    public void setValue(int inValue)
    {
        value = inValue;
    }
    /**
     * wrapped inner value
     */
    @XmlAttribute
    private int value;
}
