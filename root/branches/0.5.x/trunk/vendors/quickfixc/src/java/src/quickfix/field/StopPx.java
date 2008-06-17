package quickfix.field; 
import quickfix.DoubleField; 
import java.util.Date; 

public class StopPx extends DoubleField 
{ 
  public static final int FIELD = 99; 

  public StopPx() 
  { 
    super(99);
  } 
  public StopPx(double data) 
  { 
    super(99, data);
  } 
} 
