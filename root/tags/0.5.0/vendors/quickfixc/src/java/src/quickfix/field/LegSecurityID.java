package quickfix.field; 
import quickfix.StringField; 
import java.util.Date; 

public class LegSecurityID extends StringField 
{ 
  public static final int FIELD = 602; 

  public LegSecurityID() 
  { 
    super(602);
  } 
  public LegSecurityID(String data) 
  { 
    super(602, data);
  } 
} 
