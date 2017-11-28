package org.marketcetera.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.core.PlatformServices.divisionContext;

import java.io.File;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.marketcetera.admin.dao.PersistentPermissionDao;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.core.PriceQtyTuple;
import org.marketcetera.core.instruments.InstrumentToMessage;
import org.marketcetera.core.time.TimeFactoryImpl;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.dao.IncomingMessageDao;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.test.trade.Receiver;
import org.marketcetera.test.trade.Sender;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionTransType;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Messages;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.OrderBase;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.client.TradeClient;
import org.marketcetera.trade.dao.ExecutionReportDao;
import org.marketcetera.trade.dao.OrderSummaryDao;
import org.marketcetera.trade.dao.PersistentReportDao;
import org.marketcetera.trade.service.OrderSummaryService;
import org.marketcetera.trade.service.ReportService;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import junitparams.JUnitParamsRunner;
import quickfix.Acceptor;
import quickfix.DataDictionary;
import quickfix.FixVersions;
import quickfix.Initiator;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.field.MsgType;

/* $License$ */

/**
 * Provides common services for integrated Marketcetera platform tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(JUnitParamsRunner.class)
@ComponentScan(basePackages={"org.marketcetera","com.marketcetera"})
@EntityScan(basePackages={"org.marketcetera","com.marketcetera"})
@EnableJpaRepositories(basePackages={"org.marketcetera","com.marketcetera"})
public class MarketceteraTestBase
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        SLF4JLoggerProxy.info(this,
                              "{} beginning setup",
                              name.getMethodName());
        fixSettingsProvider = fixSettingsProviderFactory.create();
        if(reportService == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Report service will not be available for this test");
        }
        if(tradingClient == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Client will not be available for this test");
        } else {
            reports.clear();
            TradeMessageListener TradeMessageListener = new TradeMessageListener() {
                @Override
                public void receiveTradeMessage(TradeMessage inTradeMessage)
                {
                    reports.add(inTradeMessage);
                    synchronized(reports) {
                        reports.notifyAll();
                    }
                }
            };
            tradingClient.addTradeMessageListener(TradeMessageListener);
        }
        hostAcceptorPort = fixSettingsProvider.getAcceptorPort();
        remoteAcceptorPort = hostAcceptorPort + 1000;
        asyncExecutorService = Executors.newCachedThreadPool();
        clearOrderData();
        instrument = generateInstrument();
        SLF4JLoggerProxy.info(this,
                              "{} beginning",
                              name.getMethodName());
    }
    /**
     * Run after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void cleanup()
            throws Exception
    {
        try {
            SLF4JLoggerProxy.info(this,
                                  "{} cleanup beginning",
                                  name.getMethodName());
            if(tradingClient != null) {
                tradingClient.removeTradeMessageListener(TradeMessageListener);
            }
            try {
                if(receiver != null) {
                    receiver.stop();
                    receiver = null;
                }
            } catch (Exception ignored) {}
            for(Sender sender : senders.values()) {
                try {
                    sender.stop();
                } catch (Exception ignored) {}
            }
            senders.clear();
            resetSessions();
            remoteAcceptorSessions.clear();
            remoteSenderSessions.clear();
            remoteReceiverSessions.clear();
            asyncExecutorService.shutdownNow();
        } finally {
            SLF4JLoggerProxy.info(this,
                                  "{} done",
                                  name.getMethodName());
        }
    }
    /**
     * Get the instruments for test parameters.
     *
     * @return an <code>Object</code> value
     */
    protected Object instrumentParameters()
    {
        List<Object> results = Lists.newArrayList();
        for(Instrument instrument : getInstrumentList()) {
            results.add(new Object[] { instrument });
        }
        return results.toArray();
    }
    /**
     * Get the FIX versions for test parameters.
     *
     * @return an <code>Object</code> value
     */
    protected Object fixVersionParameters()
    {
        List<Object> results = Lists.newArrayList();
        for(FIXVersion fixVersion : getFixVersionList()) {
            results.add(new Object[] { fixVersion });
        }
        return results.toArray();
    }
    /**
     * Get the permutations of instruments and FIX versions for test parameters.
     *
     * @return an <code>Object</code> value
     */
    protected Object instrumentFixVersionParameters() 
    {
        List<Object> results = Lists.newArrayList();
        for(FIXVersion fixVersion : getFixVersionList()) {
            for(Instrument instrument : getInstrumentList()) {
                boolean valid = true;
                valid &= !(instrument instanceof org.marketcetera.trade.Future) || fixVersion.ordinal() >= FIXVersion.FIX41.ordinal();
                valid &= !(instrument instanceof Currency) || fixVersion.ordinal() >= FIXVersion.FIX41.ordinal();
                valid &= !(instrument instanceof Option) || fixVersion.ordinal() >= FIXVersion.FIX41.ordinal();
                valid &= !(instrument instanceof ConvertibleBond) || fixVersion.ordinal() >= FIXVersion.FIX42.ordinal();
                if(valid) {
                    results.add(new Object[] {instrument,fixVersion});
                }
            }
        }
        return results.toArray();
    }
    /**
     * Get list of FIX versions to use for testing.
     *
     * @return a <code>List&lt;FIXVersion&gt;</code> value
     */
    private List<FIXVersion> getFixVersionList()
    {
        List<FIXVersion> fixVersions = Lists.newArrayList();
        for(FIXVersion fixVersion : FIXVersion.values()) {
            if(fixVersion != FIXVersion.FIX_SYSTEM) {
                fixVersions.add(fixVersion);
            }
        }
        return fixVersions;
    }
    /**
     * Get list of instruments to use for testing.
     *
     * @return a <code>List&lt;Instrument&gt;</code> value
     */
    private List<Instrument> getInstrumentList()
    {
        List<Instrument> instruments = Lists.newArrayList();
        instruments.add(new Equity("METC"));
        instruments.add(org.marketcetera.trade.Future.fromString("METC-201811"));
        instruments.add(new Currency("USD/GBP"));
        instruments.add(new Option("METC","201811",EventTestBase.generateDecimalValue(),OptionType.Put));
        instruments.add(new ConvertibleBond("FR0011453463"));
        return instruments;
    }
    /**
     * Disable and delete all existing host sessions.
     *
     * @throws Exception if an unexpected error occurs
     */
    protected void resetSessions()
            throws Exception
    {
        for(FixSession fixSession : brokerService.findFixSessions()) {
            SessionID sessionId = new SessionID(fixSession.getSessionId());
            BrokerID brokerId = new BrokerID(fixSession.getBrokerId());
            brokerService.disableSession(sessionId);
            verifySessionDisabled(brokerId);
            brokerService.delete(sessionId);
            verifySessionDeleted(brokerId);
        }
    }
    /**
     * Clear order data.
     *
     * @throws Exception if an unexpected error occurs
     */
    protected void clearOrderData()
            throws Exception
    {
        orderStatusDao.deleteAllInBatch();
        executionReportDao.deleteAll();
        reportDao.deleteAllInBatch();
    }
    /**
     * Get the host base to use for new sessions.
     *
     * @return a <code>String</code> value
     */
    protected String getHostBase()
    {
        return defaultHostBase;
    }
    /**
     * Get the FIX version to use for new sessions.
     *
     * @return a <code>FIXVersion</code> value
     */
    protected FIXVersion getFixVersion()
    {
        return defaultFixVersion;
    }
    /**
     * Wait for and retrieve the next report received from the client.
     *
     * @return a <code>TradeMessage</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected TradeMessage waitForClientReport()
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !reports.isEmpty();
            }
        },10);
        return reports.getLast();
    }
    /**
     * Verify that the order status for the given root/order id pair exists.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @param inOrderId an <code>OrderID</code> value
     * @param inExpectedOrderStatus an <code>OrderStatus</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyOrderStatus(final OrderID inRootOrderId,
                                     final OrderID inOrderId,
                                     final OrderStatus inExpectedOrderStatus)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                OrderSummary orderStatus = orderSummaryService.findByRootOrderIdAndOrderId(inRootOrderId,
                                                                                          inOrderId);
                if(orderStatus == null) {
                    return false;
                }
                return orderStatus.getOrderStatus() == inExpectedOrderStatus;
            }
        },10);
    }
    /**
     * Create a host acceptor session with the given index.
     *
     * @param inSessionIndex an <code>int</code> value
     * @return a <code>SessionID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected SessionID createAcceptorSession(int inSessionIndex)
            throws Exception
    {
        final BrokerID testAcceptorBrokerId = new BrokerID("local_acceptor" + inSessionIndex);
        verifyBrokerStatus(testAcceptorBrokerId,
                           null);
        FixSession testSession = fixSessionFactory.create();
        testSession.setAffinity(1);
        testSession.setBrokerId(testAcceptorBrokerId.getValue());
        testSession.setHost("localhost");
        testSession.setPort(hostAcceptorPort);
        testSession.setName("local_acceptor" + inSessionIndex);
        FIXVersion fixVersion = getFixVersion();
        String beginString = fixVersion.getVersion();
        if(fixVersion.isFixT()) {
            beginString = FixVersions.BEGINSTRING_FIXT11;
        }
        testSession.setSessionId(beginString+":"+getHostBase()+"->"+senderBase+inSessionIndex);
        testSession.setIsAcceptor(true);
        testSession.getSessionSettings().put(Session.SETTING_START_TIME,
                                             "00:00:00");
        testSession.getSessionSettings().put(Session.SETTING_END_TIME,
                                             "00:00:00");
        testSession.getSessionSettings().put(Session.SETTING_RESET_ON_LOGON,
                                             "Y");
        testSession.getSessionSettings().put(Session.SETTING_RESET_ON_LOGOUT,
                                             "Y");
        testSession.getSessionSettings().put(Session.SETTING_RESET_ON_DISCONNECT,
                                             "Y");
        testSession.getSessionSettings().put(Session.SETTING_RESET_ON_ERROR,
                                             "Y");
        if(fixVersion.isFixT()) {
            testSession.getSessionSettings().put(Session.SETTING_DEFAULT_APPL_VER_ID,
                                                 fixVersion.getApplicationVersion());
        }
        testSession = brokerService.save(testSession);
        testSession = onCreateAcceptorSession(testSession);
        SessionID testAcceptorSessionId = new SessionID(testSession.getSessionId());
        brokerService.enableSession(testAcceptorSessionId);
        verifySessionEnabled(testAcceptorBrokerId);
        createRemoteSenderSession(inSessionIndex);
        verifySessionLoggedOn(testAcceptorBrokerId);
        return testAcceptorSessionId;
    }
    /**
     * Called when a new acceptor FIX session is created.
     *
     * <p>This method is invoked with an attached session after it is initially saved
     * and before it is enabled.
     *
     * @param inSession a <code>FixSession</code> value
     * @return a <code>FixSession</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected FixSession onCreateAcceptorSession(FixSession inSession)
            throws Exception
    {
        return inSession;
    }
    /**
     * Called when a new initiator FIX session is created.
     *
     * <p>This method is invoked with an attached session after it is initially saved
     * and before it is enabled.
     *
     * @param inSession a <code>FixSession</code> value
     * @return a <code>FixSession</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected FixSession onCreateInitiatorSession(FixSession inSession)
            throws Exception
    {
        return inSession;
    }
    /**
     * Create a remote initiator session designed to connect to a host acceptor session with the same index.
     *
     * @param inSessionIndex an <code>int</code> value
     * @return a <code>SessionID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected SessionID createRemoteSenderSession(int inSessionIndex)
            throws Exception
    {
        FIXVersion fixVersion = getFixVersion();
        String beginString = fixVersion.getVersion();
        if(fixVersion.isFixT()) {
            beginString = FixVersions.BEGINSTRING_FIXT11;
        }
        String session = beginString+":"+senderBase+inSessionIndex+"->"+getHostBase();
        SessionID sessionId = new SessionID(session);
        FixSession testSession = fixSessionFactory.create();
        testSession.setAffinity(1);
        testSession.setHost("localhost");
        testSession.setPort(hostAcceptorPort); 
        testSession.setName(session);
        testSession.setSessionId(session);
        testSession.setIsAcceptor(false);
        testSession.getSessionSettings().put(Session.SETTING_HEARTBTINT,
                                             String.valueOf(15));
        testSession.getSessionSettings().put(Session.SETTING_START_TIME,
                                             "00:00:00");
        testSession.getSessionSettings().put(Session.SETTING_END_TIME,
                                             "00:00:00");
        testSession.getSessionSettings().put(Initiator.SETTING_RECONNECT_INTERVAL,
                                             String.valueOf(1));
        testSession.getSessionSettings().put(Session.SETTING_RESET_ON_LOGON,
                                             "N");
        testSession.getSessionSettings().put(Session.SETTING_RESET_ON_LOGOUT,
                                             "N");
        testSession.getSessionSettings().put(Session.SETTING_RESET_ON_DISCONNECT,
                                             "N");
        testSession.getSessionSettings().put(Session.SETTING_RESET_ON_ERROR,
                                             "N");
        if(fixVersion.isFixT()) {
            testSession.getSessionSettings().put(Session.SETTING_DEFAULT_APPL_VER_ID,
                                                 fixVersion.getApplicationVersion());
        }
        testSession.setIsEnabled(true);
        Sender sender = new Sender();
        sender.setMessageFactory(messageFactory);
        SessionSettings remoteSessionSettings = brokerService.generateSessionSettings(Lists.newArrayList(testSession));
        remoteSessionSettings.getDefaultProperties().setProperty(Acceptor.SETTING_SOCKET_ACCEPT_PORT,
                                                                 String.valueOf(hostAcceptorPort));
        sender.setSessionSettings(remoteSessionSettings);
        sender.setFixSettingsProviderFactory(applicationContext.getBean(FixSettingsProviderFactory.class));
        sender.start();
        senders.put(sessionId,
                    sender);
        remoteSenderSessions.put(inSessionIndex,
                                 sessionId);
        return sessionId;
    }
    /**
     * Create a host initiator session with the given index.
     *
     * @param inSessionIndex an <code>int</code> value
     * @return a <code>SessionID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected SessionID createInitiatorSession(int inSessionIndex)
            throws Exception
    {
        final BrokerID testInitiatorBrokerId = new BrokerID("local_initiator" + inSessionIndex);
        verifyBrokerStatus(testInitiatorBrokerId,
                           null);
        FixSession testSession = fixSessionFactory.create();
        testSession.setAffinity(1);
        testSession.setBrokerId(testInitiatorBrokerId.getValue());
        testSession.setHost("localhost");
        testSession.setPort(remoteAcceptorPort);
        testSession.setName("local_initiator" + inSessionIndex);
        FIXVersion fixVersion = getFixVersion();
        String beginString = fixVersion.getVersion();
        if(fixVersion.isFixT()) {
            beginString = FixVersions.BEGINSTRING_FIXT11;
        }
        testSession.setSessionId(beginString+":"+getHostBase()+"->"+receiverBase+inSessionIndex);
        testSession.setIsAcceptor(false);
        testSession.getSessionSettings().put(Session.SETTING_START_TIME,
                                             "00:00:00");
        testSession.getSessionSettings().put(Session.SETTING_END_TIME,
                                             "00:00:00");
        testSession.getSessionSettings().put(Session.SETTING_HEARTBTINT,
                                             String.valueOf(15));
        testSession.getSessionSettings().put(Initiator.SETTING_RECONNECT_INTERVAL,
                                             String.valueOf(1));
        testSession.getSessionSettings().put(Session.SETTING_RESET_ON_LOGON,
                                             "N");
        testSession.getSessionSettings().put(Session.SETTING_RESET_ON_LOGOUT,
                                             "N");
        testSession.getSessionSettings().put(Session.SETTING_RESET_ON_DISCONNECT,
                                             "N");
        testSession.getSessionSettings().put(Session.SETTING_RESET_ON_ERROR,
                                             "N");
        if(fixVersion.isFixT()) {
            testSession.getSessionSettings().put(Session.SETTING_DEFAULT_APPL_VER_ID,
                                                 fixVersion.getApplicationVersion());
        }
        testSession = brokerService.save(testSession);
        testSession = onCreateInitiatorSession(testSession);
        SessionID testInitiatorSessionId = new SessionID(testSession.getSessionId());
        brokerService.enableSession(testInitiatorSessionId);
        verifySessionEnabled(testInitiatorBrokerId);
        createRemoteReceiverSession(inSessionIndex);
        verifySessionLoggedOn(testInitiatorBrokerId);
        return testInitiatorSessionId;
    }
    /**
     * Create a remote acceptor session designed to connect to a host acceptor session with the same index.
     * 
     * <p>Note: this method will disrupt all existing remote acceptor sessions, though they should reconnect.
     *
     * @param inSessionIndex an <code>int</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void createRemoteReceiverSession(int inSessionIndex)
            throws Exception
    {
        if(receiver != null) {
            receiver.stop();
            receiver = null;
        }
        FIXVersion fixVersion = getFixVersion();
        String beginString = fixVersion.getVersion();
        if(fixVersion.isFixT()) {
            beginString = FixVersions.BEGINSTRING_FIXT11;
        }
        String session = beginString+":"+receiverBase + inSessionIndex + "->"+getHostBase();
        FixSession testSession = fixSessionFactory.create();
        testSession.setAffinity(1);
        testSession.setHost("localhost");
        testSession.setPort(remoteAcceptorPort); 
        testSession.setName(session);
        testSession.setSessionId(session);
        testSession.setIsAcceptor(true);
        testSession.getSessionSettings().put(Session.SETTING_START_TIME,
                                             "00:00:00");
        testSession.getSessionSettings().put(Session.SETTING_END_TIME,
                                             "00:00:00");
        testSession.getSessionSettings().put(Session.SETTING_RESET_ON_LOGON,
                                             "Y");
        testSession.getSessionSettings().put(Session.SETTING_RESET_ON_LOGOUT,
                                             "Y");
        testSession.getSessionSettings().put(Session.SETTING_RESET_ON_DISCONNECT,
                                             "Y");
        testSession.getSessionSettings().put(Session.SETTING_RESET_ON_ERROR,
                                             "Y");
        if(fixVersion.isFixT()) {
            testSession.getSessionSettings().put(Session.SETTING_DEFAULT_APPL_VER_ID,
                                                 fixVersion.getApplicationVersion());
        }
        testSession.setIsEnabled(true);
        remoteAcceptorSessions.add(testSession);
        receiver = new Receiver();
        receiver.setMessageFactory(messageFactory);
        SessionSettings remoteSessionSettings = brokerService.generateSessionSettings(remoteAcceptorSessions);
        remoteSessionSettings.getDefaultProperties().setProperty(Acceptor.SETTING_SOCKET_ACCEPT_PORT,
                                                                 String.valueOf(remoteAcceptorPort));
        receiver.setSessionSettings(remoteSessionSettings);
        receiver.setFixSettingsProviderFactory(applicationContext.getBean(FixSettingsProviderFactory.class));
        receiver.start();
        remoteReceiverSessions.put(inSessionIndex,
                                   new SessionID(session));
    }
    protected void assertSymbol(Message inMessage,
                                String inSymbol)
            throws Exception
    {
        assertEquals(inSymbol,
                     inMessage.getString(quickfix.field.Symbol.FIELD));
    }
    protected void assertPrice(Message inMessage,
                               BigDecimal inPrice)
            throws Exception
    {
        assertQty(inMessage,
                  quickfix.field.Price.FIELD,
                  inPrice);
    }
    protected void assertLastPrice(Message inMessage,
                                   BigDecimal inLastPrice)
            throws Exception
    {
        assertQty(inMessage,
                  quickfix.field.LastPx.FIELD,
                  inLastPrice);
    }
    protected void assertLastQty(Message inMessage,
                                 BigDecimal inLastQty)
            throws Exception
    {
        assertQty(inMessage,
                  quickfix.field.LastQty.FIELD,
                  inLastQty);
    }
    protected void assertLeavesQty(Message inMessage,
                                   BigDecimal inLeavesQty)
            throws Exception
    {
        assertQty(inMessage,
                  quickfix.field.LeavesQty.FIELD,
                  inLeavesQty);
    }
    /**
     * Verify that the given message has a cum qty field with the given expected value.
     *
     * @param inMessage a <code>Message</code> value
     * @param inCumQty a <code>BigDecimal</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void assertCumQty(Message inMessage,
                                BigDecimal inCumQty)
            throws Exception
    {
        assertQty(inMessage,
                  quickfix.field.CumQty.FIELD,
                  inCumQty);
    }
    protected void assertOrderQty(Message inMessage,
                                  BigDecimal inOrderQty)
            throws Exception
    {
        assertQty(inMessage,
                  quickfix.field.OrderQty.FIELD,
                  inOrderQty);
    }
    protected void assertMaxFloor(Message inMessage,
                                  BigDecimal inOrderQty)
            throws Exception
    {
        assertQty(inMessage,
                  quickfix.field.MaxFloor.FIELD,
                  inOrderQty);
    }
    protected void assertQty(Message inMessage,
                             int inTag,
                             BigDecimal inExpectedQty)
            throws Exception
    {
        BigDecimal actualQty = inMessage.getDecimal(inTag);
        assertEquals("Expected: " + inExpectedQty + " actual: " + actualQty + " for " + inTag + " on " + inMessage,
                     0,
                     inExpectedQty.compareTo(actualQty));
    }
    /**
     * Verify that the given report represents a filled order.
     *
     * @param inMessage a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void assertFilled(Message inMessage)
            throws Exception
    {
        assertOrdStatus(inMessage,
                        OrderStatus.Filled);
        assertLeavesQty(inMessage,
                        BigDecimal.ZERO);
        assertCumQty(inMessage,
                     inMessage.getDecimal(quickfix.field.OrderQty.FIELD));
    }
    /**
     * Verify that the given report represents a canceled order.
     *
     * @param inMessage a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void assertCanceled(Message inMessage)
            throws Exception
    {
        assertOrdStatus(inMessage,
                        OrderStatus.Canceled);
        assertLeavesQty(inMessage,
                        BigDecimal.ZERO);
    }
    /**
     * Assert that the given message contains an AvgPx value equivalent to the expected given value.
     *
     * @param inMessage a <code>Message</code> value
     * @param inAvgPx a <code>BigDecimal</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void assertAvgPx(Message inMessage,
                               BigDecimal inAvgPx)
            throws Exception
    {
        assertQty(inMessage,
                  quickfix.field.AvgPx.FIELD,
                  inAvgPx);
    }
    protected void assertNew(Message inMessage)
            throws Exception
    {
        assertOrdStatus(inMessage,
                        OrderStatus.New);
        assertCumQty(inMessage,
                     BigDecimal.ZERO);
        BigDecimal orderQty = inMessage.getDecimal(quickfix.field.OrderQty.FIELD);
        assertLeavesQty(inMessage,
                        orderQty);
    }
    /**
     * Assert that the given message is a proper status message.
     *
     * @param inStatusMessage a <code>Message</code> value
     * @throws Exception 
     */
    protected void assertStatus(Message inStatusMessage)
            throws Exception
    {
        DataDictionary dataDictionary = FIXMessageUtil.getDataDictionary(inStatusMessage);
        if(dataDictionary.isField(quickfix.field.ExecTransType.FIELD)) {
            assertExecTransType(inStatusMessage,
                                ExecutionTransType.Status);
        } else {
            assertExecType(inStatusMessage,
                           ExecutionType.OrderStatus);
        }
    }
    protected void assertReplaced(Message inMessage)
            throws Exception
    {
        assertOrdStatus(inMessage,
                        OrderStatus.Replaced);
    }
    protected void assertPartiallyFilled(Message inMessage)
            throws Exception
    {
        assertOrdStatus(inMessage,
                        OrderStatus.PartiallyFilled);
    }
    protected void assertSide(Message inMessage,
                              Side inSide)
            throws Exception
    {
        assertEquals(inSide,
                     Side.getInstanceForFIXValue(inMessage.getChar(quickfix.field.Side.FIELD)));
    }
    protected void assertOrderType(Message inMessage,
                                   OrderType inOrderType)
            throws Exception
    {
        assertEquals(inOrderType,
                     OrderType.getInstanceForFIXValue(inMessage.getChar(quickfix.field.OrdType.FIELD)));
    }
    protected void assertOrdStatus(Message inMessage,
                                   OrderStatus inOrderStatus)
            throws Exception
    {
        assertEquals(inOrderStatus,
                     OrderStatus.getInstanceForFIXValue(inMessage.getChar(quickfix.field.OrdStatus.FIELD)));
    }
    protected void assertExecType(Message inMessage,
                                  ExecutionType inExecutionType)
            throws Exception
    {
        assertEquals(inExecutionType,
                     ExecutionType.getInstanceForFIXValue(inMessage.getChar(quickfix.field.ExecType.FIELD)));
    }
    /**
     * Assert that the exec trans type on the given message matches the given expected value.
     *
     * @param inMessage a <code>Message</code> value
     * @param inExecTransType an <code>ExecutionTransType</code> value
     * @throws Exception if the assertion fails or cannot be executed
     */
    protected void assertExecTransType(Message inMessage,
                                       ExecutionTransType inExecTransType)
            throws Exception
    {
        assertEquals(inExecTransType,
                     ExecutionTransType.getInstanceForFIXValue(inMessage.getChar(quickfix.field.ExecTransType.FIELD)));
    }
    protected void assertClOrdId(Message inLeft,
                                 Message inRight)
            throws Exception
    {
        assertField(inLeft,
                    inRight,
                    quickfix.field.ClOrdID.FIELD,
                    true);
    }
    protected void assertOrigClOrdId(Message inOriginalOrder,
                                     Message inNewOrder)
            throws Exception
    {
        String clOrdId = inOriginalOrder.getString(quickfix.field.ClOrdID.FIELD);
        String origClOrdId = inNewOrder.getString(quickfix.field.OrigClOrdID.FIELD);
        assertEquals(clOrdId,
                     origClOrdId);
    }
    protected void assertField(Message inLeft,
                               Message inRight,
                               int inTag,
                               boolean inMandatory)
            throws Exception
    {
        String leftValue = inLeft.isSetField(inTag)?inLeft.getString(inTag):null;
        String rightValue = inRight.isSetField(inTag)?inRight.getString(inTag):null;
        if(inMandatory) {
            assertNotNull(inLeft + " is missing mandatory value for tag " + inTag,
                          leftValue);
            assertNotNull(inRight + " is missing mandatory value for tag " + inTag,
                          rightValue);
        }
        assertEquals(rightValue,
                     leftValue);
    }
    /**
     * Verifies that an execution report for the given order was received from the given target.
     *
     * @param inOrder a <code>Message</code> value
     * @param inTarget a <code>SessionID</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected Message verifyExecutionReceivedAsync(Message inOrder,
                                                   SessionID inTarget)
            throws Exception
    {
        Future<Message> response = waitForAndVerifyReceiverMessageAsync(inTarget,
                                                                        MsgType.EXECUTION_REPORT);
        Message executionReport = response.get(waitPeriod,
                                               TimeUnit.MILLISECONDS);
        assertEquals(inOrder.getString(quickfix.field.ClOrdID.FIELD),
                     executionReport.getString(quickfix.field.ClOrdID.FIELD));
        return executionReport;
    }
    /**
     * Wait for and verify a market data incremental refresh for the given request and session id.
     *
     * @param inRequest a <code>Message</code> value
     * @param inTarget a <code>SessionID</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurred
     */
    protected Message verifyMarketDataIncrementalRefreshReceivedAsync(Message inRequest,
                                                                      SessionID inTarget)
            throws Exception
    {
        Future<Message> response = waitForAndVerifyReceiverMessageAsync(inTarget,
                                                                        MsgType.MARKET_DATA_INCREMENTAL_REFRESH);
        Message refresh = response.get(waitPeriod,
                                       TimeUnit.MILLISECONDS);
        assertEquals(inRequest.getString(quickfix.field.MDReqID.FIELD),
                     refresh.getString(quickfix.field.MDReqID.FIELD));
        return refresh;
    }
    /**
     * Wait for and verify a market data snapshot refresh for the given request and session id.
     *
     * @param inRequest a <code>Message</code> value
     * @param inTarget a <code>SessionID</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurred
     */
    protected Message verifyMarketDataSnapshotRefreshReceivedAsync(Message inRequest,
                                                                   SessionID inTarget)
            throws Exception
    {
        Future<Message> response = waitForAndVerifyReceiverMessageAsync(inTarget,
                                                                        MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH);
        Message refresh = response.get(waitPeriod,
                                       TimeUnit.MILLISECONDS);
        assertEquals(inRequest.getString(quickfix.field.MDReqID.FIELD),
                     refresh.getString(quickfix.field.MDReqID.FIELD));
        return refresh;
    }
    /**
     * Verify that an execution report was received for the given target.
     *
     * @param inTarget a <code>SessionID</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected Message verifyExecutionReceived(SessionID inTarget)
            throws Exception
    {
        return waitForAndVerifyReceiverMessage(inTarget,
                                               MsgType.EXECUTION_REPORT);
    }
    /**
     * Verifies that the order was received, including two execution reports which make up the ack.
     *
     * @param inOrder a <code>Message</code> value
     * @param inTarget a <code>SessionID</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected Message verifyOrderReceived(Message inOrder,
                                          SessionID inTarget)
            throws Exception
    {
        Message orderPendingMsg = waitForAndVerifyReceiverMessage(inTarget,
                                                                  MsgType.EXECUTION_REPORT);
        assertEquals("Expected " + OrderStatus.PendingNew + " actual " + OrderStatus.getInstanceForFIXMessage(orderPendingMsg),
                     OrderStatus.PendingNew.getFIXValue(),
                     orderPendingMsg.getChar(quickfix.field.OrdStatus.FIELD));
        if(inOrder.isSetField(quickfix.field.MaxFloor.FIELD)) {
            assertMaxFloor(orderPendingMsg,
                           inOrder.getDecimal(quickfix.field.MaxFloor.FIELD));
        }
        Message orderAckMsg = waitForAndVerifyReceiverMessage(inTarget,
                                                              MsgType.EXECUTION_REPORT);
        assertEquals("Expected " + OrderStatus.New + " actual " + OrderStatus.getInstanceForFIXMessage(orderAckMsg),
                     OrderStatus.New.getFIXValue(),
                     orderAckMsg.getChar(quickfix.field.OrdStatus.FIELD));
        assertEquals(inOrder.getString(quickfix.field.ClOrdID.FIELD),
                     orderPendingMsg.getString(quickfix.field.ClOrdID.FIELD));
        assertEquals(inOrder.getString(quickfix.field.ClOrdID.FIELD),
                     orderAckMsg.getString(quickfix.field.ClOrdID.FIELD));
        if(inOrder.isSetField(quickfix.field.MaxFloor.FIELD)) {
            assertMaxFloor(orderAckMsg,
                           inOrder.getDecimal(quickfix.field.MaxFloor.FIELD));
        }
        return orderAckMsg;
    }
    /**
     * Verify that the order was received, and canceled.
     *
     * @param inOrder a <code>Message</code> value
     * @param inTarget a <code>SessionID</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected Message verifyOrderCanceled(Message inOrder,
                                          SessionID inTarget)
            throws Exception
    {
        Message orderPendingMsg = waitForAndVerifyReceiverMessage(inTarget,
                                                                  MsgType.EXECUTION_REPORT);
        assertOrdStatus(orderPendingMsg,
                        OrderStatus.PendingCancel);
        assertOrigClOrdId(inOrder,
                          orderPendingMsg);
        Message orderAckMsg = waitForAndVerifyReceiverMessage(inTarget,
                                                              MsgType.EXECUTION_REPORT);
        assertOrdStatus(orderAckMsg,
                        OrderStatus.Canceled);
        assertExecType(orderAckMsg,
                       ExecutionType.Canceled);
        assertOrigClOrdId(inOrder,
                          orderAckMsg);
        assertClOrdId(orderPendingMsg,
                      orderAckMsg);
        return orderAckMsg;
    }
    /**
     * Verify that the order is rejected.
     *
     * @param inOrder a <code>Message</code> value
     * @param inTarget a <code>SessionID</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected Message verifyOrderRejected(Message inOrder,
                                          SessionID inTarget)
            throws Exception
    {
        Message orderRejectMsg = waitForAndVerifyReceiverMessage(inTarget,
                                                                 MsgType.EXECUTION_REPORT);
        assertOrdStatus(orderRejectMsg,
                        OrderStatus.Rejected);
        assertExecType(orderRejectMsg,
                       ExecutionType.Rejected);
        assertClOrdId(inOrder,
                      orderRejectMsg);
        return orderRejectMsg;
    }
    protected Message verifyCancelReject(Message inOrder,
                                         Message inOrderReplace,
                                         SessionID inTarget)
            throws Exception
    {
        Message orderPendingMsg = waitForAndVerifyReceiverMessage(inTarget,
                                                                  MsgType.EXECUTION_REPORT);
        assertOrdStatus(orderPendingMsg,
                        OrderStatus.PendingReplace);
        assertOrigClOrdId(inOrder,
                          orderPendingMsg);
        Message orderAckMsg = waitForAndVerifyReceiverMessage(inTarget,
                                                              MsgType.ORDER_CANCEL_REJECT);
        assertOrigClOrdId(inOrder,
                          orderAckMsg);
        assertClOrdId(inOrderReplace,
                      orderAckMsg);
        return orderAckMsg;
    }
    protected Message verifyOrderReplaced(Message inOrder,
                                          SessionID inTarget)
            throws Exception
    {
        Message orderPendingMsg = waitForAndVerifyReceiverMessage(inTarget,
                                                                  MsgType.EXECUTION_REPORT);
        assertOrdStatus(orderPendingMsg,
                        OrderStatus.PendingReplace);
        assertOrigClOrdId(inOrder,
                          orderPendingMsg);
        Message orderAckMsg = waitForAndVerifyReceiverMessage(inTarget,
                                                              MsgType.EXECUTION_REPORT);
        assertOrdStatus(orderAckMsg,
                        OrderStatus.Replaced);
        assertExecType(orderAckMsg,
                       ExecutionType.Replace);
        assertOrigClOrdId(inOrder,
                          orderAckMsg);
        assertClOrdId(orderPendingMsg,
                      orderAckMsg);
        return orderAckMsg;
    }
    protected Instrument generateInstrument()
    {
        Instrument instrument;
        String ticker1 = "MTC" + counter.incrementAndGet();
        switch(random.nextInt(5)) {
            case 0:
                instrument = new Equity(ticker1);
                break;
            case 1:
                instrument = org.marketcetera.trade.Future.fromString(ticker1+"-201710");
                break;
            case 2:
                String ticker2 = "METC" + counter.incrementAndGet();
                instrument = new Currency(ticker1,
                                          ticker2,
                                          "",
                                          "");
                break;
            case 3:
                instrument = new Option(ticker1,
                                        "20171001",
                                        new BigDecimal(100),
                                        random.nextBoolean()?OptionType.Put:OptionType.Call);
                break;
            case 4:
                instrument = new ConvertibleBond(ticker1 + " 2.54% 10/01/2017");
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return instrument;
    }
    /**
     * Generate a unique file store directory.
     *
     * @return a <code>File</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected File generateFileStore()
            throws Exception
    {
        File tmpDir = FileUtils.getTempDirectory();
        File fileStoreDir = new File(tmpDir,
                                     UUID.randomUUID().toString());
        fileStoreDir.deleteOnExit();
        FileUtils.forceMkdir(fileStoreDir);
        return fileStoreDir;
    }
    /**
     * Generates a unique id.
     *
     * @return a <code>String</code> value
     */
    protected static String generateId()
    {
        return UUID.randomUUID().toString();
    }
    /**
     * Generates a random value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public static BigDecimal generateDecimalValue()
    {
        return new BigDecimal(String.format("%d.%d",
                                            random.nextInt(10000),
                                            random.nextInt(100)));
    }
    /**
     * Verify that all brokers are connected.
     *
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyAllBrokersReady()
            throws Exception
    {
        for(FixSession fixSession : brokerService.findFixSessions()) {
            verifySessionLoggedOn(new BrokerID(fixSession.getBrokerId()));
        }
    }
    protected void verifyStatusNew(OrderBase inOrder,
                                   ReportBase inReport)
    {
        assertEquals(OrderStatus.New,
                     inReport.getOrderStatus());
        verifyOrderId(inOrder,
                      inReport);
    }
    protected void verifyOrderId(OrderBase inOrder,
                                 ReportBase inReport)
    {
        assertEquals(inOrder.getOrderID(),
                     inReport.getOrderID());
    }
    protected void verifyOrderId(Message inOrder,
                                 ReportBase inReport)
            throws Exception
    {
        assertEquals(inOrder.getString(quickfix.field.ClOrdID.FIELD),
                     inReport.getOrderID().getValue());
    }
    protected void verifyOrderId(Message inOrder,
                                 Message inReport)
            throws Exception
    {
        assertEquals(inOrder.getString(quickfix.field.ClOrdID.FIELD),
                     inReport.getString(quickfix.field.ClOrdID.FIELD));
    }
    protected BrokerID getBrokerIdFor(SessionID inSessionId)
    {
        FixSession session = brokerService.findFixSessionBySessionId(inSessionId);
        assertNotNull("Unknown FIX session: " + inSessionId,
                      session);
        return new BrokerID(session.getBrokerId());
    }
    /**
     * Verify that the given message is marked as a possible duplicate.
     *
     * @param inMessage a <code>Message</code> value
     */
    protected void verifyPossDup(Message inMessage)
            throws Exception
    {
        assertTrue("PossDup(" + quickfix.field.PossDupFlag.FIELD + ") not set on " + inMessage,
                   inMessage.getHeader().isSetField(quickfix.field.PossDupFlag.FIELD));
        assertTrue("PossDup(" + quickfix.field.PossDupFlag.FIELD + ") not set to true on " + inMessage,
                   inMessage.getHeader().getBoolean(quickfix.field.PossDupFlag.FIELD));
    }
    protected void verifyStatusNew(Message inOrder,
                                   ReportBase inReport)
            throws Exception
    {
        assertEquals(OrderStatus.New,
                     inReport.getOrderStatus());
        verifyOrderId(inOrder,
                      inReport);
    }
    protected void verifyStatusNew(Message inOrder,
                                   Message inReport)
            throws Exception
    {
        assertEquals(OrderStatus.New,
                     OrderStatus.getInstanceForFIXMessage(inReport));
        verifyOrderId(inOrder,
                      inReport);
    }
    /**
     * Verify that the given execution report for the given order has canceled status.
     *
     * @param inOrder a <code>Message</code> value
     * @param inReport a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyStatusCanceled(Message inOrder,
                                        Message inReport)
            throws Exception
    {
        assertEquals(OrderStatus.Canceled,
                     OrderStatus.getInstanceForFIXMessage(inReport));
        verifyOrderId(inOrder,
                      inReport);
    }
    /**
     * Verify that the given execution report for the given order has rejected status.
     *
     * @param inOrder a <code>Message</code> value
     * @param inReport a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyStatusRejected(Message inOrder,
                                        Message inReport)
            throws Exception
    {
        assertEquals(OrderStatus.Rejected,
                     OrderStatus.getInstanceForFIXMessage(inReport));
        verifyOrderId(inOrder,
                      inReport);
    }
    protected void verifyStatusPartiallyFilled(Message inOrder,
                                               Message inReport)
            throws Exception
    {
        assertEquals(OrderStatus.PartiallyFilled,
                     OrderStatus.getInstanceForFIXMessage(inReport));
        verifyOrderId(inOrder,
                      inReport);
    }
    /**
     * Verify that the given session is disabled.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionDisabled(final BrokerID inBrokerId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                BrokerStatus status = brokerService.getBrokerStatus(inBrokerId);
                if(status == null) {
                    return false;
                }
                return !status.getStatus().isEnabled();
            }
        });
    }
    /**
     * Verify that the given session is deleted.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionDeleted(final BrokerID inBrokerId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                BrokerStatus status = brokerService.getBrokerStatus(inBrokerId);
                return status == null || status.getStatus() == FixSessionStatus.DELETED;
            }
        });
    }
    /**
     * Verify that the given session is logged on.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionLoggedOn(final BrokerID inBrokerId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                BrokerStatus status = brokerService.getBrokerStatus(inBrokerId);
                if(status == null) {
                    return false;
                }
                return status.getLoggedOn();
            }
        });
    }
    /**
     * Verify that the given session is logged on.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionLoggedOn(final SessionID inSessionId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                Session session = Session.lookupSession(inSessionId);
                if(session == null) {
                    return false;
                }
                if(!session.isLoggedOn()) {
                    return false;
                }
                if(DateTime.now().isBefore(new DateTime(session.getStartTime()).plusSeconds(2))) {
                    return false;
                }
                return true;
            }
        });
    }
    /**
     * Verify that the given session is logged off.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionLoggedOff(final SessionID inSessionId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                Session session = Session.lookupSession(inSessionId);
                return session == null || !session.isLoggedOn();
            }
        });
    }
    /**
     * Verify that the given session is logged off.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionLoggedOff(final BrokerID inBrokerId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                BrokerStatus status = brokerService.getBrokerStatus(inBrokerId);
                if(status == null) {
                    return false;
                }
                return !status.getLoggedOn();
            }
        });
    }
    /**
     * Verify that the given session is enabled.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionEnabled(final BrokerID inBrokerId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                BrokerStatus status = brokerService.getBrokerStatus(inBrokerId);
                if(status == null) {
                    return false;
                }
                return status.getStatus().isEnabled();
            }
        });
    }
    /**
     * Verifies that the given broker matches the given status.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inExpectedStatus a <code>Boolean</code> value or <code>null</code>
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyBrokerStatus(BrokerID inBrokerId,
                                      Boolean inExpectedStatus)
            throws Exception
    {
        BrokerStatus brokerStatus = brokerService.getBrokerStatus(inBrokerId);
        if(inExpectedStatus == null) {
            assertNull(brokerStatus);
        } else {
            assertEquals(inExpectedStatus,
                         brokerStatus.getLoggedOn());
        }
    }
    /**
     * Generates a message with the given comma-separated fields.
     *
     * @param inHeaderFields a <code>String</code> value
     * @param inFields a <code>String</code> value
     * @param inMsgType a <code>String</code> value
     * @param inFactory a <code>FIXMessageFactory</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected static Message buildMessage(String inHeaderFields,
                                          String inFields,
                                          String inMsgType,
                                          FIXMessageFactory inFactory)
            throws Exception
    {
        Map<Integer,String> fields = new HashMap<>();
        Message message = inFactory.createMessage(inMsgType);
        String[] pairs = inHeaderFields.split(",");
        if(pairs != null) {
            for(String pair : pairs) {
                pair = StringUtils.trimToNull(pair);
                String[] components = pair.split("=");
                fields.put(Integer.parseInt(components[0]),
                           components[1]);
            }
        }
        for(Map.Entry<Integer,String> entry : fields.entrySet()) {
            message.getHeader().setString(entry.getKey(),
                                          entry.getValue());
        }
        fields.clear();
        pairs = inFields.split(",");
        for(String pair : pairs) {
            pair = StringUtils.trimToNull(pair);
            String[] components = pair.split("=");
            fields.put(Integer.parseInt(components[0]),
                       components[1]);
        }
        for(Map.Entry<Integer,String> entry : fields.entrySet()) {
            message.setString(entry.getKey(),
                              entry.getValue());
        }
        return message;
    }
    /**
     * Waits for the next message to be received by the receiver and verifies it is of the given type. 
     *
     * @param inMsgType a <code>String</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected Message waitForAndVerifySenderMessage(SessionID inSessionId,
                                                    String inMsgType)
            throws Exception
    {
        long start = System.currentTimeMillis();
        Message senderMessage = null;
        while(senderMessage == null && System.currentTimeMillis()<(start+waitPeriod)) {
            senderMessage = receiver.getNextApplicationMessage(inSessionId);
            Thread.sleep(100);
        }
        assertNotNull("No application message received for " + inSessionId + " in " + waitPeriod + "ms",
                      senderMessage);
        assertEquals(inMsgType,
                     senderMessage.getHeader().getString(MsgType.FIELD));
        return senderMessage;
    }
    /**
     * Waits for the next message to be received by the sender and verifies it is of the given type.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inMsgType a <code>String</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected Message waitForAndVerifyReceiverMessage(SessionID inSessionId,
                                                      String inMsgType)
            throws Exception
    {
        long start = System.currentTimeMillis();
        Message receiverMessage = null;
        SessionID reversedSessionId = FIXMessageUtil.getReversedSessionId(inSessionId);
        Sender sender = senders.get(reversedSessionId);
        Validate.notNull(sender,
                         "No sender for " + inSessionId + " in " + senders.keySet());
        while(receiverMessage == null && System.currentTimeMillis()<(start+waitPeriod)) {
            receiverMessage = sender.getNextApplicationMessage(inSessionId);
            Thread.sleep(100);
        }
        assertNotNull("No application message received for " + inSessionId + " in " + waitPeriod + "ms",
                      receiverMessage);
        assertEquals(inMsgType,
                     receiverMessage.getHeader().getString(MsgType.FIELD));
        return receiverMessage;
    }
    /**
     * Waits for the next message to be received by the sender and verifies it is of the given type. 
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inMsgType a <code>String</code> value
     * @return a <code>Future&lt;Message&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected Future<Message> waitForAndVerifyReceiverMessageAsync(final SessionID inSessionId,
                                                                   final String inMsgType)
            throws Exception
    {
        return asyncExecutorService.submit(new Callable<Message>() {
            @Override
            public Message call()
                    throws Exception
            {
                long start = System.currentTimeMillis();
                Message receiverMessage = null;
                SessionID reversedSessionId = FIXMessageUtil.getReversedSessionId(inSessionId);
                Sender sender = senders.get(reversedSessionId);
                Validate.notNull(sender,
                                 "No sender for " + inSessionId + " in " + senders.keySet());
                while(receiverMessage == null && System.currentTimeMillis()<(start+waitPeriod)) {
                    receiverMessage = sender.getNextApplicationMessage(inSessionId);
                    Thread.sleep(100);
                }
                assertNotNull("No application message received for " + inSessionId + " in " + waitPeriod + "ms",
                              receiverMessage);
                assertEquals(inMsgType,
                             receiverMessage.getHeader().getString(MsgType.FIELD));
                return receiverMessage;
            }
        });
    }
    /**
     * Generate an execution report based on the given inputs.
     *
     * @param inOrder a <code>Message</code> value
     * @param inOrderData an <code>OrderData</code> value
     * @param inOrderId a <code>String</code> value
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @param inExecutionType an <code>ExecutionType</code> value
     * @param inFactory a <code>FIXMessageFactory</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected static Message generateExecutionReport(Message inOrder,
                                                     OrderData inOrderData,
                                                     String inOrderId,
                                                     org.marketcetera.trade.OrderStatus inOrderStatus,
                                                     ExecutionType inExecutionType,
                                                     FIXMessageFactory inFactory)
            throws Exception
    {
        return generateExecutionReport(inOrder,
                                       inOrderData,
                                       inOrderId,
                                       inOrder.getString(quickfix.field.ClOrdID.FIELD),
                                       inOrder.isSetField(quickfix.field.OrigClOrdID.FIELD)?inOrder.getString(quickfix.field.OrigClOrdID.FIELD):null,
                                       inOrderStatus,
                                       inExecutionType,
                                       inFactory);
    }
    /**
     * 
     *
     *
     * @param inOrder
     * @param inPriceQtyInfo
     * @param inClOrdId
     * @param inOrigClOrdId
     * @param inOrderId
     * @param inOrderStatus
     * @param inExecutionType
     * @param inFactory
     * @return
     * @throws Exception
     */
    protected static Message generateExecutionReport(Message inOrder,
                                                     OrderData inPriceQtyInfo,
                                                     String inOrderId,
                                                     String inClOrdId,
                                                     String inOrigClOrdId,
                                                     org.marketcetera.trade.OrderStatus inOrderStatus,
                                                     ExecutionType inExecutionType,
                                                     FIXMessageFactory inFactory)
            throws Exception
    {
        boolean commaNeeded = false;
        StringBuilder body = new StringBuilder();
        if(inOrder.isSetField(quickfix.field.Account.FIELD)) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.Account.FIELD).append('=').append(inOrder.getString(quickfix.field.Account.FIELD)); commaNeeded = true;
        }
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.AvgPx.FIELD).append('=').append(inPriceQtyInfo.calculateAveragePrice().toPlainString()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.ClOrdID.FIELD).append('=').append(inClOrdId); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.CumQty.FIELD).append('=').append(inPriceQtyInfo.calculateCumQty().toPlainString()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.ExecID.FIELD).append('=').append(generateId()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.ExecTransType.FIELD).append('=').append(quickfix.field.ExecTransType.NEW); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.LastPx.FIELD).append('=').append(inPriceQtyInfo.calculateLastPx().toPlainString()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.LastQty.FIELD).append('=').append(inPriceQtyInfo.calculateLastQty().toPlainString()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrderID.FIELD).append('=').append(inOrderId); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrderQty.FIELD).append('=').append(inOrder.getDecimal(quickfix.field.OrderQty.FIELD).toPlainString()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrdStatus.FIELD).append('=').append(inOrderStatus.getFIXValue()); commaNeeded = true;
        if(inOrder.isSetField(quickfix.field.OrdType.FIELD)) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrdType.FIELD).append('=').append(inOrder.getChar(quickfix.field.OrdType.FIELD)); commaNeeded = true;
        }
        if(inOrigClOrdId != null) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrigClOrdID.FIELD).append('=').append(inOrigClOrdId); commaNeeded = true;
        }
        if(inOrder.isSetField(quickfix.field.Price.FIELD)) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.Price.FIELD).append('=').append(inOrder.getDecimal(quickfix.field.Price.FIELD).toPlainString()); commaNeeded = true;
        }
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.Side.FIELD).append('=').append(inOrder.getChar(quickfix.field.Side.FIELD)); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.Symbol.FIELD).append('=').append(inOrder.getString(quickfix.field.Symbol.FIELD)); commaNeeded = true;
        if(inOrder.isSetField(quickfix.field.Text.FIELD)) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.Text.FIELD).append('=').append(inOrder.getString(quickfix.field.Text.FIELD)); commaNeeded = true;
        }
        if(inOrder.isSetField(quickfix.field.TimeInForce.FIELD)) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.TimeInForce.FIELD).append('=').append(inOrder.getChar(quickfix.field.TimeInForce.FIELD)); commaNeeded = true;
        }
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.TransactTime.FIELD).append('=').append(TimeFactoryImpl.FULL_MILLISECONDS.print(System.currentTimeMillis())); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.ExecType.FIELD).append('=').append(inExecutionType.getFIXValue()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.LeavesQty.FIELD).append('=').append(inPriceQtyInfo.calculateLeavesQty().toPlainString()); commaNeeded = true;
        return buildMessage(MsgType.FIELD+"="+MsgType.EXECUTION_REPORT,
                            body.toString(),
                            MsgType.EXECUTION_REPORT,
                            inFactory);
    }
    protected static Message generateOrderCancelReject(Message inMessage,
                                                       OrderData inPriceQtyInfo,
                                                       String inOrderId,
                                                       String inClOrdId,
                                                       String inOrigClOrdId,
                                                       org.marketcetera.trade.OrderStatus inOrderStatus,
                                                       FIXMessageFactory inFactory)
            throws Exception
    {
        boolean commaNeeded = false;
        StringBuilder body = new StringBuilder();
        if(inMessage.isSetField(quickfix.field.Account.FIELD)) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.Account.FIELD).append('=').append(inMessage.getString(quickfix.field.Account.FIELD)); commaNeeded = true;
        }
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.ClOrdID.FIELD).append('=').append(inClOrdId); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrderID.FIELD).append('=').append(inOrderId); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrdStatus.FIELD).append('=').append(inOrderStatus.getFIXValue()); commaNeeded = true;
        if(inOrigClOrdId != null) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrigClOrdID.FIELD).append('=').append(inOrigClOrdId); commaNeeded = true;
        }
        if(inMessage.isSetField(quickfix.field.Text.FIELD)) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.Text.FIELD).append('=').append(inMessage.getString(quickfix.field.Text.FIELD)); commaNeeded = true;
        }
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.TransactTime.FIELD).append('=').append(TimeFactoryImpl.FULL_MILLISECONDS.print(System.currentTimeMillis())); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.CxlRejResponseTo.FIELD).append('=').append(inMessage.getHeader().getString(quickfix.field.MsgType.FIELD).equals(quickfix.field.MsgType.ORDER_CANCEL_REPLACE_REQUEST)?quickfix.field.CxlRejResponseTo.ORDER_CANCEL_REPLACE_REQUEST:quickfix.field.CxlRejResponseTo.ORDER_CANCEL_REQUEST); commaNeeded = true;
        return buildMessage(MsgType.FIELD+"="+MsgType.ORDER_CANCEL_REJECT,
                            body.toString(),
                            MsgType.ORDER_CANCEL_REJECT,
                            inFactory);
    }
    public class CalculatedOrderData
    {
        /**
         * Add a price/qty pair.
         *
         * @param inPrice a <code>BigDecimal</code> value
         * @param inQty a <code>BigDecimal</code> value
         */
        public void add(BigDecimal inPrice,
                        BigDecimal inQty)
        {
            tuples.add(new PriceQtyTuple(inPrice,inQty));
        }
        /**
         * 
         *
         *
         * @param inExecution
         * @throws Exception
         */
        public void addExecution(Message inExecution)
                throws Exception
        {
            if(inExecution.isSetField(quickfix.field.LastPx.FIELD) && inExecution.isSetField(quickfix.field.LastQty.FIELD)) {
                add(inExecution.getDecimal(quickfix.field.LastPx.FIELD),
                    inExecution.getDecimal(quickfix.field.LastQty.FIELD));
            }
        }
        /**
         * Calculate the average price from the collected quantities.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal calculateAveragePrice()
        {
            BigDecimal result = BigDecimal.ZERO;
            BigDecimal totalQty = BigDecimal.ZERO;
            BigDecimal extendedQty = BigDecimal.ZERO;
            for(PriceQtyTuple amount : tuples) {
                totalQty = totalQty.add(amount.getQty());
                extendedQty = extendedQty.add(amount.getPrice().multiply(amount.getQty()));
            }
            if(totalQty.compareTo(BigDecimal.ZERO) != 0) {
                result = extendedQty.divide(totalQty,
                                            divisionContext);
            }
            return result;
        }
        /**
         * Calculate the cumulative quantity from the collected quantities.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal calculateCumQty()
        {
            BigDecimal result = BigDecimal.ZERO;
            for(PriceQtyTuple amount : tuples) {
                result = result.add(amount.getQty());
            }
            return result;
        }
        /**
         * Calculate the leaves quantity from the order data.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal calculateLeavesQty()
        {
            return orderQuantity.subtract(calculateCumQty());
        }
        /**
         * Calculate the last price from the collected quantities.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal calculateLastPx()
        {
            BigDecimal result = BigDecimal.ZERO;
            if(!tuples.isEmpty()) {
                result = tuples.get(tuples.size()-1).getPrice();
            }
            return result;
        }
        /**
         * Calculate the last qty from the collected quantities.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal calculateLastQty()
        {
            BigDecimal result = BigDecimal.ZERO;
            if(!tuples.isEmpty()) {
                result = tuples.get(tuples.size()-1).getQty();
            }
            return result;
        }
        /**
         * Get the orderPrice value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getOrderPrice()
        {
            return orderPrice;
        }
        /**
         * Sets the orderPrice value.
         *
         * @param inOrderPrice a <code>BigDecimal</code> value
         */
        public void setOrderPrice(BigDecimal inOrderPrice)
        {
            orderPrice = inOrderPrice;
        }
        /**
         * Get the orderQuantity value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getOrderQuantity()
        {
            return orderQuantity;
        }
        /**
         * Sets the orderQuantity value.
         *
         * @param inOrderQuantity a <code>BigDecimal</code> value
         */
        public void setOrderQuantity(BigDecimal inOrderQuantity)
        {
            orderQuantity = inOrderQuantity;
        }
        /**
         * Generate an order status request for the given order sent from the given session.
         *
         * @param inOrderMessage a <code>Message</code> value
         * @param inSessionId a <code>SessionID</code> value
         * @return a <code>Message</code> value
         */
        public Message generateOrderStatusRequest(Message inOrderMessage,
                                                  SessionID inSessionId)
                throws Exception
        {
            FIXVersion version = FIXVersion.getFIXVersion(inOrderMessage);
            FIXMessageFactory factory = version.getMessageFactory();
            Message order = factory.createMessage(quickfix.field.MsgType.ORDER_STATUS_REQUEST);
            FIXMessageUtil.fillFieldsFromExistingMessage(order,
                                                         inOrderMessage,
                                                         FIXMessageUtil.getDataDictionary(version),
                                                         false);
            orderMessages.add(order);
            return order;
        }
        public Message generateOrderCancel(Message inOrderMessage,
                                           SessionID inSessionId)
                throws Exception
        {
            FIXVersion version = FIXVersion.getFIXVersion(inSessionId.getBeginString());
            FIXMessageFactory factory = version.getMessageFactory();
            Message order = factory.createMessage(quickfix.field.MsgType.ORDER_CANCEL_REQUEST);
            FIXMessageUtil.fillFieldsFromExistingMessage(order,
                                                         inOrderMessage,
                                                         FIXMessageUtil.getDataDictionary(version),
                                                         false);
            order.setField(new quickfix.field.OrigClOrdID(inOrderMessage.getString(quickfix.field.ClOrdID.FIELD)));
            order.setField(new quickfix.field.ClOrdID(generateId()));
            orderMessages.add(order);
            return order;
        }
        public Message generateOrderReplace(Message inOrderMessage,
                                            SessionID inSessionId)
                throws Exception
        {
            FIXVersion version = FIXVersion.getFIXVersion(inSessionId);
            FIXMessageFactory factory = version.getMessageFactory();
            Message order = factory.createMessage(quickfix.field.MsgType.ORDER_CANCEL_REPLACE_REQUEST);
            FIXMessageUtil.fillFieldsFromExistingMessage(order,
                                                         inOrderMessage,
                                                         FIXMessageUtil.getDataDictionary(version),
                                                         false);
            order.setField(new quickfix.field.OrigClOrdID(inOrderMessage.getString(quickfix.field.ClOrdID.FIELD)));
            order.setField(new quickfix.field.ClOrdID(generateId()));
            orderMessages.add(order);
            return order;
        }
        /**
         * Generate an order with the given instrument targeted to the given session.
         *
         * @param inInstrument an <code>Instrument</code> value
         * @param inSenderSessionId a <code>SessionID</code> value
         * @return a <code>Message</code> value
         * @throws Exception if an unexpected error occurs
         */
        public Message generateOrder(Instrument inInstrument,
                                     SessionID inSenderSessionId)
                throws Exception
        {
            FIXVersion version = FIXVersion.getFIXVersion(inSenderSessionId);
            FIXMessageFactory factory = version.getMessageFactory();
            String account = generateId();
            String clOrdId = generateId();
            StringBuilder body = new StringBuilder();
            body.append(quickfix.field.Account.FIELD).append('=').append(account).append(',');
            body.append(quickfix.field.ClOrdID.FIELD).append('=').append(clOrdId).append(',');
            body.append(quickfix.field.HandlInst.FIELD).append('=').append(quickfix.field.HandlInst.MANUAL_ORDER).append(',');
            body.append(quickfix.field.OrderQty.FIELD).append('=').append(orderQuantity.toPlainString()).append(',');
            if(orderType != null) {
                body.append(quickfix.field.OrdType.FIELD).append('=').append(orderType.getFIXValue()).append(',');
            }
            if(orderPrice != null) {
                body.append(quickfix.field.Price.FIELD).append('=').append(orderPrice.toPlainString()).append(',');
            }
            body.append(quickfix.field.Side.FIELD).append('=').append(side.getFIXValue()).append(',');
            Message order = buildMessage("35="+MsgType.ORDER_SINGLE,
                                         body.toString(),
                                         MsgType.ORDER_SINGLE,
                                         factory);
            order.setField(new quickfix.field.TransactTime(new Date()));
            InstrumentToMessage<?> instrumentFunction = InstrumentToMessage.SELECTOR.forInstrument(inInstrument);
            DataDictionary fixDictionary = FIXMessageUtil.getDataDictionary(inSenderSessionId);
            if(!instrumentFunction.isSupported(fixDictionary,
                                               quickfix.field.MsgType.ORDER_SINGLE)) {
                throw new I18NException(Messages.UNSUPPORTED_INSTRUMENT);
            }
            instrumentFunction.set(inInstrument,
                                   fixDictionary,
                                   quickfix.field.MsgType.ORDER_SINGLE,
                                   order);
            orderMessages.add(order);
            return order;
        }
        /**
         * Create a new CalculatedOrderData instance.
         *
         * @param inOrderQuantity a <code>BigDecimal</code> value
         * @param inOrderPrice a <code>BigDecimal</code> value or <code>null</code>
         * @param inOrderType an <code>OrderType</code> value
         * @param inSide a <code>Side</code> value
         */
        public CalculatedOrderData(BigDecimal inOrderQuantity,
                                   BigDecimal inOrderPrice,
                                   OrderType inOrderType,
                                   Side inSide)
        {
            orderQuantity = inOrderQuantity;
            orderPrice = inOrderPrice;
            orderType = inOrderType;
            side = inSide;
        }
        /**
         * collection of price/qty tuples
         */
        private final List<PriceQtyTuple> tuples = new ArrayList<>();
        /**
         * order quantity value
         */
        protected BigDecimal orderQuantity;
        /**
         * 
         */
        protected BigDecimal orderPrice;
        protected String orderId;
        protected final OrderType orderType;
        protected final Side side;
        protected final Deque<Message> orderMessages = Lists.newLinkedList();
        protected final List<Message> executionMessages = Lists.newArrayList();
    }
    /**
     * Tracks a list of price/qty execution pairs for an order.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public class OrderData
            extends CalculatedOrderData
    {
        /**
         * Create a new OrderData instance.
         *
         * @param inOrderQuantity a <code>BigDecimal</code> value
         * @param inOrderPrice a <code>BigDecimal</code> value
         * @param inOrderType an <code>OrderType</code> value
         * @param inSide a <code>Side</code> value
         * @param inSessionIndex an <code>int</code> value
         */
        public OrderData(BigDecimal inOrderQuantity,
                         BigDecimal inOrderPrice,
                         OrderType inOrderType,
                         Side inSide,
                         int inSessionIndex)
        {
            this(inOrderQuantity,
                 inOrderPrice,
                 inOrderType,
                 inSide,
                 inSessionIndex,
                 inSessionIndex);
        }
        public OrderData(BigDecimal inOrderQuantity,
                         BigDecimal inOrderPrice,
                         OrderType inOrderType,
                         Side inSide,
                         int inAcceptorSessionIndex,
                         int inInitiatorSessionIndex)
        {
            super(inOrderQuantity,
                  inOrderPrice,
                  inOrderType,
                  inSide);
            acceptorSessionIndex = inAcceptorSessionIndex;
            initiatorSessionIndex = inInitiatorSessionIndex;
            senderSessionId = remoteSenderSessions.get(acceptorSessionIndex);
            acceptorSessionId = FIXMessageUtil.getReversedSessionId(senderSessionId);
            Validate.notNull(senderSessionId,
                             "No remote sender session for session " + senderBase+inAcceptorSessionIndex);
            receiverSessionId = remoteReceiverSessions.get(initiatorSessionIndex);
            initiatorSessionId = FIXMessageUtil.getReversedSessionId(receiverSessionId);
            Validate.notNull(receiverSessionId,
                             "No remote receiver session for session " + receiverBase+inAcceptorSessionIndex);
        }
        /**
         * Generate and send a partial fill based on the given order message.
         *
         * @param inMessage a <code>Message</code> value
         * @return a <code>Message</code> value
         * @throws Exception if an unexpected error occurs
         */
        public Message generateAndSendPartialFill(Message inMessage)
                throws Exception
        {
            if(orderId == null) {
                orderId = generateId();
            }
            BigDecimal leavesQty = calculateLeavesQty();
            // generate a fill qty of 1-10 100 lots
            BigDecimal fillQty = new BigDecimal((random.nextInt(10)+1)*100);
            assertTrue("Not enough qty left to generate a partial fill",
                       leavesQty.compareTo(fillQty) == 1);
            BigDecimal fillPrice = orderPrice==null?generateDecimalValue():orderPrice;
            if(side.isBuy()) {
                fillPrice = fillPrice.subtract(new BigDecimal(0.01*random.nextInt(10)));
            } else {
                fillPrice = fillPrice.add(new BigDecimal(0.01*random.nextInt(10)));
            }
            fillPrice = fillPrice.round(divisionContext);
            add(fillPrice,fillQty);
            Message fill = generateAndSendReport(inMessage,
                                                 OrderStatus.PartiallyFilled,
                                                 ExecutionType.PartialFill);
            return fill;
        }
        public Message generateAndSendOrderCanceReject(Message inMessage,
                                                       OrderStatus inOrderStatus)
                throws Exception
        {
            FIXVersion version = FIXVersion.getFIXVersion(receiverSessionId.getBeginString());
            FIXMessageFactory factory = version.getMessageFactory();
            if(orderId == null) {
                orderId = generateId();
            }
            Message report = generateOrderCancelReject(orderMessages.getLast(),
                                                       this,
                                                       orderId,
                                                       inMessage.isSetField(quickfix.field.ClOrdID.FIELD)?inMessage.getString(quickfix.field.ClOrdID.FIELD):null,
                                                       inMessage.isSetField(quickfix.field.OrigClOrdID.FIELD)?inMessage.getString(quickfix.field.OrigClOrdID.FIELD):null,
                                                       inOrderStatus,
                                                       factory);
            executionMessages.add(report);
            Session.sendToTarget(report,
                                 receiverSessionId);
            return report;
        }
        /**
         * 
         *
         *
         * @param inMessage
         * @return
         * @throws Exception
         */
        public Message generateAndSendReplaceAck(Message inMessage)
                throws Exception
        {
            return generateAndSendReport(inMessage,
                                      OrderStatus.Replaced,
                                      ExecutionType.Replace);
        }
        /**
         * 
         *
         *
         * @param inMessage
         * @param inOrderStatus
         * @param inExecutionType
         * @return
         * @throws Exception
         */
        public Message generateAndSendReport(Message inMessage,
                                             OrderStatus inOrderStatus,
                                             ExecutionType inExecutionType)
                throws Exception
        {
            FIXVersion version = FIXVersion.getFIXVersion(receiverSessionId.getBeginString());
            FIXMessageFactory factory = version.getMessageFactory();
            if(orderId == null) {
                orderId = generateId();
            }
            Message report = generateExecutionReport(orderMessages.getLast(),
                                                     this,
                                                     orderId,
                                                     inMessage.isSetField(quickfix.field.ClOrdID.FIELD)?inMessage.getString(quickfix.field.ClOrdID.FIELD):null,
                                                     inMessage.isSetField(quickfix.field.OrigClOrdID.FIELD)?inMessage.getString(quickfix.field.OrigClOrdID.FIELD):null,
                                                     inOrderStatus,
                                                     inExecutionType,
                                                     factory);
            executionMessages.add(report);
            Session.sendToTarget(report,
                                 receiverSessionId);
            return report;
        }
        /**
         * 
         *
         *
         * @param inMessage
         * @param inOrderStatus
         * @param inExecutionType
         * @return
         * @throws Exception
         */
        public Message generateAndSendAck(Message inMessage)
                throws Exception
        {
            return generateAndSendReport(inMessage,
                                         OrderStatus.New,
                                         ExecutionType.New);
        }
        /**
         * 
         *
         *
         * @param inMessage
         * @param inOrderStatus
         * @param inExecutionType
         * @return
         * @throws Exception
         */
        public Message generateAndSendCancelAck(Message inMessage)
                throws Exception
        {
            return generateAndSendReport(inMessage,
                                         OrderStatus.Canceled,
                                         ExecutionType.Canceled);
        }
        /**
         * 
         *
         *
         * @param inMessage
         * @param inOrderPrice
         * @param inOrderQuantity
         * @return
         * @throws Exception
         */
        public Message generateAndSendReplace(Message inMessage,
                                              BigDecimal inOrderPrice,
                                              BigDecimal inOrderQuantity)
                throws Exception
        {
            FIXVersion version = FIXVersion.getFIXVersion(senderSessionId.getBeginString());
            FIXMessageFactory factory = version.getMessageFactory();
            Message replace = factory.createMessage(quickfix.field.MsgType.ORDER_CANCEL_REPLACE_REQUEST);
            FIXMessageUtil.copyFields(replace,
                                      inMessage);
            replace.setField(new quickfix.field.OrigClOrdID(inMessage.getString(quickfix.field.ClOrdID.FIELD)));
            replace.setField(new quickfix.field.ClOrdID(generateId()));
            if(inOrderPrice != null) {
                replace.setField(new quickfix.field.Price(inOrderPrice));
            }
            orderPrice = inOrderPrice;
            replace.setField(new quickfix.field.OrderQty(inOrderQuantity));
            orderQuantity = inOrderQuantity;
            orderMessages.add(replace);
            Session.sendToTarget(replace,
                                 senderSessionId);
            return replace;
        }
        /**
         *
         *
         * @param inMessage
         * @return
         * @throws Exception
         */
        public Message generateAndSendCancel(Message inMessage)
                throws Exception
        {
            FIXVersion version = FIXVersion.getFIXVersion(senderSessionId.getBeginString());
            FIXMessageFactory factory = version.getMessageFactory();
            Message cancel = factory.createMessage(quickfix.field.MsgType.ORDER_CANCEL_REQUEST);
            FIXMessageUtil.copyFields(cancel,
                                      inMessage);
            cancel.setField(new quickfix.field.OrigClOrdID(inMessage.getString(quickfix.field.ClOrdID.FIELD)));
            cancel.setField(new quickfix.field.ClOrdID(generateId()));
            cancel.removeField(quickfix.field.HandlInst.FIELD);
            cancel.removeField(quickfix.field.OrdType.FIELD);
            cancel.removeField(quickfix.field.Price.FIELD);
            orderMessages.add(cancel);
            Session.sendToTarget(cancel,
                                 senderSessionId);
            return cancel;
        }
        /**
         * 
         *
         * @param inInstrument
         * @return
         * @throws Exception
         */
        public Message generateOrder(Instrument inInstrument)
                throws Exception
        {
            return super.generateOrder(inInstrument,
                                       senderSessionId);
        }
        /**
         * Generate an order using the given instrument.
         * 
         * @param inInstrument an <code>Instrument</code> value
         * @return a <code>Message</code> value
         * @throws Exception if an unexpected error occurs
         */
        public Message generateAndSendOrder(Instrument inInstrument)
                throws Exception
        {
            Message order = generateOrder(inInstrument);
            sendOrder(order);
            return order;
        }
        /**
         * Send the given order to the sender session.
         *
         * @param inOrder a <code>Message</code> value
         * @return a <code>boolean</code> value indicating if the order was queued and sent or not
         * @throws Exception if an unexpected error occurs
         */
        public boolean sendOrder(Message inOrder)
                throws Exception
        {
            return Session.sendToTarget(inOrder,
                                        senderSessionId);
        }
        /**
         *
         *
         * @param inMessage a <code>Message</code> value
         * @return a <code>Message</code> value
         * @throws Exception if an unexpected error occurs
         */
        public Message waitForAndVerifyReceiverMessage(Message inMessage)
                throws Exception
        {
            return MarketceteraTestBase.this.waitForAndVerifyReceiverMessage(acceptorSessionId,
                                                                             inMessage.getHeader().getString(MsgType.FIELD));
        }
        /**
         * Wait for a receiver message of the given type.
         *
         * @param inMsgType a <code>String</code> value
         * @return a <code>Message</code> value
         * @throws Exception if an unexpected error occurs
         */
        public Message waitForReceiverMessage(String inMsgType)
                throws Exception
        {
            return MarketceteraTestBase.this.waitForAndVerifyReceiverMessage(acceptorSessionId,
                                                                             inMsgType);
        }
        /**
         * Wait for a sender message of the given type.
         *
         * @param inMsgType a <code>String</code> value
         * @return a <code>Message</code> value
         * @throws Exception if an unexpected error occurs
         */
        public Message waitForSenderMessage(String inMsgType)
                throws Exception
        {
            return MarketceteraTestBase.this.waitForAndVerifySenderMessage(initiatorSessionId,
                                                                           inMsgType);
        }
        /**
         *
         *
         * @param inSenderMessage
         * @return a <code>Message</code> value
         * @throws Exception
         */
        public Message waitForAndVerifySenderMessage(Message inSenderMessage)
                throws Exception
        {
            return MarketceteraTestBase.this.waitForAndVerifySenderMessage(initiatorSessionId,
                                                                           inSenderMessage.getHeader().getString(MsgType.FIELD));
        }
        private final int acceptorSessionIndex;
        private final int initiatorSessionIndex;
        private final SessionID senderSessionId;
        private final SessionID receiverSessionId;
        private final SessionID acceptorSessionId;
        private final SessionID initiatorSessionId;
    }
    /**
     * listens for reports
     */
    private TradeMessageListener TradeMessageListener;
    /**
     * stores reports received from the client
     */
    protected final Deque<TradeMessage> reports = Lists.newLinkedList();
    /**
     * provides access to client trading services
     */
    @Autowired(required=false)
    protected TradeClient tradingClient;
    /**
     * provides fix settings
     */
    protected FixSettingsProvider fixSettingsProvider;
    /**
     * creates fix settings provider values
     */
    @Autowired
    protected FixSettingsProviderFactory fixSettingsProviderFactory;
    /**
     * receives messages sent from the test initiator sessions
     */
    private Receiver receiver;
    /**
     * sender sessions created during test
     */
    private final Map<SessionID,Sender> senders = Maps.newHashMap();
    /**
     * holds session ids of remote sender sessions by index
     */
    private static final Map<Integer,SessionID> remoteSenderSessions = Maps.newHashMap();
    /**
     * holds session ids of remote receiver sessions by index
     */
    private static final Map<Integer,SessionID> remoteReceiverSessions = Maps.newHashMap();
    /**
     * message factory value
     */
    @Autowired
    protected MessageFactory messageFactory;
    /**
     * holds remote acceptor sessions
     */
    private final Collection<FixSession> remoteAcceptorSessions = new ArrayList<>();
    /**
     * creates fix sessions
     */
    @Autowired
    protected FixSessionFactory fixSessionFactory;
    /**
     * provides access to the exchange report data store
     */
    @Autowired
    protected ExecutionReportDao executionReportDao;
    /**
     * provides access to the incoming message data store
     */
    protected IncomingMessageDao incomingMessageDao;
    /**
     * provides access to the report data store
     */
    @Autowired
    protected PersistentReportDao reportDao;
    /**
     * provides access to the permission data store
     */
    @Autowired
    protected PersistentPermissionDao permissionDao;
    /**
     * provides access to order order summary services
     */
    @Autowired
    protected OrderSummaryService orderSummaryService;
    /**
     * provides access to the order status data store
     */
    @Autowired
    protected OrderSummaryDao orderStatusDao;
    /**
     * provides access to broker services
     */
    @Autowired
    protected BrokerService brokerService;
    /**
     * provides access to authorization services
     */
    @Autowired
    protected AuthorizationService authorizationService;
    /**
     * provides access to report services
     */
    @Autowired(required=false)
    protected ReportService reportService;
    /**
     * provides access to user services
     */
    @Autowired
    protected UserService userService;
    /**
     * application context value
     */
    @Autowired
    protected ApplicationContext applicationContext;
    /**
     * 
     */
    protected long waitPeriod = 10000;
    protected Instrument instrument;
    /**
     * port on which host systems will listen for FIX connections
     */
    protected int hostAcceptorPort;
    /**
     * port on which remote systems will listen for FIX connections
     */
    protected int remoteAcceptorPort;
    protected static final String senderBase = "SENDER";
    protected static final String receiverBase = "RECEIVER";
    protected static final String defaultHostBase = "MATP";
    protected static final FIXVersion defaultFixVersion = FIXVersion.FIX42;
    /**
     * manages asynchronous tasks
     */
    private ExecutorService asyncExecutorService;
    /**
     * generates random values
     */
    protected static final Random random = new SecureRandom();
    /**
     * used to guarantee unique values
     */
    protected static final AtomicInteger counter = new AtomicInteger(0);
    /**
     * test artifact used to identify the current test case
     */
    @Rule
    public TestName name = new TestName();
    /**
     * rule used to load test context
     */
    @ClassRule
    public static final SpringClassRule SCR = new SpringClassRule();
    /**
     * test spring method rule
     */
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
}
