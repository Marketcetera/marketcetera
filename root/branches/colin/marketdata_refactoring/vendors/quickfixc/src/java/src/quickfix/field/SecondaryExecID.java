package quickfix.field; 
import quickfix.StringField; 
import java.util.Date; 

public class SecondaryExecID extends StringField 
{ 
  public static final int FIELD = 527; 

  public SecondaryExecID() 
  { 
    super(527);
  } 
  public SecondaryExecID(String data) 
  { 
    super(527, data);
  } 
} 
