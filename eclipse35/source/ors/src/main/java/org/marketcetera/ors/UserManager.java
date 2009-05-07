package org.marketcetera.ors;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.ors.security.SingleSimpleUserQuery;
import org.marketcetera.ors.ws.ClientSession;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.tags.SessionId;

/**
 * A manager of the set of connected users (active sessions). It also
 * routes ORS replies to ORS clients.
 *
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class UserManager
{

    // INSTANCE DATA.

    private final Map<UserID,Set<ClientSession>> mUserIDMap;
    private final Set<UserID> mRUserIDs;
    private final Set<UserID> mSUserIDs;
    private SessionManager<ClientSession> mSessionManager;


    // CONSTRUCTORS.

    /**
     * Creates a new user manager.
     */

    public UserManager()
    {
        mUserIDMap=new ConcurrentHashMap<UserID,Set<ClientSession>>();
        mRUserIDs=new CopyOnWriteArraySet<UserID>();
        mSUserIDs=new CopyOnWriteArraySet<UserID>();
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's map of user IDs to (one or more)
     * sessions. Individual sessions are removed when they become
     * inactive; map entries remain in this map for as long as at
     * least one session is active for a user.
     *
     * @return The map.
     */

    private Map<UserID,Set<ClientSession>> getUserIDMap()
    {
        return mUserIDMap;
    }

    /**
     * Returns the receiver's set of non-superuser user IDs. A user
     * belongs in this set for as long as they are a non-superuser
     * with one or more active sessions.
     *
     * @return The set.
     */

    private Set<UserID> getRUserIDs()
    {
        return mRUserIDs;
    }

    /**
     * Returns the receiver's set of superuser user IDs. A user
     * belongs in this set for as long as they are a superuser with
     * one or more active sessions.
     *
     * @return The set.
     */

    private Set<UserID> getSUserIDs()
    {
        return mSUserIDs;
    }

    /**
     * Sets the receiver's session manager to the given one.
     *
     * @param sessionManager The manager.
     */

    void setSessionManager
        (SessionManager<ClientSession> sessionManager)
    {
        mSessionManager=sessionManager;
    }

    /**
     * Returns the receiver's session manager.
     *
     * @return The manager.
     */

    public SessionManager<ClientSession> getSessionManager()
    {
        return mSessionManager;
    }

    /**
     * Updates the receiver's data structures to reflect the current
     * user definitions in the database.
     */

    public synchronized void sync()
    {
        Set<UserID> allUserIDs=new HashSet<UserID>();
        allUserIDs.addAll(getSUserIDs());
        allUserIDs.addAll(getRUserIDs());
        for (UserID userID:allUserIDs) {
            // Assume user is nonexistent/inactive.
            SimpleUser user=null;
            try {
                user=new SingleSimpleUserQuery(userID.getValue()).fetch();
            } catch (PersistenceException ex) {
                // Ignored: user remains null.
            }
            if ((user!=null) && (!user.isActive())) {
                user=null;
            }
            getSUserIDs().remove(userID);
            getRUserIDs().remove(userID);

            // User is active: add back to the correct set.

            if (user!=null) {
                if (user.isSuperuser()) {
                    getSUserIDs().add(userID);
                } else {
                    getRUserIDs().add(userID);
                }
                continue;
            }

            // User is nonexistent/inactive: remove all their sessions.

            Set<ClientSession> sessions=getUserIDMap().get(userID);
            for (ClientSession s:sessions) {
                // This will generate a call to
                // removedSession((ClientSession).
                getSessionManager().remove(s.getSessionId());
            }
        }
        logStatus();
    }

    /**
     * Adds the given session to the receiver.
     *
     * @param session The session.
     */

    public synchronized void addSession
        (ClientSession session)
    {
        SimpleUser user=session.getUser();
        UserID userID=user.getUserID();
        Set<ClientSession> sessions=getUserIDMap().get(userID);
        if (sessions==null) {
            sessions=new CopyOnWriteArraySet<ClientSession>();
            getUserIDMap().put(userID,sessions);
        }
        sessions.add(session);
        if (user.isSuperuser()) {
            // Remove from non-superusers, in case user record changed.
            getRUserIDs().remove(userID);
            // Add to superusers.
            getSUserIDs().add(userID);
        } else {
            // Add to non-superusers.
            getRUserIDs().add(userID);
            // Remove from superusers, in case user record changed.
            getSUserIDs().remove(userID);
        }
        logStatus();
    }

    /**
     * Removes the given session from the receiver.
     *
     * @param session The session.
     */

    public synchronized void removedSession
        (ClientSession session)
    {
        UserID userID=session.getUser().getUserID();
        Set<ClientSession> sessions=getUserIDMap().get(userID);
        sessions.remove(session);
        if (sessions.size()==0) {
            getUserIDMap().remove(userID);
            getSUserIDs().remove(userID);
            getRUserIDs().remove(userID);
        }
        logStatus();
    }      

    /**
     * Returns the ID of the user associated with the given session ID.
     *
     * @param sessionId The session ID.
     *
     * @return The user ID. It may be null if the session has expired.
     */

    public UserID getSessionUserID
        (SessionId sessionId)
    {
        SessionHolder<ClientSession> holder=getSessionManager().get(sessionId);
        if (holder==null) {
            return null;
        }
        return holder.getSession().getUser().getUserID();
    }

    /**
     * Sends the given message to the appropriate sessions managed by
     * the receiver.
     *
     * @param msg The message.
     */

    public void convertAndSend
        (TradeMessage msg)
    {
        
        // Sessions for non-superuser viewer (if the viewer is a
        // superuser, they get the message via the next section
        // below).

        UserID viewerID=null;
        if (msg instanceof ReportBase) {
            viewerID=((ReportBase)msg).getViewerID();
        }
        if ((viewerID!=null) && getRUserIDs().contains(viewerID)) {
            Set<ClientSession> sessions=getUserIDMap().get(viewerID);
            if (sessions!=null) {
                for (ClientSession s:sessions) {
                    s.getReplyTopic().convertAndSend(msg);
                }
            }
        }

        // Sessions for all superuser viewers.

        for (UserID userID:getSUserIDs()) {
            Set<ClientSession> sessions=getUserIDMap().get(userID);
            if (sessions!=null) {
                for (ClientSession s:sessions) {
                    s.getReplyTopic().convertAndSend(msg);
                }
            }
        }
    }

    /**
     * Logs the receiver's status.
     */

    public void logStatus()
    {
        SLF4JLoggerProxy.debug(this,"User ID map"); //$NON-NLS-1$
        for (Map.Entry<UserID,Set<ClientSession>> e:
             getUserIDMap().entrySet()) {
            SLF4JLoggerProxy.debug
                (this," User ID: {}",e.getKey()); //$NON-NLS-1$
            for (ClientSession s:e.getValue()) {
                SLF4JLoggerProxy.debug(this,"  {}",s); //$NON-NLS-1$
            }
        }
        SLF4JLoggerProxy.debug(this,"Non-superusers"); //$NON-NLS-1$
        for (UserID e:getRUserIDs()) {
            SLF4JLoggerProxy.debug(this," {}",e); //$NON-NLS-1$
        }
        SLF4JLoggerProxy.debug(this,"Superusers"); //$NON-NLS-1$
        for (UserID e:getSUserIDs()) {
            SLF4JLoggerProxy.debug(this," {}",e); //$NON-NLS-1$
        }
    }
}
