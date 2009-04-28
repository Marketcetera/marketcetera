package org.marketcetera.ors;

import org.marketcetera.client.ClientVersion;
import org.marketcetera.client.Service;
import org.marketcetera.ors.config.SpringConfig;
import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.util.ws.stateful.Client;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.tags.AppId;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class ORSTestBase
    extends TestCaseBase
{
    private OrderRoutingSystem mORS;
    private Thread mORSThread;
    private Client mORSClient;
    private Service mORSService;

    protected void startORS
        (final String args[])
        throws Exception
    {
        DBInit.initORSDB();
        mORS=new OrderRoutingSystem(args);
        mORSThread=new Thread("testThread") {
            @Override
            public void run() {
                getORS().startWaitingForever();
            }
        };
        mORSThread.start();

        // Wait for initialization to complete.
        while (!getORS().isWaitingForever()) {
            Thread.sleep(1000);
        }
        Thread.sleep(1000);

        mORSClient=new Client
            (SpringConfig.getSingleton().getServerHost(),
             SpringConfig.getSingleton().getServerPort(),
             new AppId("testClient"+
                       ClientVersion.APP_ID_VERSION_SEPARATOR+
                       ClientVersion.APP_ID_VERSION));
        mORSClient.login(mORS.getAuth().getUser(),mORS.getAuth().getPassword());
        mORSService=getORSClient().getService(Service.class);
    }

    protected OrderRoutingSystem getORS()
    {
        return mORS;
    }

    protected Client getORSClient()
    {
        return mORSClient;
    }

    protected ClientContext getORSClientContext()
    {
        return getORSClient().getContext();
    }

    protected Service getORSService()
    {
        return mORSService;
    }

    protected void stopORS()
        throws Exception
    {
        mORSThread.interrupt();

        // Wait for cleaup to complete.
        while (mORSThread.isAlive()) {
            Thread.sleep(1000);
        }
    }
}
