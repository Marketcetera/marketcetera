package quickfix.field; 
import quickfix.IntField; 
import java.util.Date; 

public class TradSesStatusRejReason extends IntField 
{ 
  public static final int FIELD = 567; 
public static final int UNKNOWN_OR_INVALID_TRADINGSESSIONID = 1; 

  public TradSesStatusRejReason() 
  { 
    super(567);
  } 
  public TradSesStatusRejReason(int data) 
  { 
    super(567, data);
  } 
} 
