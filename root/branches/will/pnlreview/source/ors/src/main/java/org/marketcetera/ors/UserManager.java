package org.marketcetera.ors;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.marketcetera.ors.ws.ClientSession;
import org.marketcetera.quickfix.IQuickFIXSender;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.SessionId;

/**
 * A sender of replies to clients.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class UserManager
{

    // INSTANCE DATA.

    private final Map<SessionId,ClientSession> mSessionIDMap;
    private final Set<ClientSession> mSuperSessions;
    private final Map<UserID,Set<ClientSession>> mNSUserIDMap;


    // CONSTRUCTORS.

    public UserManager()
    {
        mSessionIDMap=new ConcurrentHashMap<SessionId,ClientSession>();
        mSuperSessions=new CopyOnWriteArraySet<ClientSession>();
        mNSUserIDMap=new ConcurrentHashMap<UserID,Set<ClientSession>>();
    }


    // INSTANCE METHODS.

    private Map<SessionId,ClientSession> getSessionIDMap()
    {
        return mSessionIDMap;
    }

    private Set<ClientSession> getSuperSessions()
    {
        return mSuperSessions;
    }

    private Map<UserID,Set<ClientSession>> getNSUserIDMap()
    {
        return mNSUserIDMap;
    }

    public synchronized void addSession
        (ClientSession session)
    {
        getSessionIDMap().put(session.getSessionId(),session);
        if (session.getUser().isSuperuser()) {
            getSuperSessions().add(session);
        } else {
            UserID id=session.getUser().getUserID();
            Set<ClientSession> sessions=getNSUserIDMap().get(id);
            if (sessions==null) {
                sessions=new CopyOnWriteArraySet<ClientSession>();
                getNSUserIDMap().put(id,sessions);
            }
            sessions.add(session);
        }
    }      

    public synchronized void removedSession
        (ClientSession session)
    {
        getSessionIDMap().remove(session.getSessionId());
        getSuperSessions().remove(session);
        UserID id=session.getUser().getUserID();
        Set<ClientSession> sessions=getNSUserIDMap().get(id);
        if (sessions==null) {
            return;
        }
        sessions.remove(sessions);
        if (sessions.size()==0) {
            getNSUserIDMap().remove(id);
        }
    }      

    public UserID getActorID
        (SessionId id)
    {
        return getSessionIDMap().get(id).getUser().getUserID();
    }

    public void convertAndSend
        (TradeMessage message,
         UserID viewer)
    {
        if (viewer!=null) {
            Set<ClientSession> sessions=getNSUserIDMap().get(viewer);
            if (sessions!=null) {
                for (ClientSession s:sessions) {
                    s.getReplyTopic().convertAndSend(message);
                }
            }
        }
        for (ClientSession s:getSuperSessions()) {
            s.getReplyTopic().convertAndSend(message);
        }
    }
}
