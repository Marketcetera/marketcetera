package org.marketcetera.trade;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.Messages;
import org.apache.commons.lang.ObjectUtils;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

/**
 * Represents a Security's trading symbol. The symbol
 * is used to identify the security being traded within 
 * trading messages like orders and reports. 
 * <p>
 * No processing is done on the supplied symbol. It is 
 * used unmodified within the trading messages. The symbol
 * being used must be the correct symbol for the security 
 * as defined by the broker / exchange that its been traded on.
 *
 * @author Graham Miller
 * @author anshul@marketcetera.com
 * @version $Id$
 * @Since 0.5.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@ClassVersion("$Id$")
public class MSymbol implements Serializable {
    private final String mFullSymbol;
    private final SecurityType mSecurityType;

    /**
     * Creates a new instance.
     *
     * @param inFullSymbol the symbol value.
     */
    public MSymbol(String inFullSymbol){
        this(inFullSymbol, null);
    }

    /**
     * Creates a new instance.
     *
     * @param inFullSymbol the symbol value.
     * @param inSecurityType the security type.
     */
    public MSymbol(String inFullSymbol, SecurityType inSecurityType) {
        if (inFullSymbol == null){
            throw new IllegalArgumentException(Messages.ERROR_NULL_MSYMBOL.getText());
        }
        mFullSymbol = inFullSymbol;
        mSecurityType = inSecurityType;
    }


    /**
     * Creates an instance. This empty constructor is intended for use
     * by JAXB.
     */

    protected MSymbol() {
    	mFullSymbol = null;
    	mSecurityType = null;
    }

    /**
     * Returns the full symbol value.
     * 
     * @return the full symbol value.
     */
    public String getFullSymbol() {
        return mFullSymbol;
    }

    /**
     * Returns the Security Type for this Symbol.
     *
     * @return the security type of this symbol.
     */
    public SecurityType getSecurityType() {
        return mSecurityType;
    }

    @Override
    public String toString(){
        return getFullSymbol();
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof MSymbol) {
            MSymbol aSymbol = (MSymbol) obj;
            return ObjectUtils.equals(getFullSymbol(),aSymbol.getFullSymbol()) &&
                ObjectUtils.equals(getSecurityType(), aSymbol.getSecurityType());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getFullSymbol() == null? 0: getFullSymbol().hashCode();
    }

    private static final long serialVersionUID = 2L;
}
