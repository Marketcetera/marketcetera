package org.marketcetera.oms;

import junit.extensions.RepeatedTest;
import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.*;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import org.marketcetera.quickfix.QuickFIXInitiator;
import quickfix.*;
import quickfix.field.Side;

import java.util.Vector;
import java.util.Properties;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class QuickFIXAdapterTest extends TestCase
{
    private QuickFIXInitiator qfInitiator;
    private MyFixSessionAdapterSource myqfAdapterSource;

    public QuickFIXAdapterTest(String inName)
    {
        super(inName);
    }

    public static Test suite()
    {
        MarketceteraTestSuite suite = new MarketceteraTestSuite(QuickFIXAdapterTest.class, OrderManagementSystem.OMS_MESSAGE_BUNDLE_INFO);
        suite.addTest(new RepeatedTest(new QuickFIXAdapterTest("testMessagePassthrough"), 5));
        return suite;
    }

    protected void setUp() throws Exception {
        super.setUp();
        ConfigData config = new PropertiesConfigData(ConfigPropertiesLoader.loadProperties(OrderManagementSystemIT.CONFIG_FILE));
        myqfAdapterSource = new MyFixSessionAdapterSource();
        qfInitiator = new QuickFIXInitiator(myqfAdapterSource);

        qfInitiator.init(config);
        myqfAdapterSource.clearAll();
    }

    protected void tearDown() throws Exception {
        if(qfInitiator != null) {
            qfInitiator.shutdown();
        }
        super.tearDown();
    }

    /** Basic test to create/init QF adapter
     * technically this is done in setup/teardown anyway
     * */
    public void testAdapterCreation() throws Exception {
        ConfigData config = new PropertiesConfigData(ConfigPropertiesLoader.loadProperties(OrderManagementSystemIT.CONFIG_FILE));
        QuickFIXInitiator qfInitiator = new QuickFIXInitiator(null);

        qfInitiator.init(config);

        qfInitiator.shutdown();
    }


    /** Sanity check to make sure that we read the config file paramters correctly
     * Since the QuickFIXInitiator.mSocketInitator field is private, we use the
     * {@link AccessViolator} to get to it.
     * (could create another accessor but we really don't want to expose that field)
     * @throws Exception
     */
    public void testConfigVarReading() throws Exception {
        QuickFIXInitiator qfi = new QuickFIXInitiator(null);
        Properties props = new Properties();
        props.setProperty(Session.SETTING_RESET_ON_DISCONNECT, "N");
        props.setProperty(Session.SETTING_RESET_ON_LOGOUT, "N");
        props.setProperty(Session.SETTING_RESET_WHEN_INITIATING_LOGON, "N");
        ConfigData config = new PropertiesConfigData(props);
        qfi.init(config);

        AccessViolator violator = new AccessViolator(QuickFIXInitiator.class);
        SocketInitiator socketI = (SocketInitiator) violator.getField("mSocketInitiator", qfi);
        assertEquals("N", socketI.getSettings().getString(Session.SETTING_RESET_ON_LOGOUT));
        assertEquals("N", socketI.getSettings().getString(Session.SETTING_RESET_ON_DISCONNECT));
        assertEquals("N", socketI.getSettings().getString(Session.SETTING_RESET_WHEN_INITIATING_LOGON));
        qfi.shutdown();

        // and now with positive values
        props.setProperty(Session.SETTING_RESET_ON_DISCONNECT, "Y");
        props.setProperty(Session.SETTING_RESET_ON_LOGOUT, "Y");
        props.setProperty(Session.SETTING_RESET_WHEN_INITIATING_LOGON, "Y");
        config = new PropertiesConfigData(props);
        qfi.init(config);

        socketI = (SocketInitiator) violator.getField("mSocketInitiator", qfi);
        assertEquals("Y", socketI.getSettings().getString(Session.SETTING_RESET_ON_LOGOUT));
        assertEquals("Y", socketI.getSettings().getString(Session.SETTING_RESET_ON_DISCONNECT));
        assertEquals("Y", socketI.getSettings().getString(Session.SETTING_RESET_WHEN_INITIATING_LOGON));
        qfi.shutdown();

    }
    public void testOnLogon() throws Exception {
        SessionID someSession = new SessionID("a", "b", "c");
        assertFalse(qfInitiator.isLoggedOn(someSession));
        qfInitiator.onLogon(someSession);
        assertTrue(qfInitiator.isLoggedOn(someSession));

        qfInitiator.onLogout(someSession);
        assertFalse(qfInitiator.isLoggedOn(someSession));
    }

    /** Verify messages go to the right adapter */
    public void testMessagePassthrough() throws Exception {
        Message msg = FIXMessageUtilTest.createNOS("TOLI", 12, 344, Side.SELL);
        qfInitiator.fromAdmin(msg, new SessionID("bogus", "b", "c"));
        myqfAdapterSource.verifySizes(0,0,0,0);


        // add a session
        SessionID session = new SessionID("knownSession", "a", "b");
        qfInitiator.onCreate(session);

        qfInitiator.fromApp(msg, session);
        myqfAdapterSource.verifySizes(1,0,0,0);

        qfInitiator.fromAdmin(msg, session);
        myqfAdapterSource.verifySizes(1,1,0,0);

        qfInitiator.toApp(msg, session);
        myqfAdapterSource.verifySizes(1,1,1,0);

        qfInitiator.toAdmin(msg, session);
        myqfAdapterSource.verifySizes(1,1,1,1);
    }

    private class MyFixSessionAdapterSource extends FIXSessionAdapterSource
    {
        private Vector<Message> fromApp = new Vector<Message>();
        private Vector<Message> fromAdmin = new Vector<Message>();
        private Vector<Message> toAdmin = new Vector<Message>();
        private Vector<Message> toApp = new Vector<Message>();

        public void fromApp(Message message) {
            fromApp.add(message);
        }

        public void fromAdmin(Message message) throws FieldNotFound {
            fromAdmin.add(message);
        }

        public void toAdmin(Message message) {
            toAdmin.add(message);
        }

        public void toApp(Message message) throws DoNotSend {
            toApp.add(message);
        }

        private void clearAll()
        {
            fromApp.clear();
            fromAdmin.clear();
            toAdmin.clear();
            toApp.clear();
        }

        private void verifySizes(int cfromApp, int cfromAdmin, int ctoApp, int ctoAdmin)
        {
            assertEquals("fromApp", cfromApp, fromApp.size());
            assertEquals("fromAdmin", cfromAdmin, fromAdmin.size());
            assertEquals("toApp", ctoApp, toApp.size());
            assertEquals("toAdmin", ctoAdmin, toAdmin.size());
        }
    }

}
