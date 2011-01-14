package org.marketcetera.server.config;

import java.util.Collections;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Contains the configuration for an order router instance.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Immutable
@ClassVersion("$Id$")
public class OrderRouter
{
    /**
     * Create a new <code>OrderRouter</code> instance.
     *
     * @param inName a <code>String</code> value
     * @param inId a <code>String</code> value
     * @throws IllegalArgumentException if the given arguments are not valid
     */
    public OrderRouter(String inName,
                       String inId)
    {
        name = StringUtils.trimToNull(inName);
        id = StringUtils.trimToNull(inId);
        Validate.notNull(name,
                         "Order router name must be specified");
        Validate.notNull(id,
                         "Order router id must be specified");
    }
    /**
     * Get the <code>OrderRouter</code> name.
     *
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }
    /**
     * Get the <code>OrderRouter</code> id.
     *
     * @return a <code>String</code> value
     */
    public String getId()
    {
        return id;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(id).toHashCode();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof OrderRouter == false) {
            return false;
        }
        if(this == obj) {
            return true;
        }
        final OrderRouter otherObject = (OrderRouter)obj;
        return new EqualsBuilder().append(this.id,
                                          otherObject.id).isEquals();    
    }
    /**
     * the name of the <code>OrderRouter</code>
     */
    private final String name;
    /**
     * the id of the <code>OrderRouter</code>
     */
    private final String id;
    /**
     * Maintains an aggregation of <code>OrderRouter</code> values.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    public static class OrderRouters
    {
        /**
         * Create a new OrderRouters instance.
         *
         * @param inRouters
         */
        public OrderRouters(Set<OrderRouter> inRouters)
        {
            routers = Collections.unmodifiableSet(inRouters);
        }
        /**
         * Get an immutable view of the <code>OrderRouter</code> values.
         *
         * @return a <code>Set&lt;OrderRouter&gt;</code> value
         */
        public Set<OrderRouter> getRouters()
        {
            return routers;
        }
        /**
         * the collection of <code>OrderRouter</code> values contained in this aggregation
         */
        private final Set<OrderRouter> routers;
    }
}
