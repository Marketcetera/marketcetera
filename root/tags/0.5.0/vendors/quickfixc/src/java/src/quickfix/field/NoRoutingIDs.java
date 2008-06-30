package quickfix.field; 
import quickfix.IntField; 
import java.util.Date; 

public class NoRoutingIDs extends IntField 
{ 
  public static final int FIELD = 215; 

  public NoRoutingIDs() 
  { 
    super(215);
  } 
  public NoRoutingIDs(int data) 
  { 
    super(215, data);
  } 
} 
