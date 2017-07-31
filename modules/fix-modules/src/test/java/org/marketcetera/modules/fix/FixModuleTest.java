package org.marketcetera.modules.fix;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.SessionSettingsGenerator;
import org.marketcetera.fix.SessionSettingsProvider;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.quickfix.FIXVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;

import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Test {@link FixAcceptorModule} and {@link FixInitiatorModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:**/test.xml" })
public class FixModuleTest
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected failure occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        
    }
    /**
     * Run after each test.
     *
     * @throws Exception if an unexpected failure occurs
     */
    @After
    public void cleanup()
            throws Exception
    {
        if(acceptorModuleUrn != null) {
            try {
                moduleManager.stop(acceptorModuleUrn);
            } catch (Exception e) {
                PlatformServices.handleException(this,
                                                 "Problem stopping acceptor module",
                                                 e);
            } finally {
                acceptorModuleUrn = null;
            }
        }
    }
    /**
     * Test starting and connecting the acceptors and initiators.
     *
     * @throws Exception if an unexpected failure occurs
     */
    @Test
    public void testStart()
            throws Exception
    {
        SessionSettingsProvider acceptorSessionSettingsProvider = new SessionSettingsProvider() {
            @Override
            public SessionSettings create()
            {
                return SessionSettingsGenerator.generateSessionSettings(Lists.newArrayList(generateFixSession(1,true)),
                                                                        fixSettingsProviderFactory);
            }
        };
        acceptorModuleUrn = moduleManager.createModule(FixAcceptorModuleFactory.PROVIDER_URN,
                                                       acceptorSessionSettingsProvider);
        moduleManager.start(acceptorModuleUrn);
    }
    private FixSession generateFixSession(int inIndex,
                                          boolean inAcceptor)
    {
        String sender = inAcceptor?"TARGET"+inIndex:"MATP"+inIndex;
        String target = inAcceptor?"MATP"+inIndex:"TARGET"+inIndex;
        FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
        FixSession fixSession = fixSessionFactory.create();
        fixSession.setAffinity(1);
        fixSession.setBrokerId("test-"+(inAcceptor?"acceptor":"initiator")+inIndex);
        fixSession.setHost(fixSettingsProvider.getAcceptorHost());
        fixSession.setIsAcceptor(inAcceptor);
        fixSession.setIsEnabled(true);
        fixSession.setName(fixSession.getBrokerId());
        fixSession.setPort(fixSettingsProvider.getAcceptorPort());
        fixSession.setSessionId(new SessionID(FIXVersion.FIX42.getVersion(),
                                              sender,
                                              target).toString());
        fixSession.getSessionSettings().put(Session.SETTING_START_TIME,
                                            "00:00:00");
        fixSession.getSessionSettings().put(Session.SETTING_END_TIME,
                                            "00:00:00");
        return fixSession;
    }
    /**
     * test acceptor module
     */
    private ModuleURN acceptorModuleUrn;
    /**
     * creates {@link FixSession} objects
     */
    @Autowired
    private FixSessionFactory fixSessionFactory;
    /**
     * provides access to the module framework
     */
    @Autowired
    private ModuleManager moduleManager;
    /**
     * provides fix settings
     */
    @Autowired
    private FixSettingsProviderFactory fixSettingsProviderFactory;
}
