package org.marketcetera.dao.domain;

import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.*;

import org.marketcetera.api.dao.MutableSystemInformation;
import org.marketcetera.api.dao.SystemInformation;

/* $License$ */

/**
 * Persistent implementation of a <code>SystemInformation</code> value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@Entity
@NamedQueries( { @NamedQuery(name="PersistentSystemInformation.findAll",query="select s from PersistentSystemInformation s")})
@Table(name="system_information")
@Access(AccessType.FIELD)
public class PersistentSystemInformation
        extends PersistentVersionedObject
        implements MutableSystemInformation
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.SystemInformation#getDatabaseSchemaVersion()
     */
    @Override
    public String getDatabaseSchemaVersion()
    {
        return databaseSchemaVersion;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.SystemInformation#getMutableView()
     */
    @Override
    public MutableSystemInformation getMutableView()
    {
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutableSystemInformation#setDatabaseSchemaVersion(java.lang.String)
     */
    @Override
    public void setDatabaseSchemaVersion(String inValue)
    {
        databaseSchemaVersion = inValue;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PersistentSystemInformation [databaseSchemaVersion=").append(databaseSchemaVersion).append("]");
        return builder.toString();
    }
    /**
     * Create a new PersistentSystemInformation instance.
     */
    PersistentSystemInformation() {}
    /**
     * Create a new PersistentSystemInformation instance.
     *
     * @param inData a <code>SystemInformation</code> value
     */
    PersistentSystemInformation(SystemInformation inData)
    {
        setDatabaseSchemaVersion(inData.getDatabaseSchemaVersion());
    }
    /**
     * database schema version value
     */
    @Column(name="database_schema_version",nullable=false)
    private String databaseSchemaVersion;
}
