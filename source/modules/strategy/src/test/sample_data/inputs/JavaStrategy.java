import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.lang.Override;
import java.math.BigDecimal;

import org.marketcetera.client.dest.DestinationStatus;
import org.marketcetera.core.MSymbol;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderCancelReject;

public class JavaStrategy
        extends org.marketcetera.strategy.java.Strategy
{
    private int callbackCounter = 0;

    @Override
    public void onStart()
    {
        callbackCounter = 0;
        String shouldFail = getParameter("shouldFailOnStart");
        if(shouldFail != null) {
            int x = 10 / 0;
        }
        String shouldLoop = getParameter("shouldLoopOnStart");
        if(shouldLoop != null) {
            while(true) {
                String shouldStopLoop = getParameter("shouldStopLoop");
                if(shouldStopLoop != null) {
                    break;
                }
                System.out.println("sleeping...");
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    // do something
                }
            }
        }
        String marketDataSource = getParameter("shouldRequestData");
        if(marketDataSource != null) {
            String symbols = getParameter("symbols");
            setProperty("requestID",
                        Long.toString(requestMarketData(symbols,
                                                        marketDataSource)));
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
        }
        if(getProperty("askForPosition") != null) {
            String symbol = getProperty("symbol");
            String dateString = getProperty("date");
            Date date;
            if(dateString == null) {
                date = null;
            } else {
                date = new Date(Long.parseLong(dateString));
            }
            BigDecimal result = getPositionAsOf(date,
                                                symbol);
            String resultString;
            if(result == null) {
               resultString = null;
            } else {
               resultString = result.toString();
            }
            setProperty("position",
                        resultString);
        }
        if(getProperty("askForDestinations") != null) {
            int counter = 0;
            for(DestinationStatus destination : getDestinations()) {
                setProperty("" + counter++,
                            destination.toString());
            }
        }
        setProperty("onStart",
                    Long.toString(System.currentTimeMillis()));
    }
    
    @Override
    public void onStop()
    {
        String shouldFail = getParameter("shouldFailOnStop");
        if(shouldFail != null) { 
            int x = 10 / 0;
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
        setProperty("onBid",
                    bid.toString());
    }
    
    @Override
    public void onCallback(Object data)
    {
        callbackCounter += 1;
        String shouldCancel = getProperty("shouldCancel");
        if(shouldCancel != null) {
            String requestToCancel = getProperty("requestID");
            cancelMarketDataRequest(Long.parseLong(requestToCancel));
        }
        String shouldFail = getParameter("shouldFailOnCallback");
        if(shouldFail != null) { 
            int x = 10 / 0;
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
    public void onCancel(OrderCancelReject cancel)
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
        setProperty("onOther",
                    data.toString());
    }
    
    private void doRequestParameterCallbacks()
    {
        doCallbacks(getParameter("shouldRequestCallbackAfter"),
                    getParameter("shouldRequestCallbackAt"));
    }
    
    private void doRequestPropertiesCallbacks()
    {
        doCallbacks(getProperty("shouldRequestCallbackAfter"),
                    getProperty("shouldRequestCallbackAt"));
    }
    
    private void doCallbacks(String callbackAfter,
                             String callbackAt)
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
            } else {
                if(callbackAfter != null) {
                    requestCallbackAfter(Long.parseLong(callbackAfter),
                                         this);
                }
                if(callbackAt != null) {
                    requestCallbackAt(new Date(Long.parseLong(callbackAt)),
                                      this);
                }
            }
        }
    }
}
