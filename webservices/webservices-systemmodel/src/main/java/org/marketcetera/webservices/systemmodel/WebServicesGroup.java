package org.marketcetera.webservices.systemmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.api.security.User;
import org.marketcetera.core.systemmodel.Authority;
import org.marketcetera.core.systemmodel.Group;
import org.marketcetera.webservices.security.WebServicesUser;

/* $License$ */

/**
 * Provides a web-services appropriate group implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class WebServicesGroup
{
    /**
     * Create a new WebServicesGroup instance.
     */
    public WebServicesGroup() {}
    /**
     * Create a new WebServicesGroup instance.
     *
     * @param inGroup a <code>Group</code> value
     */
    public WebServicesGroup(Group inGroup)
    {
        id = inGroup.getId();
        name = inGroup.getName();
        for(User user : inGroup.getUsers()) {
            users.add(new WebServicesUser(user));
        }
        for(Authority authority : inGroup.getAuthorities()) {
            authorities.add(new WebServicesAuthority(authority));
        }
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
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }
    /**
     * Get the users value.
     *
     * @return a <code>List&lt;WebServicesUser&gt;</code> value
     */
    public List<WebServicesUser> getUsers()
    {
        return Collections.unmodifiableList(users);
    }
    /**
     * Get the authorities value.
     *
     * @return a <code>List&lt;WebServicesAuthority&gt;</code> value
     */
    public List<WebServicesAuthority> getAuthorities()
    {
        return Collections.unmodifiableList(authorities);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("WebServicesGroup [id=").append(id).append(", name=").append(name).append(", users=")
                .append(users).append(", authorities=").append(authorities).append("]");
        return builder.toString();
    }
    /**
     * id value
     */
    private long id;
    /**
     * name value
     */
    private String name;
    /**
     * users value
     */
    private final List<WebServicesUser> users = new ArrayList<WebServicesUser>();
    /**
     * authorities value
     */
    private final List<WebServicesAuthority> authorities = new ArrayList<WebServicesAuthority>();
}
