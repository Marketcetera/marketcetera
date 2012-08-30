package org.marketcetera.webservices.systemmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.core.systemmodel.Authority;

/* $License$ */

/**
 * Provides a web-services appropriate authority implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class WebServicesAuthority
{
    /**
     * Create a new WebServicesAuthority instance.
     */
    public WebServicesAuthority() {}
    /**
     * Create a new WebServicesAuthority instance.
     *
     * @param inAuthority a <code>String</code> value
     */
    public WebServicesAuthority(Authority inAuthority)
    {
        authority = inAuthority.getAuthority();
        id = inAuthority.getId();
    }
    /**
     * Get the authority value.
     *
     * @return a <code>String</code> value
     */
    public String getAuthority()
    {
        return authority;
    }
    /**
     * Sets the authority value.
     *
     * @param a <code>String</code> value
     */
    public void setAuthority(String inAuthority)
    {
        authority = inAuthority;
    }
    /**
     * Get the id value.
     *
     * @return a <code>long</code> value
     */
    public long getId()
    {
        return id;
    }
    /**
     * Sets the id value.
     *
     * @param a <code>long</code> value
     */
    public void setId(long inId)
    {
        id = inId;
    }
    /**
     * id value
     */
    private long id;
    /**
     * authority value
     */
    private String authority;
}
