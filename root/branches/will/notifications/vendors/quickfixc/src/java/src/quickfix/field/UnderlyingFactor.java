package quickfix.field; 
import quickfix.DoubleField; 
import java.util.Date; 

public class UnderlyingFactor extends DoubleField 
{ 
  public static final int FIELD = 246; 

  public UnderlyingFactor() 
  { 
    super(246);
  } 
  public UnderlyingFactor(double data) 
  { 
    super(246, data);
  } 
} 
