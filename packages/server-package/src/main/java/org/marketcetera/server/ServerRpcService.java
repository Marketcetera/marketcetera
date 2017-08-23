package org.marketcetera.server;

import java.util.List;

import org.marketcetera.server.session.ServerSession;
import org.marketcetera.util.rpc.RpcServer;
import org.marketcetera.util.rpc.RpcServiceSpec;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Provides the server RPC service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
@ConfigurationProperties(prefix="rpc")
public class ServerRpcService
        extends RpcServer<ServerSession>
{
    /**
     * Create a new ServerRpcService instance.
     *
     * @param inSessionManager a <code>SessionManager&lt;ServerSession&gt;</code> value
     * @param inAuthenticator an <code>Authenticator</code> value
     * @param inServiceSpecs a <code>List&lt;RpcServiceSpec&lt;ServerSessin&gt;&gt;</code> value
     */
    @Autowired
    public ServerRpcService(SessionManager<ServerSession> inSessionManager,
                            Authenticator inAuthenticator,
                            List<RpcServiceSpec<ServerSession>> inServiceSpecs)
    {
        super();
        setSessionManager(inSessionManager);
        setAuthenticator(inAuthenticator);
        setServiceSpecs(inServiceSpecs);
    }
}
