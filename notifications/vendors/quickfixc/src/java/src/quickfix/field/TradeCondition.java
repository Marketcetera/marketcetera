package quickfix.field; 
import quickfix.StringField; 
import java.util.Date; 

public class TradeCondition extends StringField 
{ 
  public static final int FIELD = 277; 
public static final char CASH_MARKET = 'A'; 
public static final char AVERAGE_PRICE_TRADE = 'B'; 
public static final char CASH_TRADE = 'C'; 
public static final char NEXT_DAY_MARKET = 'D'; 
public static final char OPENING_REOPENING_TRADE_DETAIL = 'E'; 
public static final char INTRADAY_TRADE_DETAIL = 'F'; 
public static final char RULE127 = 'G'; 
public static final char RULE155 = 'H'; 
public static final char SOLD_LAST = 'I'; 
public static final char NEXT_DAY_TRADE = 'J'; 
public static final char OPENED = 'K'; 
public static final char SELLER = 'L'; 
public static final char SOLD = 'M'; 
public static final char STOPPED_STOCK = 'N'; 
public static final char IMBALANCE_MORE_BUYERS = 'P'; 
public static final char IMBALANCE_MORE_SELLERS = 'Q'; 
public static final char OPENING_PRICE = 'R'; 

  public TradeCondition() 
  { 
    super(277);
  } 
  public TradeCondition(String data) 
  { 
    super(277, data);
  } 
} 
