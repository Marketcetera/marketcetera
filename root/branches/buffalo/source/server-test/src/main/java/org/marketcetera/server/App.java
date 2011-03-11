package org.marketcetera.server;

import java.util.List;

import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.server.ws.MatpService;
import org.marketcetera.util.ws.stateful.Client;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.wrappers.RemoteException;

public class App 
{
    public static void main(String[] args)
    {
        LoggerConfiguration.logSetup();
        Client client = new Client();
        client.setHost("localhost");
        client.setPort(9000);
        try {
            client.login("user",
                         "user".toCharArray());
            MatpService matpService = client.getService(MatpService.class);
            ClientContext context = client.getContext();
            List<ModuleURN> providers = matpService.getProviders(context);
            System.out.println(providers);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                client.logout();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
//        MatpService matpService = client.getService(MatpService.class);
//        org.apache.cxf.endpoint.Client cxfClient = org.apache.cxf.frontend.ClientProxy.getClient(matpService);
//        org.apache.cxf.endpoint.Endpoint cxfEndpoint = cxfClient.getEndpoint();
////        Map<String,Object> inProps = new HashMap<String,Object>();
////        inProps.put(WSHandlerConstants.ACTION,
////                    WSHandlerConstants.NO_SECURITY);
////        WSS4JInInterceptor wssIn = new WSS4JInInterceptor(inProps);
////        cxfEndpoint.getInInterceptors().add(wssIn);
////        Map<String,Object> outProps = new HashMap<String,Object>();
////        WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
////        outProps.put(WSHandlerConstants.ACTION,
////                     WSHandlerConstants.NO_SECURITY);
//////        outProps.put(WSHandlerConstants.ACTION,
//////                     WSHandlerConstants.USERNAME_TOKEN);
////        // Specify our username
//////        outProps.put(WSHandlerConstants.USER,
//////                     "joe");
//////        // Password type : plain text
//////        outProps.put(WSHandlerConstants.PASSWORD_TYPE,
//////                     WSConstants.PW_TEXT);
////        // for hashed password use:
////        //properties.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
////        // Callback used to retrieve password for given user.
//////        outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS,
//////                     ClientPasswordHandler.class.getName());
////        cxfEndpoint.getOutInterceptors().add(wssOut);
//        try {
//            System.out.println(matpService.getProviders(null));
//        } catch (RemoteException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        Services marketceteraServices = client.getService(Services.class);
//        try {
//            System.out.println("GOT: " + marketceteraServices.getBrokersStatus(new ClientContext(){}));
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }
//    public static class ClientPasswordHandler
//            implements CallbackHandler
//    {
//        /* (non-Javadoc)
//         * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
//         */
//        @Override
//        public void handle(Callback[] inCallbacks)
//                throws IOException, UnsupportedCallbackException
//        {
//            WSPasswordCallback pc = (WSPasswordCallback)inCallbacks[0];
//            // set the password for our message.
//            pc.setPassword("password");
//        }
//    }
}
