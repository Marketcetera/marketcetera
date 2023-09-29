package org.marketcetera.ui.trade.service;

import javax.annotation.PostConstruct;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.ui.service.PhotonSoundService;
import org.marketcetera.ui.service.ServiceManager;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Provides sound services for a specific user related to trades.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@EnableAutoConfiguration
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TradeSoundService
{
    /**
     * Initialize and start the service.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting {}",
                              PlatformServices.getServiceName(getClass()));
        tradeClientService = serviceManager.getService(TradeClientService.class);
        initializeTradeMessageListener();
    }
    /**
     * Create the trade message listener value.
     */
    private void initializeTradeMessageListener()
    {
        tradeMessageListener =  new TradeMessageListener() {
            @Override
            public void receiveTradeMessage(TradeMessage inTradeMessage)
            {
                receive(inTradeMessage);
            }
        };
        tradeClientService.addTradeMessageListener(tradeMessageListener);
    }
    /**
     * Receives a trade message value.
     *
     * @param inTradeMessage a <code>TradeMessage</code> value
     */
    private void receive(TradeMessage inTradeMessage)
    {
        if(inTradeMessage instanceof ExecutionReport) {
            ExecutionReport executionReport = (ExecutionReport)inTradeMessage;
            if(executionReport.getExecutionType().isFill()) {
                if(playTradeSound) {
                    photonSoundService.playSound(tradeSoundFilename);
                }
            } else if(executionReport.getExecutionType().isCancel()) {
                if(playCancelSound) {
                    photonSoundService.playSound(cancelSoundFilename);
                }
            }
        }
    }
    /**
     * indicates whether to play a sound on a fill or not
     */
    @Value("${metc.trade.play.sound:true}")
    private boolean playTradeSound;
    /**
     * file containing sound to play on trade
     */
    @Value("${metc.trade.sound:sounds/ui_chime_tone_simple_003.mp3}")
    private String tradeSoundFilename;
    /**
     * indicates whether to play a sound on a fill or not
     */
    @Value("${metc.cancel.play.sound:true}")
    private boolean playCancelSound;
    /**
     * file containing sound to play on trade
     */
    @Value("${metc.cancel.sound:sounds/ui_prompt_complete_or_soft_error_003.mp3}")
    private String cancelSoundFilename;
    /**
     * provides access to trade services
     */
    private TradeClientService tradeClientService;
    /**
     * listens for trade messages
     */
    private TradeMessageListener tradeMessageListener;
    /**
     * provides access to sound services
     */
    @Autowired
    private PhotonSoundService photonSoundService;
    /**
     * provides access to client services
     */
    @Autowired
    private ServiceManager serviceManager;
}
