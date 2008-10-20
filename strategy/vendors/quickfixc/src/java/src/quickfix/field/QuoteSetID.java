package quickfix.field; 
import quickfix.StringField; 
import java.util.Date; 

public class QuoteSetID extends StringField 
{ 
  public static final int FIELD = 302; 

  public QuoteSetID() 
  { 
    super(302);
  } 
  public QuoteSetID(String data) 
  { 
    super(302, data);
  } 
} 
