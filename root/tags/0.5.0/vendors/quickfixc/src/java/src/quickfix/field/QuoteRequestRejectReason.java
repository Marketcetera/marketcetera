package quickfix.field; 
import quickfix.IntField; 
import java.util.Date; 

public class QuoteRequestRejectReason extends IntField 
{ 
  public static final int FIELD = 658; 
public static final int UNKNOWN_SYMBOL = 1; 
public static final int EXCHANGE_CLOSED = 2; 
public static final int QUOTE_REQUEST_EXCEEDS_LIMIT = 3; 
public static final int TOO_LATE_TO_ENTER = 4; 
public static final int INVALID_PRICE = 5; 
public static final int NOT_AUTHORIZED_TO_REQUEST_QUOTE = 6; 
public static final int NO_MATCH_FOR_INQUIRY = 7; 
public static final int NO_MARKET_FOR_INSTRUMENT = 8; 
public static final int NO_INVENTORY = 9; 

  public QuoteRequestRejectReason() 
  { 
    super(658);
  } 
  public QuoteRequestRejectReason(int data) 
  { 
    super(658, data);
  } 
} 
