package org.marketcetera.oms.jcycloneSample;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jcyclone.core.boot.JCyclone;
import org.jcyclone.core.boot.Main;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.Util;
import org.marketcetera.core.ClassVersion;

import java.net.URL;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class JCycloneStagesTest extends TestCase {
    public JCycloneStagesTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        LoggerAdapter.initializeLogger("unitTest");
        return (new TestSuite(JCycloneStagesTest.class));
    }


    public void testStageThroughput() throws Exception {
        String config = "jcycloneTestApp.cfg";
        URL cfgResource = Util.loadFileFromClasspath(config, this);
        if(cfgResource == null) { throw new Exception("Unable to load config file "+config); }
        final String cfgFile = cfgResource.getPath();

        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new Runnable() {
            public void run() {
                Main.main(new String[]{cfgFile});
            }
        });

        service.awaitTermination(5, TimeUnit.SECONDS);
        //service.awaitTermination(500000, TimeUnit.SECONDS);

        JCyclone cyclone = JCyclone.getInstance();
        LastStage lastStage = (LastStage) cyclone.getManager().getStage("lastStage").getWrapper().getEventHandler();
        Vector<String> allMsgs = lastStage.getAllMessages();
        assertEquals("didn't get all messages", 3, allMsgs.size());
        for (int i = 0; i < allMsgs.size(); i++) {
              assertEquals("event "+(i+1)+"--middle--last", allMsgs.get(i));
        }
    }
}
