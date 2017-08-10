package org.marketcetera.trade.dao;

import java.sql.SQLException;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ExternalIDFactory;
import org.marketcetera.core.NoMoreIDsException;

/**
 * @author toli
 * @version $Id: DBBackedIDFactory.java 16468 2014-05-12 00:36:56Z colin $
 */

@ClassVersion("$Id: DBBackedIDFactory.java 16468 2014-05-12 00:36:56Z colin $")
public abstract class DBBackedIDFactory
        extends ExternalIDFactory
{
    protected DBBackedIDFactory(String prefix) {
        super(prefix);
    }

    /** Lock the table to prevent concurrent access with {@link java.sql.ResultSet#CONCUR_UPDATABLE} */
    public void grabIDs() throws NoMoreIDsException {

        factoryValidityCheck();

        try {
            performIDRequest();
        } catch (SQLException e) {
            throw new NoMoreIDsException(e);
	    } catch (Throwable t){
	    	throw new NoMoreIDsException(t);
		}
    }

    public void init() throws ClassNotFoundException, NoMoreIDsException {
        // no-op
    }

    /** Intended to be overwritten by subclasses
     * Extra validity check before performing the request.
     * Checks the factory state, if inconsistent, throws an exception
     * @throws org.marketcetera.core.NoMoreIDsException
     */
    protected void factoryValidityCheck() throws NoMoreIDsException
    {
        // do nothing
    }

    /** Peforms the necessary cleanup after the request is done, whether or not it succeeds or fails. */
    protected void postRequestCleanup()
    {
        // do nothing
    }

    /** Helper function intended to be overwritten by subclasses.
     * Thsi is where the real requiest for IDs happens
     */
    protected abstract void performIDRequest() throws Exception;

}
