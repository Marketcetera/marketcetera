package quickfix.field; 
import quickfix.StringField; 
import java.util.Date; 

public class OpenCloseSettleFlag extends StringField 
{ 
  public static final int FIELD = 286; 
public static final char DAILY_OPEN = '0'; 
public static final char SESSION_OPEN = '1'; 
public static final char DELIVERY_SETTLEMENT_PRICE = '2'; 
public static final char EXPECTED_PRICE = '3'; 
public static final char PRICE_FROM_PREVIOUS_BUSINESS_DAY = '4'; 

  public OpenCloseSettleFlag() 
  { 
    super(286);
  } 
  public OpenCloseSettleFlag(String data) 
  { 
    super(286, data);
  } 
} 
