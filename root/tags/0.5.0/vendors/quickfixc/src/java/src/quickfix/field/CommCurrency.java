package quickfix.field; 
import quickfix.StringField; 
import java.util.Date; 

public class CommCurrency extends StringField 
{ 
  public static final int FIELD = 479; 

  public CommCurrency() 
  { 
    super(479);
  } 
  public CommCurrency(String data) 
  { 
    super(479, data);
  } 
} 
