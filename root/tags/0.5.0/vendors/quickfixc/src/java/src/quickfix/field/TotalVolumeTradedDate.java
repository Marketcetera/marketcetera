package quickfix.field; 
import quickfix.UtcDateOnlyField; 
import java.util.Date; 

public class TotalVolumeTradedDate extends UtcDateOnlyField 
{ 
  public static final int FIELD = 449; 

  public TotalVolumeTradedDate() 
  { 
    super(449);
  } 
  public TotalVolumeTradedDate(Date data) 
  { 
    super(449, data);
  } 
} 
