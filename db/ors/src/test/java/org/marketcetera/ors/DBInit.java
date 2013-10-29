
package org.marketcetera.ors;

import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.marketcetera.core.ApplicationBase;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/* $License$ */
/**
 * Initializes DB for ORS, so that it can be
 *
 * @author anshul@marketcetera.com
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
        } catch (Throwable t) {
            t.printStackTrace();
            SLF4JLoggerProxy.error(DBInit.class,"Error",t);
        }
    }
    /**
     * Initialize the schema and create the admin user
     * Close the spring context so that ORS can startup
     *
     * @throws Exception if there was an error.
     */
    public static void initORSDB()
            throws Exception
    {
        context = new FileSystemXmlApplicationContext(new String[] { "file:"+CONF_DIR+"dbinit.xml" },
                                                      null);
        LocalContainerEntityManagerFactoryBean emf = context.getBean(LocalContainerEntityManagerFactoryBean.class);
        Map<String,Object> jpaProperties = emf.getJpaPropertyMap();
        // force the schema to be recreated
        jpaProperties.put("hibernate.hbm2ddl.auto",
                          "create");
        context.start();
        context.close();
    }
    private static ConfigurableApplicationContext context;
}
