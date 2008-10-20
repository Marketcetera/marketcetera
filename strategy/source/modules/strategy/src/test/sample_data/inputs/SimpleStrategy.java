import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import java.lang.String;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;

public class SimpleStrategy
        extends org.marketcetera.strategy.java.Strategy
{
    // the maximum width that is interesting to the user
    private static final BigDecimal WIDTH_SPREAD = new BigDecimal("0.1");
    private final Map lastBids = new HashMap();
    private final Map lastAsks = new HashMap();
    public void onBid(BidEvent inBid)
    {
        String symbol = inBid.getSymbol();
        lastBids.put(symbol,
                     inBid);
        process(symbol);
    }
    public void onAsk(AskEvent inAsk)
    {
        String symbol = inAsk.getSymbol();
        lastAsks.put(symbol,
                     inAsk);
        process(symbol);
    }
    private synchronized void process(String inSymbol)
    {
        AskEvent ask = (AskEvent)lastAsks.get(inSymbol);
        BidEvent bid = (BidEvent)lastBids.get(inSymbol);
        if(bid != null &&
           ask != null) {
            BigDecimal bidPrice = bid.getPrice();
            BigDecimal askPrice = ask.getPrice();
            String symbol = bid.getSymbol();
            if(askPrice.subtract(bidPrice).compareTo(WIDTH_SPREAD) == -1) {
                setProperty("STRATEGY_PROPERTY",
                            Long.toString(System.nanoTime()));
            }
        }
    }
}

/*
require 'java'

#Marketcetera Classes
module Marketcetera
  include_class "org.marketcetera.photon.scripting.Strategy"
end

#Quick Fix Classes
module QF
  include_class "quickfix.Message"
  include_class "quickfix.field.MDEntryPx"
  include_class "quickfix.field.MDEntryType"
  include_class "quickfix.field.Symbol"
end

# the maximum width that is interesting to the user
WIDTH_SPREAD = 0.1

# each object that is interested in receiving market data
# must be a class that implements on_message(message)
class SimpleStrategy < Marketcetera::Strategy
  def on_market_data_snapshot(message)
    symbol = message.getString(QF::Symbol::FIELD)
    bid = BigDecimal.new(extractMD(message, QF::MDEntryType::BID, QF::MDEntryPx::FIELD).to_s)
    ask = BigDecimal.new(extractMD(message, QF::MDEntryType::OFFER, QF::MDEntryPx::FIELD).to_s)
    if((ask-bid) < WIDTH_SPREAD)
      puts "Market below minimum width #{WIDTH_SPREAD}: #{symbol} [#{bid} - #{ask}] at "+Time.now.to_s
    end
  end
end
*/