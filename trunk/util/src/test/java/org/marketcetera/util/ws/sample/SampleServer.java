package org.marketcetera.util.ws.sample;

import java.io.File;
import org.apache.log4j.PropertyConfigurator;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.util.ws.stateful.Server;
import org.marketcetera.util.ws.stateful.SessionManager;

/**
 * A sample server. It exposes a single service, which is configured
 * via command-line arguments.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class SampleServer
    extends TestCaseBase
{

    // MAIN PROGRAM.

    public static void main(String[] args)
        throws Exception
    {

        // Configure log4j.

        PropertyConfigurator.configure
            (DIR_ROOT+File.separator+"ws"+File.separator+"sample"+
             File.separator+"log_server.properties");

        // Parse command-line.

        String user=null;
        char[] password=null;
        I18NMessage2P greeting=SampleMessages.SHORT_GREETING;
        for (int i=0;i<args.length;i++) {
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
            if ("-longGreeting".equals(args[i])) {
                greeting=SampleMessages.LONG_GREETING;
                continue;
            }
            throw new IllegalArgumentException("Unknown option: "+args[i]);
        }

        // Configure server.

        Server<SampleSession> s=new Server<SampleSession>
            (new SampleAuthenticator(user,password),
             new SessionManager<SampleSession>());

        // Publish stateless service.

        s.publish(new SampleStatelessServiceImpl(greeting),
                  SampleStatelessService.class);

        // Publish service.

        s.publish(new SampleServiceImpl(s.getSessionManager(),greeting),
                  SampleService.class);

        // Wait indefinitely.

        s.wait();
    }
}
