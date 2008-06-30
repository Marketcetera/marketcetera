package quickfix.field; 
import quickfix.StringField; 
import java.util.Date; 

public class NestedPartyID extends StringField 
{ 
  public static final int FIELD = 524; 

  public NestedPartyID() 
  { 
    super(524);
  } 
  public NestedPartyID(String data) 
  { 
    super(524, data);
  } 
} 
