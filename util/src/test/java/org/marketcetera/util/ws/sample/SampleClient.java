package org.marketcetera.util.ws.sample;

import java.io.File;
import org.apache.log4j.PropertyConfigurator;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.util.ws.stateful.Client;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.wrappers.MarshalledLocale;

/**
 * A sample client. It calls into a single service. The call is
 * configured via command-line arguments.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class SampleClient
    extends TestCaseBase
{

    // The ID of the client application.

    private static final AppId APP_ID=new AppId("SCApp");


    // MAIN PROGRAM.

    public static void main(String[] args)
        throws Exception
    {

        // Configure log4j.

        PropertyConfigurator.configure
            (DIR_ROOT+File.separator+"ws"+File.separator+"sample"+
             File.separator+"log_client.properties");

        // Parse command-line.

        String user=null;
        char[] password=null;
        MarshalledLocale locale=new MarshalledLocale(null);
        String argument=null;
        for (int i=0;i<args.length;i++) {
            if ("-language".equals(args[i])) {
                if ((++i)==args.length) {
                    throw new IllegalArgumentException("Missing language");
                }
                locale.setLanguage(args[i]);
                continue;
            }
            if ("-country".equals(args[i])) {
                if ((++i)==args.length) {
                    throw new IllegalArgumentException("Missing country");
                }
                locale.setCountry(args[i]);
                continue;
            }
            if ("-variant".equals(args[i])) {
                if ((++i)==args.length) {
                    throw new IllegalArgumentException("Missing variant");
                }
                locale.setVariant(args[i]);
                continue;
            }
            if ("-user".equals(args[i])) {
                if ((++i)==args.length) {
                    throw new IllegalArgumentException("Missing user");
                }
                user=args[i];
                continue;
            }
            if ("-password".equals(args[i])) {
                if ((++i)==args.length) {
                    throw new IllegalArgumentException("Missing password");
                }
                password=args[i].toCharArray();
                continue;
            }
            if ("-argument".equals(args[i])) {
                if ((++i)==args.length) {
                    throw new IllegalArgumentException("Missing argument");
                }
                argument=args[i];
                continue;
            }
            throw new IllegalArgumentException("Unknown option: "+args[i]);
        }

        // Configure client.

        if (locale.getLanguage()!=null) {
            ActiveLocale.setProcessLocale(locale.toLocale());
        }
        Client c=new Client(APP_ID);

        // Obtain remote service handles.

        SampleStatelessService statelessService=
            c.getService(SampleStatelessService.class);
        SampleService service=
            c.getService(SampleService.class);

        // Execute stateless call.

        System.err.println(statelessService.hello(c.getContext(),argument));

        // Login.

        if (user!=null) {
            c.login(user,password);
        }
        try {

            // Execute remote call.

            System.err.println(service.hello(c.getContext(),argument));
        } finally {

            // Logout.

            if (c.getContext().getSessionId()!=null) {
                c.logout();
            }
        }
    }
}
