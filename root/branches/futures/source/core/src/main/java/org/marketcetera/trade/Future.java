package org.marketcetera.trade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Identifies a future contract.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@ClassVersion("$Id$")
public class Future
        extends ExpirableInstrument
{
    /**
     * Create a new Future instance.
     *
     * @param inSymbolRoot a <code>String</code> value containing the future symbol root
     * @param inExpiry a <code>String</code> value containing the future expiry
     */
    public Future(String inSymbolRoot,
                  String inExpiry)
    {
        super(inSymbolRoot,
              inExpiry);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Instrument#getSecurityType()
     */
    @Override
    public SecurityType getSecurityType()
    {
        return SecurityType.Future;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this,
                                                      ToStringStyle.SHORT_PREFIX_STYLE).append("symbol", //$NON-NLS-1$
                                                                                               getSymbol())
                                                                                       .append("expiry", //$NON-NLS-1$
                                                                                               getExpiry());
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getExpiry() == null) ? 0 : getExpiry().hashCode());
        result = prime * result + ((getSymbol() == null) ? 0 : getSymbol().hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Future)) {
            return false;
        }
        Future other = (Future) obj;
        if (getExpiry() == null) {
            if (other.getExpiry() != null) {
                return false;
            }
        } else if (!getExpiry().equals(other.getExpiry())) {
            return false;
        }
        if (getSymbol() == null) {
            if (other.getSymbol() != null) {
                return false;
            }
        } else if (!getSymbol().equals(other.getSymbol())) {
            return false;
        }
        return true;
    }
    /**
     * Create a new Future instance.
     * 
     * Parameterless constructor for use only by JAXB.
     */
    protected Future()
    {
    }
    private static final long serialVersionUID = 1L;
}
