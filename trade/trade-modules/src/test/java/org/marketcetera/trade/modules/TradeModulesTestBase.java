package org.marketcetera.trade.modules;

import static org.junit.Assert.assertEquals;

import java.util.Deque;
import java.util.List;

import org.junit.Before;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.modules.fix.FixDataRequest;
import org.marketcetera.modules.fix.FixInitiatorModuleFactory;
import org.marketcetera.trade.service.TradeTestBase;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides common behavior for trade modules tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class TradeModulesTestBase
        extends TradeTestBase
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
        orderConverterModuleUrn = OrderConverterModuleFactory.INSTANCE_URN;
        tradeMessageConverterModuleUrn = TradeMessageConverterModuleFactory.INSTANCE_URN;
        super.setup();
    }
    /**
     * Build a data request that includes the order converter and the FIX initiator.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @param inHeadwaterInstance a <code>String</code> value
     * @return a <code>DataRequest[]</code> value
     */
    protected DataRequest[] getFullInitiatorDataSendRequest(FixDataRequest inFixDataRequest,
                                                            String inHeadwaterInstance)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        ModuleURN headwaterUrn = createHeadwaterModule(inHeadwaterInstance);
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(OrderConverterModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(OutgoingMessageCachingModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(OutgoingMessagePersistenceModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(FixInitiatorModuleFactory.INSTANCE_URN,
                                               inFixDataRequest));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Build a data request that includes the order converter and the FIX initiator.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @param inReceivedData a <code>Deque&lt;Object&gt;</code> value
     * @return a <code>DataRequest[]</code> value
     */
    protected DataRequest[] getFullInitiatorReceiveDataRequest(FixDataRequest inFixDataRequest,
                                                               Deque<Object> inReceivedData)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        dataRequestBuilder.add(new DataRequest(FixInitiatorModuleFactory.INSTANCE_URN,
                                               inFixDataRequest));
        dataRequestBuilder.add(new DataRequest(TradeMessageConverterModuleFactory.INSTANCE_URN));
        ModuleURN publisherUrn = createPublisherModule(inReceivedData);
        dataRequestBuilder.add(new DataRequest(publisherUrn));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Build a data request for the order converter module.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @param inHeadwaterInstance a <code>String</code> value
     * @return a <code>DataRequest[]</code> value
     */
    protected DataRequest[] getOrderConverterDataRequest(String inHeadwaterInstance,
                                                         Deque<Object> inReceivedData)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        ModuleURN headwaterUrn = createHeadwaterModule(inHeadwaterInstance);
        ModuleURN publisherUrn = createPublisherModule(inReceivedData);
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(OrderConverterModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(publisherUrn));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Build a data request for the order converter module.
     *
     * @param inFixDataRequest a <code>FixDataRequest</code> value
     * @param inHeadwaterInstance a <code>String</code> value
     * @return a <code>DataRequest[]</code> value
     */
    protected DataRequest[] getTradeMessageConverterDataRequest(String inHeadwaterInstance,
                                                                Deque<Object> inReceivedData)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        ModuleURN headwaterUrn = createHeadwaterModule(inHeadwaterInstance);
        ModuleURN publisherUrn = createPublisherModule(inReceivedData);
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(TradeMessageConverterModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(publisherUrn));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Start the order converter module, if necessary.
     *
     * @throws Exception if an unexpected error occurs
     */
    protected void startModulesIfNecessary()
            throws Exception
    {
        super.startModulesIfNecessary();
        if(!moduleManager.getModuleInfo(orderConverterModuleUrn).getState().isStarted()) {
            moduleManager.start(orderConverterModuleUrn);
            assertEquals(ModuleState.STARTED,
                         moduleManager.getModuleInfo(orderConverterModuleUrn).getState());
        }
        if(!moduleManager.getModuleInfo(tradeMessageConverterModuleUrn).getState().isStarted()) {
            moduleManager.start(tradeMessageConverterModuleUrn);
            assertEquals(ModuleState.STARTED,
                         moduleManager.getModuleInfo(tradeMessageConverterModuleUrn).getState());
        }
    }
    /**
     * Generate a test user.
     *
     * @return a <code>User</code> value
     */
    protected User generateUser()
    {
        User user = userFactory.create("test-user-"+System.nanoTime(),
                                  "password",
                                  "Description of the test user",
                                  true);
        user = userService.save(user);
        return user;
    }
    /**
     * provides access to user services
     */
    @Autowired
    protected UserService userService;
    /**
     * test order converter module
     */
    protected ModuleURN orderConverterModuleUrn;
    /**
     * test trade message converter module
     */
    protected ModuleURN tradeMessageConverterModuleUrn;
    /**
     * creates user objects
     */
    @Autowired
    protected UserFactory userFactory;
}
