package org.marketcetera.ors;

import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

import org.marketcetera.admin.User;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.client.ExtendedReportListener;
import org.marketcetera.client.ReportListener;
import org.marketcetera.client.ReportPublisher;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.ors.dao.UserService;
import org.marketcetera.ors.info.SessionInfo;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.ors.ws.ClientSession;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.FIXResponse;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.tags.SessionId;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

/**
 * A manager of the set of connected users (active sessions). It also
 * routes ORS replies to ORS clients.
 *
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id: UserManager.java 17266 2017-04-28 14:58:00Z colin $
 */

/* $License$ */

@ClassVersion("$Id: UserManager.java 17266 2017-04-28 14:58:00Z colin $")
public class UserManager
        implements ReportPublisher
{

    // INSTANCE DATA.

    private final Map<UserID,Set<ClientSession>> mUserIDMap;
    private final Set<UserID> mRUserIDs;
    private final Set<UserID> mSUserIDs;
    private SessionManager<ClientSession> mSessionManager;
    /**
     * holds those interested in reports
     */
    private final Queue<ReportListener> reportListeners = new ConcurrentLinkedQueue<>();

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

    public void setSessionManager
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
            user = userService.findById(userID.getValue());
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

    public synchronized void addSession(ClientSession session)
    {
        allSessions.add(session);
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
    private final Set<ClientSession> allSessions = Sets.newConcurrentHashSet();
    /**
     * Removes the given session from the receiver.
     *
     * @param session The session.
     */

    public synchronized void removedSession(ClientSession session)
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
     * Returns the session information associated with the given session ID.
     *
     * @param inSessionId The session ID.
     * @return The information. It may be null if the session has expired.
     */
    public SessionInfo getSessionInfo(SessionId inSessionId)
    {
        SessionHolder<ClientSession> holder = getSessionManager().get(inSessionId);
        if(holder==null) {
            return null;
        }
        return holder.getSession().getSessionInfo();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ReportPublisher#addReportListener(org.marketcetera.client.ReportListener)
     */
    @Override
    public void addReportListener(ReportListener inListener)
    {
        reportListeners.add(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ReportPublisher#removeReportListener(org.marketcetera.client.ReportListener)
     */
    @Override
    public void removeReportListener(ReportListener inListener)
    {
        reportListeners.remove(inListener);
    }
    /**
     * Sends the given message to the appropriate sessions managed by
     * the receiver.
     *
     * @param inMessage The message.
     */
    public void convertAndSend(TradeMessage inMessage)
    {
        // TODO should implement permissions here. need to create a subinterface of ReportListener that also includes (Direct)Client username. check the permissions before sending the report.
        // this block will deliver messages directly to in-process listeners, regardless of permissions
        for(ReportListener listener : reportListeners) {
            if(inMessage instanceof ExecutionReport) {
                listener.receiveExecutionReport((ExecutionReport)inMessage);
            } else if(inMessage instanceof OrderCancelReject) {
                listener.receiveCancelReject((OrderCancelReject)inMessage);
            } else if(listener instanceof ExtendedReportListener && inMessage instanceof FIXResponse){
                ((ExtendedReportListener)listener).receiveFixResponse((FIXResponse)inMessage);
            } else {
                // TODO message
                SLF4JLoggerProxy.warn(this,
                                      "Unable to deliver {} to {}",
                                      inMessage,
                                      listener);
            }
        }
        // this block will deliver messages via JMS to out-of-process listeners, permission checks are appropriate here
        User actor = null;
        UserID viewerId = null;
        Set<User> supervisors = Sets.newHashSet();
        if(inMessage instanceof ReportBase) {
            viewerId = ((ReportBase)inMessage).getViewerID();
            try {
                actor = userService.findByUserId(viewerId);
                supervisors.addAll(authzService.getSupervisorsFor(actor.getName(),
                                                                  TradingPermissions.ViewReportAction.name()));
            } catch (Exception e) {
                PlatformServices.handleException(UserManager.this,
                                                           "Cannot find supervisors for " + inMessage,
                                                           e);
            }
        }
        for(ClientSession session : allSessions) {
            User viewer = session.getUser();
            if(actor == null) {
                // this is not a ReportBase (not an execution report or an order cancel reject) - send if the viewer has the appropriate permission
                if(authzService.authorizeNoException(viewer.getName(),
                                                     TradingPermissions.ViewReportAction.name())) {
                    session.getReplyTopic().convertAndSend(inMessage);
                }
            } else {
                // this is a ReportBase (meaning an er or cxr reject), meaning we know both the actor (owner of the message) and viewer (owner of the session)
                if(actor.equals(viewer)) {
                    // the owner of the message and the owner of the session are the same, send the report if the actor/viewer has the appropriate permission
                    if(authzService.authorizeNoException(viewer.getName(),
                                                         TradingPermissions.ViewReportAction.name())) {
                        session.getReplyTopic().convertAndSend(inMessage);
                    }
                } else {
                    // actor (owner of the message) is not the same as the viewer (owner of the session). send the report only if the viewer has supervisory permission over the actor
                    if(supervisors.contains(viewer)) {
                        session.getReplyTopic().convertAndSend(inMessage);
                    }
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
    /**
     * Get the userService value.
     *
     * @return a <code>UserService</code> value
     */
    public UserService getUserService()
    {
        return userService;
    }
    /**
     * Sets the userService value.
     *
     * @param inUserService a <code>UserService</code> value
     */
    public void setUserService(UserService inUserService)
    {
        userService = inUserService;
    }
    /**
     * provides access to user services
     */
    @Autowired
    private UserService userService;
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authzService;
}
