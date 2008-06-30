package quickfix.field; 
import quickfix.UtcTimeStampField; 
import java.util.Date; 

public class OrigOrdModTime extends UtcTimeStampField 
{ 
  public static final int FIELD = 586; 

  public OrigOrdModTime() 
  { 
    super(586);
  } 
  public OrigOrdModTime(Date data) 
  { 
    super(586, data);
  } 
} 
