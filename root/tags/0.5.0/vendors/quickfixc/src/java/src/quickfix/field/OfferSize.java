package quickfix.field; 
import quickfix.DoubleField; 
import java.util.Date; 

public class OfferSize extends DoubleField 
{ 
  public static final int FIELD = 135; 

  public OfferSize() 
  { 
    super(135);
  } 
  public OfferSize(double data) 
  { 
    super(135, data);
  } 
} 
