package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage2P;
import static org.marketcetera.persist.Messages.*;

import java.util.List;
/* $License$ */

/**
 * Provides remoting services for persistent entities.
 * The system will be configured with an appropriate implementation
 * of the remote services suitable for the process.
 *
 * Server-side
 * applications with direct access to the database will be
 * configured with a {@link EntityRemoteServer local} instance
 * that directly sends the requests to the database.
 *
 * Client-side applications will be configured with a remote
 * instance that uses the appropriate protocol to send requests
 * to the server-side and receive responses
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
abstract class EntityRemoteServices {
    /**
     * Sends a request to save the supplied entity.
     *
     * @param entity The entity that needs to be saved
     * @param context the supplied persist context, if any
     *
     * @return An object encapsulating the state changes that
     * were made to the supplied entity when saving it.
     *
     * @throws PersistenceException if there was an error
     * saving the entity
     */
    public abstract SaveResult save(EntityBase entity,
                                    PersistContext context)
            throws PersistenceException;

    /**
     * Sends a request to delete the supplied entity.
     *
     * @param entity The entity that needs to be deleted
     * @param context the supplied persist context, if any
     *
     * @return The save result instance to reset entity's
     * state back to unsaved.
     * 
     * @throws PersistenceException if there was an error deleting
     * the entity
     */
    public abstract SaveResult delete(EntityBase entity,
                                      PersistContext context)
            throws PersistenceException;

    /**
     * Sends the query for execution and returns its results back.
     *
     * @param query The query to execute.
     * @param processors the processors to process query's results
     *
     * @return The results from query's execution.
     *
     * @throws PersistenceException if there's an error executing
     * the query
     */
    public abstract <T> List<QueryResults<T>> execute(
            QueryBase query,
            List<QueryProcessor<T>> processors)
            throws PersistenceException;

    /**
     * Executes the supplied transaction. Invocations to this method
     * can be nested. The transaction is created for the outer-most invocation
     * and is committed or rolled-back only when the outer-most invocation
     * completes.
     *
     * @param t The transaction instance to be executed.
     * @param context the persist context, if any.
     *
     * @return The results from a transaction
     * 
     * @throws PersistenceException if there was an error executing the
     * transaction
     */
    public abstract <R> R execute(Transaction<R> t, PersistContext context)
            throws PersistenceException;
    /**
     * Returns the configured remote entity persistence services
     * instance for the environment.
     *
     * @return The configured remote entity persistence services
     * instance.
     *
     * @throws PersistSetupException if the instance is not
     * initialized already.
     */
    public static EntityRemoteServices getInstance() throws PersistSetupException {
        if(singleton == null) {
            throw new PersistSetupException(ERS_NOT_INITIALIZED);
        }
        return singleton;
    }

    /**
     * Creates an instance. Only one instance of this class
     * can be created. If an attempt is made to create more
     * than one instance, an exception is thrown.
     * 
     * @throws PersistSetupException if an instance of this class
     * already exists.
     */
    protected EntityRemoteServices() throws PersistSetupException {
        super();
        if(singleton != null) {
            throw new PersistSetupException(new I18NBoundMessage2P(
                    ERS_ALREADY_INITIALIZED, getClass().getName(),
                    singleton.getClass().getName()));
        }
        singleton = this;
    }
    private static EntityRemoteServices singleton;
}
