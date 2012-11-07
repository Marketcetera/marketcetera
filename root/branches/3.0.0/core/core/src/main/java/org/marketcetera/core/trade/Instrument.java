package org.marketcetera.core.trade;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.sun.xml.bind.AnyTypeAdapter;

/* $License$ */

/**
 * Represents the common attributes of an <code>Instrument</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement
@XmlJavaTypeAdapter(AnyTypeAdapter.class)
public interface Instrument
        extends Serializable
{
    /**
     * Gets the symbol value.
     *
     * @return a <code>String</code> value
     */
    public String getSymbol();
    /**
     * Gets the full symbol value.
     *
     * @return a <code>String</code> value
     */
    public String getFullSymbol();
    /**
     * Gets the security type value.
     *
     * @return a <code>SecurityType</code> value
     */
    public SecurityType getSecurityType();
}
