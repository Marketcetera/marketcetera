package quickfix.field; 
import quickfix.StringField; 
import java.util.Date; 

public class UnderlyingMaturityMonthYear extends StringField 
{ 
  public static final int FIELD = 313; 

  public UnderlyingMaturityMonthYear() 
  { 
    super(313);
  } 
  public UnderlyingMaturityMonthYear(String data) 
  { 
    super(313, data);
  } 
} 
