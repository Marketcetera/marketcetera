package org.marketcetera.quickfix;

import java.util.HashMap;
import java.util.Map;

import quickfix.Log;
import quickfix.SLF4JLogFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Provides a Quickfix/J log factory implementation that allows observers of log activities.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ObservableLogFactory
        extends SLF4JLogFactory
{
    /**
     * Create a new ObservableLogFactory instance.
     *
     * @param inSettings a <code>SessionSettings</code> value
     */
    public ObservableLogFactory(SessionSettings inSettings)
    {
        super(inSettings);
    }
    /* (non-Javadoc)
     * @see quickfix.SLF4JLogFactory#create()
     */
    @Override
    public ObservableLog create()
    {
        unqualifiedLog = new ObservableLog(super.create());
        return unqualifiedLog;
    }
    /* (non-Javadoc)
     * @see quickfix.SLF4JLogFactory#create(quickfix.SessionID, java.lang.String)
     */
    @Override
    public Log create(SessionID inSessionId,
                      String inCallerName)
    {
        ObservableLog log = new ObservableLog(super.create(inSessionId,
                                                           inCallerName));
        logsByFullyQualifiedSessionId.put(getKey(inSessionId,
                                                 inCallerName),
                                          log);
        return log;
    }
    /* (non-Javadoc)
     * @see quickfix.SLF4JLogFactory#create(quickfix.SessionID)
     */
    @Override
    public Log create(SessionID inSessionId)
    {
        ObservableLog log = new ObservableLog(super.create(inSessionId));
        logsBySessionId.put(inSessionId,
                            log);
        return log;
    }
    /**
     * Gets the unqualified log object.
     *
     * @return an <code>ObservableLog</code> value
     */
    public ObservableLog getLog()
    {
        return unqualifiedLog;
    }
    /**
     * Gets the log object for the given session and fully-qualified caller.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inCallerName a <code>String</code> value
     * @return an <code>ObservableLog</code> value
     */
    public ObservableLog getLog(SessionID inSessionId,
                                String inCallerName)
    {
        return logsByFullyQualifiedSessionId.get(getKey(inSessionId,
                                                        inCallerName));
    }
    /**
     * Gets the log object for the given session.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return an <code>ObservableLog</code> value
     */
    public ObservableLog getLog(SessionID inSessionId)
    {
        return logsBySessionId.get(inSessionId);
    }
    /**
     * Gets the key for the given session and caller.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inCallerName a <code>String</code> value
     * @return a <code>String</code> value
     */
    private String getKey(SessionID inSessionId,
                          String inCallerName)
    {
        return new StringBuilder().append(inSessionId).append('-').append(inCallerName).toString();
    }
    /**
     * the unqualified log instance, if any
     */
    private ObservableLog unqualifiedLog = null;
    /**
     * logs by session id
     */
    private final Map<SessionID,ObservableLog> logsBySessionId = new HashMap<>();
    /**
     * logs by full qualification
     */
    private final Map<String,ObservableLog> logsByFullyQualifiedSessionId = new HashMap<>();
}
