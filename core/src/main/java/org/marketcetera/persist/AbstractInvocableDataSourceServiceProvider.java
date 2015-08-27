package org.marketcetera.persist;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.commons.lang.Validate;

/* $License$ */

/**
 * Provides common behavior for an invocable data source service provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AbstractInvocableDataSourceServiceProvider.java 84382 2015-01-20 19:43:06Z colin $
 * @since $Release$
 */
public abstract class AbstractInvocableDataSourceServiceProvider
        extends AbstractDataSourceServiceProvider
{
    /**
     * Get the invocation value.
     *
     * @return a <code>String</code> value
     */
    public String getInvocation()
    {
        return invocation;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.irouter.service.impl.AbstractDataSourceServiceProvider#start()
     */
    @Override
    public void start()
    {
        super.start();
        Validate.notNull(invocation,
                         "Invocation is required");
    }
    /**
     * Sets the invocation value.
     *
     * @param inInvocation a <code>String</code> value
     */
    public void setInvocation(String inInvocation)
    {
        invocation = inInvocation;
    }
    /**
     * Executes the given block with the appropriate resources provided and cleaned up.
     *
     * @param inBlock a <code>CollectionCallable&lt;ReturnClazz&gt;</code> value
     * @return a <code>ReturnClazz</code> value
     * @throws Exception if an error occurs executing the call
     */
    protected <ReturnClazz> ReturnClazz execute(ConnectionCallable<ReturnClazz> inBlock)
            throws Exception
    {
        try(Connection connection = getDataSource().getConnection()) {
            try(CallableStatement statement = connection.prepareCall(invocation)) {
                return inBlock.call(statement);
            }
        }
    }
    /**
     * Executes the given block with the appropriate resources provided and cleaned up.
     *
     * @param inBlock a <code>PreparedCollectionCallable&lt;ReturnClazz&gt;</code> value
     * @return a <code>ReturnClazz</code> value
     * @throws Exception if an error occurs executing the call
     */
    protected <ReturnClazz> ReturnClazz executePrepared(PreparedConnectionCallable<ReturnClazz> inBlock)
            throws Exception
    {
        try(Connection connection = getDataSource().getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(invocation)) {
                return inBlock.call(statement);
            }
        }
    }
    /**
     * Describes a prepared statement database call to be made with appropriate resources.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    protected interface PreparedConnectionCallable<ReturnClazz>
    {
        ReturnClazz call(PreparedStatement inStatement)
                throws Exception;
    }
    /**
     * Describes a database call to be made with appropriate resources.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: AbstractInvocableDataSourceServiceProvider.java 84382 2015-01-20 19:43:06Z colin $
     * @since $Release$
     */
    protected interface ConnectionCallable<ReturnClazz>
    {
        /**
         * Executes a database call that needs a <code>CallableStatement</code> value
         *
         * @param inStatement a <code>CallableStatement</code> value
         * @return a <code>ReturnClazz</code> value
         * @throws Exception if an error occurs executing the call
         */
        ReturnClazz call(CallableStatement inStatement)
                throws Exception;
    }
    /**
     * invocation for the stored procedure
     */
    private String invocation;
}
