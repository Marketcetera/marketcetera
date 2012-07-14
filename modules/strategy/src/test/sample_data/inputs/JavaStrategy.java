import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;

import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.util.test.UnicodeData;

public class JavaStrategy
        extends org.marketcetera.strategy.java.Strategy
{
    private int callbackCounter = 0;

    @Override
    public void onStart()
    {
        setProperty("onStartBegins",
                    Long.toString(System.currentTimeMillis()));
        callbackCounter = 0;
        String shouldFail = getParameter("shouldFailOnStart");
        if(shouldFail != null) {
            int x = 10 / 0;
        }
        String shouldLoop = getParameter("shouldLoopOnStart");
        if(shouldLoop != null) {
            while(true) {
                String shouldStopLoop = getProperty("shouldStopLoop");
                if(shouldStopLoop != null) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    // loop has been interrupted, stop onStart method
                    break;
                }
            }
            setProperty("loopDone",
                        "true");
        }
        String marketDataSource = getParameter("shouldRequestData");
        if(marketDataSource != null) {
            String symbols = getParameter("symbols");
            String content = getParameter("content");
            if(content == null) {
                content = "LATEST_TICK,TOP_OF_BOOK";
            }
            String stringAPI = getParameter("useStringAPI");
            try {
                if(stringAPI != null) {
                    setProperty("requestID",
                                Integer.toString(requestMarketData(MarketDataRequestBuilder.newRequest().withContent(content)
                                                                                                        .withSymbols(symbols)
                                                                                                        .withProvider(marketDataSource).create().toString())));
                } else {
                    setProperty("requestID",
                                Integer.toString(requestMarketData(MarketDataRequestBuilder.newRequest().withContent(content)
                                                                                                        .withSymbols(symbols)
                                                                                                        .withProvider(marketDataSource).create())));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(getParameter("shouldRequestCEPData") != null) {
            String cepDataSource = getParameter("source");
            if(cepDataSource != null) {
                String statementString = getParameter("statements");
                String[] statements;
                if(statementString != null) {
                    statements = statementString.split("#");
                } else {
                    statements = null;
                }
                setProperty("requestID",
                            Integer.toString(requestCEPData(statements,
                                                            cepDataSource)));
            }
        }
        doRequestParameterCallbacks();
        doRequestPropertiesCallbacks();
        if(getParameter("shouldNotify") != null) { 
            notifyLow("low subject",
                      Long.toString(System.nanoTime()));
            notifyMedium("medium subject",
                         Long.toString(System.nanoTime()));
            notifyHigh("high subject",
                       Long.toString(System.nanoTime()));
            debug(null);
            debug("");
            debug("Some statement");
            debug(UnicodeData.HOUSE_AR);
            info(null);
            info("");
            info("Some statement");
            info(UnicodeData.HOUSE_AR);
            warn(null);
            warn("");
            warn("Some statement");
            warn(UnicodeData.HOUSE_AR);
            error(null);
            error("");
            error("Some statement");
            error(UnicodeData.HOUSE_AR);
        }
        if(getProperty("askForBrokers") != null) {
            int counter = 0;
            for(BrokerStatus broker : getBrokers()) {
                setProperty("" + counter++,
                            broker.toString());
            }
        }
        Properties userdata = getUserData();
        userdata.setProperty("onStart",
                             Long.toString(System.currentTimeMillis()));
        setUserData(userdata);
        setProperty("onStart",
                    Long.toString(System.currentTimeMillis()));
    }
    
    @Override
    public void onStop()
    {
        setProperty("onStopBegins",
                    Long.toString(System.currentTimeMillis()));
        String shouldFail = getProperty("shouldFailOnStop");
        if(shouldFail != null) { 
            int x = 10 / 0;
        }
        String marketDataSource = getParameter("shouldRequestDataOnStop");
        if(marketDataSource != null) {
            String symbols = getParameter("symbols");
            setProperty("requestID",
                        Long.toString(requestMarketData(MarketDataRequestBuilder.newRequest().withContent("LATEST_TICK")
                                                                                             .withSymbols(symbols)
                                                                                             .withProvider(marketDataSource).create())));
        }
        String shouldLoop = getParameter("shouldLoopOnStop");
        if(shouldLoop != null) {
            while(true) {
                String shouldStopLoop = getProperty("shouldStopLoop");
                if(shouldStopLoop != null) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    // thread has been interrupted
                    break;
                }
            }
            setProperty("loopDone",
                        "true");
        }
        setProperty("onStop",
                    Long.toString(System.currentTimeMillis()));
    }
    
    @Override
    public void onAsk(AskEvent ask)
    {
        String shouldFail = getParameter("shouldFailOnAsk");
        if(shouldFail != null) { 
            int x = 10 / 0;
        }
        if(getParameter("shouldRequestCEPData") != null) {
            suggestionFromEvent(ask);
        }
        setProperty("onAsk",
                    ask.toString());
    }
    
    @Override
    public void onBid(BidEvent bid)
    {
        String shouldFail = getParameter("shouldFailOnBid");
        if(shouldFail != null) {
            int x = 10 / 0;
        }
        if(getParameter("shouldRequestCEPData") != null) {
            suggestionFromEvent(bid);
        }
        setProperty("onBid",
                    bid.toString());
    }
    
    @Override
    public void onMarketstat(MarketstatEvent statistics)
    {
        String shouldFail = getParameter("shouldFailOnStatistics");
        if(shouldFail != null) {
            int x = 10 / 0;
        }
        setProperty("onStatistics",
                    statistics.toString());
    }
    
    @Override
    public void onCallback(Object data)
    {
        callbackCounter += 1;
        String shouldCancel = getProperty("shouldCancel");
        if(shouldCancel != null) {
            String requestToCancel = getProperty("requestID");
            cancelDataRequest(Integer.parseInt(requestToCancel));
        }
        String shouldFail = getParameter("shouldFailOnCallback");
        if(shouldFail != null) { 
            int x = 10 / 0;
        }
        if(getParameter("shouldRequestCEPData") != null) {
            cancelAllDataRequests();
        }
        setProperty("onCallback",
                    Integer.toString(callbackCounter));
    }

    @Override
    public void onExecutionReport(ExecutionReport executionReport)
    {
        String shouldFail = getParameter("shouldFailOnExecutionReport");
        if(shouldFail != null)  {
            int x = 10 / 0;
        }
        setProperty("onExecutionReport",
                    executionReport.toString());
    }  
    
    @Override
    public void onCancelReject(OrderCancelReject cancel)
    {
        String shouldFail = getParameter("shouldFailOnCancel");
        if(shouldFail != null) {
            int x = 10 / 0;
        }
        setProperty("onCancel",
                    cancel.toString());
    }
    
    @Override
    public void onTrade(TradeEvent trade)
    {
        String shouldFail = getParameter("shouldFailOnTrade");
        if(shouldFail != null) { 
            int x = 10 / 0;
        }
        if(getParameter("shouldRequestCEPData") != null) {
            suggestionFromEvent(trade);
        }
        setProperty("onTrade",
                    trade.toString());
    }
    
    @Override
    public void onOther(Object data)
    {
        String shouldFail = getParameter("shouldFailOnOther");
        if(shouldFail != null)  {
            int x = 10 / 0;
        }
        if(getProperty("shouldCancelCEPData") != null) {
            cancelDataRequest(Integer.parseInt(getProperty("requestID")));
        }
        setProperty("onOther",
                    data.toString());
    }
    
    @Override
    public void onDividend(DividendEvent dividend)
    {
        String shouldFail = getParameter("shouldFailOnDividend");
        if(shouldFail != null) { 
            int x = 10 / 0;
        }
        setProperty("onDividend",
                    dividend.toString());
    }
    
    private void doRequestParameterCallbacks()
    {
        doCallbacks(getParameter("shouldRequestCallbackAfter"),
                    getParameter("shouldRequestCallbackAt"),
                    getParameter("shouldRequestCallbackEvery"));
    }
    
    private void doRequestPropertiesCallbacks()
    {
        doCallbacks(getProperty("shouldRequestCallbackAfter"),
                    getProperty("shouldRequestCallbackAt"),
                    getProperty("shouldRequestCallbackEvery"));
    }
    
    private void doCallbacks(String callbackAfter,
                             String callbackAt,
                             String callbackEvery)
    {
        String shouldDoubleCallbacks = getParameter("shouldDoubleCallbacks");
        int multiplier;
        if(shouldDoubleCallbacks != null) {
            multiplier = 2;
        } else {
            multiplier = 1;
        }
        String callbackDataIsNull = getParameter("callbackDataIsNull");
        for(int i=1;i<=multiplier;i++) {
            if(callbackDataIsNull != null) {
                if(callbackAfter != null) {
                    requestCallbackAfter(Long.parseLong(callbackAfter),
                                         null);
                }
                if(callbackAt != null) {
                    requestCallbackAt(new Date(Long.parseLong(callbackAt)),
                                      null);
                }
                if(callbackEvery != null) {
                    String[] params = callbackEvery.split(",");
                    requestCallbackEvery(Long.parseLong(params[0]),
                                         Long.parseLong(params[1]),
                                         this);
                }
            } else {
                if(callbackAfter != null) {
                    requestCallbackAfter(Long.parseLong(callbackAfter),
                                         this);
                }
                if(callbackAt != null) {
                    requestCallbackAt(new Date(Long.parseLong(callbackAt)),
                                      this);
                }
                if(callbackEvery != null) {
                    try {
                           String[] params = callbackEvery.split(",");
                           requestCallbackEvery(Long.parseLong(params[0]),
                                                Long.parseLong(params[1]),
                                               this);
                     } catch (Exception ex) {
                           ex.printStackTrace();
                           setProperty("callbackEveryException", ex.getClass().getName());
                     }
                }                
            }
        }
    }
    /**
     * Creates a trade suggestion from the given event.
     *
     * @param inEvent an <code>Event</code> value
     */
    private void suggestionFromEvent(Event inEvent)
    {
        OrderSingle suggestedOrder = Factory.getInstance().createOrderSingle();
        if(inEvent instanceof QuoteEvent) {
            QuoteEvent event = (QuoteEvent)inEvent;
            suggestedOrder.setPrice(event.getPrice());
            suggestedOrder.setInstrument(event.getInstrument());
            suggestedOrder.setQuantity(event.getSize());
        } else if(inEvent instanceof TradeEvent) {
            TradeEvent event = (TradeEvent)inEvent;
            suggestedOrder.setPrice(event.getPrice());
            suggestedOrder.setInstrument(event.getInstrument());
            suggestedOrder.setQuantity(event.getSize());
        }
        suggestTrade(suggestedOrder,
                     new BigDecimal("1.0"),
                     "CEP Event Received"); 
    }
}
