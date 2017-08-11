package org.marketcetera.trade.dao;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.ApplicationBase;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/* $License$ */

/**
 * Initializes the DB and runs a specified application.
 *
 * @author anshul@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DBInit.java 16522 2014-12-31 16:33:08Z colin $
 * @since 2.4.2
 */
@ClassVersion("$Id: DBInit.java 16522 2014-12-31 16:33:08Z colin $")
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
            Validate.isTrue(args.length > 0,
                            "Class name not supplied");
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
