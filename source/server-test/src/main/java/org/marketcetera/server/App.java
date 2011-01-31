package org.marketcetera.server;

import org.marketcetera.server.ws.Services;
import org.marketcetera.api.server.ClientContext;
import org.marketcetera.util.ws.stateless.StatelessClient;
import org.marketcetera.util.ws.wrappers.RemoteException;

public class App 
{
    public static void main(String[] args)
    {
        StatelessClient client = new StatelessClient();
        Services marketceteraServices = client.getService(Services.class);
        try {
            System.out.println("GOT: " + marketceteraServices.getUserData(new ClientContext(){}));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
