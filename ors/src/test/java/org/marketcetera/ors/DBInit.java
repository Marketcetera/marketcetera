
package org.marketcetera.ors;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.marketcetera.core.ApplicationBase;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/* $License$ */

/**
 * Initializes the DB and runs a specified application.
 *
 * @author anshul@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DBInit
        extends ApplicationBase
{
    /**
     * Runs the main method in the class supplied as the
     * first argument after initializing the DB.
     * 
     * @param args the class name of the main class followed
     * by any arguments to that class.
     */
    public static void main(String[] args) {
        try {
            initORSDB();
            // verify that at least the class name is supplied
            Assert.assertTrue("Class name not supplied",
                              args.length > 0);
            Class.forName(args[0]).getDeclaredMethod("main",
                                                     String[].class).invoke(null,
                                                                            (Object)Arrays.copyOfRange(args,
                                                                                                       1,
                                                                                                       args.length));
        } catch (Exception e) {
            e.printStackTrace();
            SLF4JLoggerProxy.error(DBInit.class,
                                   e,
                                   "Error");
        }
    }
    /**
     * Initializes the schema and create the admin user.
     * 
     * @throws IOException 
     */
    public static void initORSDB()
            throws IOException
    {
        ConfigurableApplicationContext context = new FileSystemXmlApplicationContext(new String[] { "file:"+CONF_DIR+"dbinit.xml" },
                                                                                     null);
        // this context is done, close it to allow the passed app to run normally
        context.close();
    }
}
