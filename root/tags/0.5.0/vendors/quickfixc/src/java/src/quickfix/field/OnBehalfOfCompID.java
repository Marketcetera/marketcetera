package quickfix.field; 
import quickfix.StringField; 
import java.util.Date; 

public class OnBehalfOfCompID extends StringField 
{ 
  public static final int FIELD = 115; 

  public OnBehalfOfCompID() 
  { 
    super(115);
  } 
  public OnBehalfOfCompID(String data) 
  { 
    super(115, data);
  } 
} 
