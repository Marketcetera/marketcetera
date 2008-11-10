package quickfix.field; 
import quickfix.CharField; 
import java.util.Date; 

public class HandlInst extends CharField 
{ 
  public static final int FIELD = 21; 
public static final char AUTOMATED_EXECUTION_ORDER_PRIVATE = '1'; 
public static final char AUTOMATED_EXECUTION_ORDER_PUBLIC = '2'; 
public static final char MANUAL_ORDER = '3'; 

  public HandlInst() 
  { 
    super(21);
  } 
  public HandlInst(char data) 
  { 
    super(21, data);
  } 
} 
