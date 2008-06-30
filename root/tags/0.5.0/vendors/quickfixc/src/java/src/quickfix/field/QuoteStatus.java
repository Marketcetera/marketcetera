package quickfix.field; 
import quickfix.IntField; 
import java.util.Date; 

public class QuoteStatus extends IntField 
{ 
  public static final int FIELD = 297; 
public static final int ACCEPTED = 0; 
public static final int CANCELED_FOR_SYMBOL = 1; 
public static final int CANCELED_FOR_SECURITY_TYPE = 2; 
public static final int CANCELED_FOR_UNDERLYING = 3; 
public static final int CANCELED_ALL = 4; 
public static final int REJECTED = 5; 
public static final int REMOVED_FROM_MARKET = 6; 
public static final int EXPIRED = 7; 
public static final int QUERY = 8; 
public static final int QUOTE_NOT_FOUND = 9; 
public static final int PENDING = 10; 
public static final int PASS = 11; 
public static final int LOCKED_MARKET_WARNING = 12; 
public static final int CROSS_MARKET_WARNING = 13; 
public static final int CANCELED_DUE_TO_LOCK_MARKET = 14; 
public static final int CANCELED_DUE_TO_CROSS_MARKET = 15; 

  public QuoteStatus() 
  { 
    super(297);
  } 
  public QuoteStatus(int data) 
  { 
    super(297, data);
  } 
} 
