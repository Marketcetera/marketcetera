package org.marketcetera.webservices.systemmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.marketcetera.api.security.User;

/* $License$ */

/**
 * Provides a web-services appropriate user implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name = "users")
@XmlAccessorType(XmlAccessType.FIELD)
public class WebServicesUser
{
    /**
     * Get the username value.
     *
     * @return a <code>String</code> value
     */
    public String getUsername()
    {
        return username;
    }
    /**
     * Sets the username value.
     *
     * @param inUsername <code>String</code> value
     */
    public void setUsername(String inUsername)
    {
        username = inUsername;
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
     * @param inId <code>long</code> value
     */
    public void setId(long inId)
    {
        id = inId;
    }
    /**
     * Create a new WebServicesUser instance.
     *
     * @param inUser
     */
    public WebServicesUser(User inUser)
    {
        username = inUser.getUsername();
        id = inUser.getId();
    }
    /**
     * Create a new WebServicesUser instance.
     */
    public WebServicesUser()
    {
    }
    /**
     * username value
     */
    private String username;
    /**
     * id value
     */
    private long id;
}
