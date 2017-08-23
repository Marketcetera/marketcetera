package org.marketcetera.trade.dao;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Describes the database version.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DatabaseVersion.java 16522 2014-12-31 16:33:08Z colin $
 * @since 2.4.2
 */
@ClassVersion("$Id: DatabaseVersion.java 16522 2014-12-31 16:33:08Z colin $")
public class DatabaseVersion
        implements Serializable
{
    /**
     * Create a new DatabaseVersion instance.
     *
     * @param inDatabaseVersion a <code>PersistentSystemInfo</code> value
     */
    public DatabaseVersion(PersistentSystemInfo inDatabaseVersion)
    {
        version = inDatabaseVersion.getValue();
    }
    /**
     * Get the version value.
     *
     * @return a <code>String</code> value
     */
    public String getVersion()
    {
        return version;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Database Version ").append(version);
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(version).toHashCode();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DatabaseVersion other = (DatabaseVersion) obj;
        return new EqualsBuilder().append(version,other.version).isEquals();
    }
    /**
     * Create a new DatabaseVersion instance.
     */
    private DatabaseVersion()
    {
        version = NO_VERSION_VALUE;
    }
    /**
     * Create a new DatabaseVersion instance.
     *
     * @param inVersion a <code>String</code> value
     */
    private DatabaseVersion(String inVersion)
    {
        version = inVersion;
    }
    /**
     * database version value
     */
    private final String version;
    /**
     * indicates no known version
     */
    private final String NO_VERSION_VALUE = "none";
    /**
     * value used to indicate that the database version cannot be determined
     */
    public static final DatabaseVersion NO_VERSION = new DatabaseVersion();
    /**
     * value used to indicate the current database version
     */
    public static final DatabaseVersion CURRENT_VERSION = new DatabaseVersion(String.valueOf(8));
    private static final long serialVersionUID = 8659422315010966218L;
}
