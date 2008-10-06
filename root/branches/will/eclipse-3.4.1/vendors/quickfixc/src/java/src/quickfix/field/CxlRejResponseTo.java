package quickfix.field; 
import quickfix.CharField; 
import java.util.Date; 

public class CxlRejResponseTo extends CharField 
{ 
  public static final int FIELD = 434; 
public static final char ORDER_CANCEL_REQUEST = '1'; 
public static final char ORDER_CANCEL_REPLACE_REQUEST = '2'; 

  public CxlRejResponseTo() 
  { 
    super(434);
  } 
  public CxlRejResponseTo(char data) 
  { 
    super(434, data);
  } 
} 
