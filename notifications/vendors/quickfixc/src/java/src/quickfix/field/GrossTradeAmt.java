package quickfix.field; 
import quickfix.DoubleField; 
import java.util.Date; 

public class GrossTradeAmt extends DoubleField 
{ 
  public static final int FIELD = 381; 

  public GrossTradeAmt() 
  { 
    super(381);
  } 
  public GrossTradeAmt(double data) 
  { 
    super(381, data);
  } 
} 
