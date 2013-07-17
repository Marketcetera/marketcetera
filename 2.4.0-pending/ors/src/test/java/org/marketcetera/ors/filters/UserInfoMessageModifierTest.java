package org.marketcetera.ors.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.ors.filters.FieldRemoverMessageModifier;
import org.marketcetera.ors.filters.Messages;
import org.marketcetera.ors.info.SessionInfo;
import org.marketcetera.ors.info.SessionInfoImpl;
import org.marketcetera.ors.info.SystemInfoImpl;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.messagefactory.NoOpFIXMessageAugmentor;

import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.SenderSubID;
import quickfix.field.Side;

/* $License$ */

/**
 * Tests {@link UserInfoMessageModifier}.
 *
 */
public class UserInfoMessageModifierTest
{
    private FIXMessageFactory msgFactory = FIXVersion.FIX42.getMessageFactory();
    private Map<String,String> userMap = new HashMap<String,String>();
    protected static final SystemInfoImpl SYSTEM_INFO=
            new SystemInfoImpl();
        protected static final SessionInfoImpl SESSION_INFO=
            new SessionInfoImpl(SYSTEM_INFO);
    
    /**Initialize user mapping : Marketcetera User <==> Broker user
     * 
     */
    
    @Before
    public void init()
    {
    	userMap.put("admin", "spatil");
    	userMap.put("bjones", "bjones3");
    }
    
    /*Test modifier where User mapping exists 
     * 
     */
    @Test
    public void testUserMappingExists() throws Exception {
        Message msg = FIXMessageUtilTest.createNOS("ABC", new BigDecimal("23.33"), new BigDecimal("100"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        UserInfoMessageModifier umod = new UserInfoMessageModifier();
        SimpleUser simUser = new SimpleUser();
        simUser.setName("admin");
        SESSION_INFO.setValue(SessionInfo.ACTOR, simUser);
        umod.setSessionInfo(SESSION_INFO);
        umod.setMarketceteraToBrokerUserMap(userMap);        
        assertTrue(umod.modifyMessage(msg, null, new NoOpFIXMessageAugmentor()));
        assertEquals("spatil", msg.getHeader().getString(SenderSubID.FIELD)); 
    }
    
    /*Test modifier where User mapping does not exists 
     * 
     */
    @Test
    public void testUserMappingDoesNotExists() throws Exception {
        Message msg = FIXMessageUtilTest.createNOS("ABC", new BigDecimal("23.33"), new BigDecimal("100"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        UserInfoMessageModifier umod = new UserInfoMessageModifier();
        SimpleUser simUser = new SimpleUser();
        simUser.setName("admin_invalid");
        SESSION_INFO.setValue(SessionInfo.ACTOR, simUser);
        umod.setSessionInfo(SESSION_INFO);
        umod.setMarketceteraToBrokerUserMap(userMap);        
        assertTrue(umod.modifyMessage(msg, null, new NoOpFIXMessageAugmentor()));
        assertEquals("admin_invalid", msg.getHeader().getString(SenderSubID.FIELD)); 
    }
    
    /*Test modifier where session user not found 
     * 
     */
    @Test
    public void testSessionUserNotFound() throws Exception {
        Message msg = FIXMessageUtilTest.createNOS("ABC", new BigDecimal("23.33"), new BigDecimal("100"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        UserInfoMessageModifier umod = new UserInfoMessageModifier();
        SimpleUser simUser = new SimpleUser();
        simUser.setName("admin_invalid");
        SESSION_INFO.removeValue(SessionInfo.ACTOR);
        umod.setSessionInfo(SESSION_INFO);
        umod.setMarketceteraToBrokerUserMap(userMap);        
        assertFalse(umod.modifyMessage(msg, null, new NoOpFIXMessageAugmentor()));
        assertFalse("admin_invalid", msg.getHeader().isSetField(SenderSubID.FIELD)); 
    }
}
