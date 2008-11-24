package org.marketcetera.ors;

import org.marketcetera.persist.PersistTestBase;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.junit.Assert;

import java.util.Arrays;

/* $License$ */
/**
 * Initializes DB for ORS, so that it can be
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class DBInit {
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
            //Verify that atleast the class name is supplied
            Assert.assertTrue("Class name not supplied",args.length > 0);
            Class.forName(args[0]).getDeclaredMethod("main",
                    String[].class).invoke(null,
                    (Object)Arrays.copyOfRange(args,1,args.length));
        } catch (Throwable t) {
            SLF4JLoggerProxy.error(DBInit.class,"Error",t);
        }

    }

    /**
     * Initialize the schema and create the admin user
     * Close the spring context so that ORS can startup
     *
     * @throws Exception if there was an error.
     */
    static void initORSDB() throws Exception {
        //
        //Close the spring context so that ORS can startup
        PersistTestBase.springSetup(new String[]{
                "ors_initdb_create_admin_vendor.xml", "ors_db.xml"}, //$NON-NLS-1$ //$NON-NLS-2$
                    new FileSystemXmlApplicationContext(
                            OrderRoutingSystem.CFG_BASE_FILE_NAME)).close();
    }
}
