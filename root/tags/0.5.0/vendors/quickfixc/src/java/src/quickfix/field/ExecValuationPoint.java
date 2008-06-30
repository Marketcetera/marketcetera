package quickfix.field; 
import quickfix.UtcTimeStampField; 
import java.util.Date; 

public class ExecValuationPoint extends UtcTimeStampField 
{ 
  public static final int FIELD = 515; 

  public ExecValuationPoint() 
  { 
    super(515);
  } 
  public ExecValuationPoint(Date data) 
  { 
    super(515, data);
  } 
} 
