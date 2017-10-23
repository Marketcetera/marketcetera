package org.marketcetera.admin.rpc;

import org.marketcetera.admin.AdminRpcClient;
import org.marketcetera.admin.AdminRpcClientFactory;
import org.marketcetera.admin.AdminRpcClientParameters;
import org.marketcetera.admin.impl.SimplePermissionFactory;
import org.marketcetera.admin.impl.SimpleRoleFactory;
import org.marketcetera.admin.impl.SimpleUserAttributeFactory;
import org.marketcetera.admin.impl.SimpleUserFactory;
import org.marketcetera.rpc.RpcTestBase;
import org.marketcetera.rpc.client.RpcClientFactory;
import org.marketcetera.util.ws.tags.SessionId;

import com.marketcetera.admin.AdminRpcServiceGrpc;

/* $License$ */

/**
 * Tests {@link AdminRpcService}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AdminRpcServiceTest
        extends RpcTestBase<AdminRpcClientParameters,AdminRpcClient,SessionId,AdminRpcServiceGrpc.AdminRpcServiceImplBase,AdminRpcService<SessionId>>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getRpcClientFactory()
     */
    @Override
    protected RpcClientFactory<AdminRpcClientParameters,AdminRpcClient> getRpcClientFactory()
    {
        return new AdminRpcClientFactory();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getClientParameters(java.lang.String, int, java.lang.String, java.lang.String)
     */
    @Override
    protected AdminRpcClientParameters getClientParameters(String inHostname,
                                                           int inPort,
                                                           String inUsername,
                                                           String inPassword)
    {
        AdminRpcClientParameters parameters = new AdminRpcClientParameters();
        parameters.setHeartbeatInterval(1000);
        parameters.setHostname(inHostname);
        parameters.setPassword(inPassword);
        parameters.setPort(inPort);
        parameters.setUsername(inUsername);
        return parameters;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#prepareClient(org.marketcetera.rpc.client.RpcClient)
     */
    @Override
    protected void prepareClient(AdminRpcClient inClient)
    {
        inClient.setPermissionFactory(new SimplePermissionFactory());
        inClient.setRoleFactory(new SimpleRoleFactory());
        inClient.setUserAttributeFactory(new SimpleUserAttributeFactory());
        inClient.setUserFactory(new SimpleUserFactory());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#createTestService()
     */
    @Override
    protected AdminRpcService<SessionId> createTestService()
    {
        return new AdminRpcService<SessionId>();
    }
}
