package org.marketcetera.util.ws.stateful;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.NodeId;
import org.marketcetera.util.ws.tags.SessionId;

/**
 * A session manager. It maps session IDs ({@link SessionId}) to
 * session holders ({@link SessionHolder}), and (optionally)
 * automatically expires sessions (map entries) which remain unused
 * for a time interval that exceeds the session lifespan. This
 * expiration is performed at regular intervals by a reaper thread,
 * initiated during construction as part of the caller's thread group;
 * the interval between scans is normally 5% of the session lifespan.
 * 
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class SessionManager<T>
{

    // CLASS DATA.

    /**
     * The maximum sleep interval between reaper scans, in ms.
     */

    public static final long MAX_REAPER_SLEEP=
        60*1000;

    /**
     * A sentinel value for an infinite session lifespan (non-expiring
     * sessions).
     */

    public static final long INFINITE_SESSION_LIFESPAN=
        -1;


    // INSTANCE DATA.

    private NodeId mServerId;
    private final long mSessionLife;
    private final HashMap<SessionId,SessionHolder<T>> mMap=
        new HashMap<SessionId,SessionHolder<T>>();

    /**
     * The reaper.
     */

    class Reaper
        extends Thread
    {

        // INSTANCE DATA.

        private long mReaperSleep;


        // CONSTRUCTORS.

        /**
         * Creates a new reaper.
         */

        public Reaper()
        {
            super(Thread.currentThread().getThreadGroup(),
                  Messages.REAPER_THREAD_NAME.getText());
            mReaperSleep=Math.min
                (MAX_REAPER_SLEEP,(long)(getLifespan()*0.05));
        }

        /**
         * Returns the time interval between scans, in ms.
         *
         * @return The interval.
         */

        private long getReaperSleep()
        {
            return mReaperSleep;
        }


        // Runnable.

        @Override
        public void run()
        {
            while (true) {
                long cutoff=System.currentTimeMillis()-getLifespan();
                synchronized (getMap()) {
                    for (Iterator<Map.Entry<SessionId,SessionHolder<T>>> i=
                             getMap().entrySet().iterator();i.hasNext();) {
                        Map.Entry<SessionId,SessionHolder<T>> entry=i.next();
                        if (entry.getValue().getLastAccess()<=cutoff) {
                            Messages.REAPER_EXPIRED_SESSION.info
                                (this,entry.getKey(),
                                 entry.getValue().getCreationContext());
                            i.remove();
                        }
                    }
                }
                try {
                    Thread.sleep(getReaperSleep());
                } catch (InterruptedException ex) {
                    Messages.REAPER_TERMINATED.info(this,ex,getServerId());
                    return;
                }
            }
        }
    }


    // CONSTRUCTORS.

    /**
     * Creates a new session manager whose sessions have the given
     * lifespan, in ms.
     *
     * @param sessionLife The lifespan. Use {@link
     * #INFINITE_SESSION_LIFESPAN} for an infinite lifespan.
     */

    public SessionManager
        (long sessionLife)
    {
        mSessionLife=sessionLife;
        if (getLifespan()!=INFINITE_SESSION_LIFESPAN) {
            (new Reaper()).start();
        }
    }

    /**
     * Creates a new session manager whose sessions never expire.
     */

    public SessionManager()
    {
        this(INFINITE_SESSION_LIFESPAN);
    }


    // INSTANCE METHODS.

    /**
     * Sets the receiver's server ID to the given one.
     *
     * @param serverId The server ID, which may be null.
     */

    void setServerId
        (NodeId serverId)
    {
        mServerId=serverId;
    }

    /**
     * Returns the receiver's server ID.
     *
     * @return The server ID, which may be null.
     */

    public NodeId getServerId()
    {
        return mServerId;
    }

    /**
     * Returns the lifespan of the sessions managed by the receiver.
     *
     * @return The lifespan.
     */

    public long getLifespan()
    {
        return mSessionLife;
    }   

    /**
     * Returns the receiver's map.
     *
     * @return The map.
     */

    private HashMap<SessionId,SessionHolder<T>> getMap()
    {
        return mMap;
    }

    /**
     * Adds the given holder, associated with the given session ID to
     * the receiver. This addition counts as an access that renews the
     * session's expiration counter.
     *
     * @param id The session ID.
     * @param holder The holder.
     */

    public void put
        (SessionId id,
         SessionHolder<T> holder)
    {
        synchronized (getMap()) {
            holder.markAccess();
            getMap().put(id,holder);
        }
    }

    /**
     * Returns the holder that the receiver associates with the given
     * session ID. This access renews the session's expiration
     * counter.
     *
     * @param id The session ID.
     *
     * @return The holder. It is null if there is no holder for the
     * given ID.
     */

    public SessionHolder<T> get
        (SessionId id)
    {
        synchronized (getMap()) {
            SessionHolder<T> holder=getMap().get(id);
            if (holder==null) {
                return null;
            }
            holder.markAccess();
            return holder;
        }
    }

    /**
     * Removes the holder that the receiver associates with the given
     * session ID. This method is a no-op if no such association
     * exists.
     *
     * @param id The session ID.
     */

    public void remove
        (SessionId id)
    {
        synchronized (getMap()) {
            getMap().remove(id);
        }
    }
}
