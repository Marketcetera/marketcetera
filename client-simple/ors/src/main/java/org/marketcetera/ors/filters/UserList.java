package org.marketcetera.ors.filters;

import java.util.Set;

import org.marketcetera.util.misc.ClassVersion;

/**
 * Contains allowed/disallowed lists of users.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id: OrderFilter.java 16154 2012-07-14 16:34:05Z colin $")
public class UserList
{
    /**
     * Get the user Whitelist value.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getWhitelist()
    {
        return whitelist;
    }
    /**
     * Sets the user Whitelist value.
     *
     * @param inWhitelist a <code>Set&lt;String&gt;</code> value
     */
    public void setWhitelist(Set<String> inWhitelist)
    {
        whitelist = inWhitelist;
    }
    /**
     * Get the user Blacklist value.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getBlacklist()
    {
        return blacklist;
    }
    /**
     * Sets the user Blacklist value.
     *
     * @param inBlacklist a <code>Set&lt;String&gt;</code> value
     */
    public void setBlacklist(Set<String> inBlacklist)
    {
        blacklist = inBlacklist;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("UserList [whitelist=").append(whitelist).append(", blacklist=") //$NON-NLS-1$ //$NON-NLS-2$
                .append(blacklist).append("]"); //$NON-NLS-1$
        return builder.toString();
    }
    /**
     * whitelist of allowed users, may be <code>null</code>
     */
    private Set<String> whitelist;
    /**
     * blacklist of disallowed users, may be <code>null</code>
     */
    private Set<String> blacklist;
}
