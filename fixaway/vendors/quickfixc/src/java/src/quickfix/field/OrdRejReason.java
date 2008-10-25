package quickfix.field; 
import quickfix.IntField; 
import java.util.Date; 

public class OrdRejReason extends IntField 
{ 
  public static final int FIELD = 103; 
public static final int BROKER_EXCHANGE_OPTION = 0; 
public static final int UNKNOWN_SYMBOL = 1; 
public static final int EXCHANGE_CLOSED = 2; 
public static final int ORDER_EXCEEDS_LIMIT = 3; 
public static final int TOO_LATE_TO_ENTER = 4; 
public static final int UNKNOWN_ORDER = 5; 
public static final int DUPLICATE_ORDER = 6; 
public static final int DUPLICATE_OF_A_VERBALLY_COMMUNICATED_ORDER = 7; 
public static final int STALE_ORDER = 8; 
public static final int TRADE_ALONG_REQUIRED = 9; 
public static final int INVALID_INVESTOR_ID = 10; 
public static final int UNSUPPORTED_ORDER_CHARACTERISTIC = 11; 
public static final int SURVEILLENCE_OPTION = 12; 
public static final int INCORRECT_QUANTITY = 13; 
public static final int INCORRECT_ALLOCATED_QUANTITY = 14; 
public static final int UNKNOWN_ACCOUNT = 15; 
public static final int OTHER = 99; 

  public OrdRejReason() 
  { 
    super(103);
  } 
  public OrdRejReason(int data) 
  { 
    super(103, data);
  } 
} 
