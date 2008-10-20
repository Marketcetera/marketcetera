package quickfix.field; 
import quickfix.UtcTimeStampField; 
import java.util.Date; 

public class StrikeTime extends UtcTimeStampField 
{ 
  public static final int FIELD = 443; 

  public StrikeTime() 
  { 
    super(443);
  } 
  public StrikeTime(Date data) 
  { 
    super(443, data);
  } 
} 
