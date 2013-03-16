package org.marketcetera.webservices.systemmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.StringUtils;

/* $License$ */

/**
 * Provides a web services-capable object wrapping a <code>String</code> value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="string")
@XmlAccessorType(XmlAccessType.NONE)
public class WebServicesString
{
    /**
     * Create a new WebServicesString instance.
     */
    public WebServicesString() {}
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("WebServicesString [value=").append(value).append("]");
        return builder.toString();
    }
    /**
     * Get the value value.
     *
     * @return a <code>String</code> value
     */
    public String getValue()
    {
        return value;
    }
    /**
     * Sets the value value.
     *
     * @param inValue a <code>String</code> value
     */
    public void setValue(String inValue)
    {
        value = inValue;
    }
    /**
     * Adapts <code>WebServicesString</code> values for XML marshalling and unmarshalling.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class WebServicesStringAdapter
            extends XmlAdapter<String,String>
    {
        /* (non-Javadoc)
         * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
         */
        @Override
        public String unmarshal(String inValue)
                throws Exception
        {
            inValue = StringUtils.trimToNull(inValue);
            if(inValue == null) {
                return null;
            }
            // trim out the CDATA and closure
            return inValue.substring(START.length(),
                                     inValue.length() - END.length());
        }
        /* (non-Javadoc)
         * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
         */
        @Override
        public String marshal(String inValue)
                throws Exception
        {
            return new StringBuilder().append(START).append(inValue).append(END).toString();
        }
        /**
         * XML-compatible marker for the beginning of the wrapped value
         */
        private static final String START = "![CDATA[";
        /**
         * XML-compatible marker for the end of the wrapped value
         */
        private static final String END = "]]";
    }
    /**
     * wrapped inner value
     */
    @XmlElement
    @XmlJavaTypeAdapter(WebServicesString.WebServicesStringAdapter.class)
    private String value;
}
