package quickfix.field; 
import quickfix.CharField; 
import java.util.Date; 

public class AdvSide extends CharField 
{ 
  public static final int FIELD = 4; 
public static final char BUY = 'B'; 
public static final char SELL = 'S'; 
public static final char CROSS = 'X'; 
public static final char TRADE = 'T'; 

  public AdvSide() 
  { 
    super(4);
  } 
  public AdvSide(char data) 
  { 
    super(4, data);
  } 
} 
