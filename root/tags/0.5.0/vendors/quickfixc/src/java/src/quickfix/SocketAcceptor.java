/****************************************************************************
** Copyright (c) quickfixengine.org  All rights reserved.
**
** This file is part of the QuickFIX FIX Engine
**
** This file may be distributed under the terms of the quickfixengine.org
** license as defined by quickfixengine.org and appearing in the file
** LICENSE included in the packaging of this file.
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
** See http://www.quickfixengine.org/LICENSE for licensing information.
**
** Contact ask@quickfixengine.org if any conditions of this licensing are
** not clear to you.
**
****************************************************************************/

package quickfix;
import java.io.InputStream;
import java.util.ArrayList;

public class SocketAcceptor implements Acceptor {
    private long cppPointer;

    private Application application;
    private SessionSettings settings;
    private MessageStoreFactory messageStoreFactory;
    private MessageFactory messageFactory;
    private LogFactory logFactory = null;

    public SocketAcceptor(Application application,
                          MessageStoreFactory messageStoreFactory,
                          SessionSettings settings,
                          MessageFactory messageFactory) throws ConfigError {
        this.application = application;
        this.settings = settings;
        this.messageStoreFactory = messageStoreFactory;
        this.messageFactory = messageFactory;
        create();
    }

    public SocketAcceptor(Application application,
                          MessageStoreFactory messageStoreFactory,
                          SessionSettings settings,
                          LogFactory logFactory,
                          MessageFactory messageFactory) throws ConfigError {
        this.application = application;
        this.settings = settings;
        this.messageStoreFactory = messageStoreFactory;
        this.logFactory = logFactory;
        this.messageFactory = messageFactory;
        create();
    }

    protected void finalize() {
        destroy();
    }

    private native void create();
    private native void destroy();

    public void start() throws RuntimeError, ConfigError {
        doStart();
    }

    public void block() throws RuntimeError, ConfigError {
        doBlock();
    }

    public void stop() {
        doStop();
    }

    public void stop( boolean force ) {
        doStop( force );
    }

    public boolean isLoggedOn() {
        return doIsLoggedOn();
    }

	public ArrayList getSessions() {
        return doGetSessions();
    }

    private native void doStart() throws RuntimeError, ConfigError;
    private native void doBlock() throws RuntimeError, ConfigError;
    private native void doStop();
    private native void doStop( boolean force );
    private native boolean doIsLoggedOn();
    private native ArrayList doGetSessions();
}
