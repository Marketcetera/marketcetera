package org.marketcetera.webservices.systemmodel;

import java.util.Set;

import javax.xml.bind.annotation.*;

import org.apache.commons.lang.Validate;
import org.codehaus.jackson.map.annotate.JsonRootName;
import org.marketcetera.api.dao.MutablePermission;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.PermissionAttribute;
import org.marketcetera.webservices.systemmodel.impl.JsonMarshallingProvider;

/* $License$ */

/**
 * Provides a web-services appropriate permission implementation.
 *
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="permission")
@XmlAccessorType(XmlAccessType.NONE)
@JsonRootName(value="permission")
public class WebServicesPermission
        extends WebServicesNamedObject
        implements MutablePermission
{
    /**
     * Create a new WebServicesPermission instance.
     */
    public WebServicesPermission() {}
    /**
     * Create a new WebServicesPermission instance.
     *
     * @param inPermission a <code>String</code> value
     */
    public WebServicesPermission(Permission inPermission)
    {
        copyAttributes(inPermission);
    }
    /**
     * Create a new WebServicesPermission instance.
     *
     * @param inPermissionValue a <code>String</code> value
     */
    public WebServicesPermission(String inPermissionValue)
    {
        WebServicesPermission proxyObject = JsonMarshallingProvider.getInstance().getService().unmarshal(inPermissionValue,
                                                                                                         WebServicesPermission.class);
        copyAttributes(proxyObject);
    }
    /**
     * Get the method value.
     *
     * @return a <code>Set<PermissionAttribute></code> value
     */
    public Set<PermissionAttribute> getMethod()
    {
        return method;
    }
    /**
     * Sets the method value.
     *
     * @param a <code>Set<PermissionAttribute></code> value
     */
    public void setMethod(Set<PermissionAttribute> inMethod)
    {
        method = inMethod;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        if(JsonMarshallingProvider.getInstance() != null &&
                JsonMarshallingProvider.getInstance().getService() != null) {
            return JsonMarshallingProvider.getInstance().getService().marshal(this);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("WebServicesPermission [method=").append(method).append("]");
        return builder.toString();
    }
    /**
     * Copies the attributes from the given object to this one.
     *
     * @param inPermission a <code>Permission</code> value
     * @throws IllegalArgumentException if the given object is <code>null</code>
     */
    private final void copyAttributes(Permission inPermission)
    {
        Validate.notNull(inPermission);
        super.copyAttributes(inPermission);
        setMethod(inPermission.getMethod());
    }
    /**
     * set of permissions assigned to this permission
     */
    @XmlElementWrapper(name="methods")
    @XmlElement(name="method")
    private Set<PermissionAttribute> method;
}
