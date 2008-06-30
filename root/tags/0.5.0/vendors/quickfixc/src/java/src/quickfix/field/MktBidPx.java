package quickfix.field; 
import quickfix.DoubleField; 
import java.util.Date; 

public class MktBidPx extends DoubleField 
{ 
  public static final int FIELD = 645; 

  public MktBidPx() 
  { 
    super(645);
  } 
  public MktBidPx(double data) 
  { 
    super(645, data);
  } 
} 
