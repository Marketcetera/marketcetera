package quickfix.field; 
import quickfix.IntField; 
import java.util.Date; 

public class TradeAllocIndicator extends IntField 
{ 
  public static final int FIELD = 826; 
public static final int ALLOCATION_NOT_REQUIRED = 0; 
public static final int ALLOCATION_REQUIRED = 1; 
public static final int USE_ALLOCATION_PROVIDED_WITH_THE_TRADE = 2; 

  public TradeAllocIndicator() 
  { 
    super(826);
  } 
  public TradeAllocIndicator(int data) 
  { 
    super(826, data);
  } 
} 
