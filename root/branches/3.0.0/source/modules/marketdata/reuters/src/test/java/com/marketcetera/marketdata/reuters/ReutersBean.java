package com.marketcetera.marketdata.reuters;

import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleState;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.marketcetera.marketdata.reuters.ReutersFeedModuleFactory;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReutersBean.java 82376 2012-06-07 17:10:13Z colin $
 * @since $Release$
 */
public class ReutersBean
        implements InitializingBean
{
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        try {
            moduleManager.start(ReutersFeedModuleFactory.INSTANCE_URN);
            ModuleState state = moduleManager.getModuleInfo(ReutersFeedModuleFactory.INSTANCE_URN).getState();
            if(state == ModuleState.STARTED) {
                MarketDataRequest request = MarketDataRequestBuilder.newRequest().withSymbols("GOOG").create();
                SLF4JLoggerProxy.debug(ReutersBean.class,
                                       "Request is {}",
                                       request);
                // TODO issue request
            } else {
                SLF4JLoggerProxy.error(ReutersBean.class,
                                       "Reuters feed is {}",
                                       state);
            }
        } catch (ModuleException e) {
            SLF4JLoggerProxy.error(ReutersBean.class,
                                   "Unable to start Reuters adapter");
        }
    }
    /**
     * 
     */
    @Autowired
    private volatile ModuleManager moduleManager;
}
