package quickfix.field; 
import quickfix.StringField; 
import java.util.Date; 

public class ExpireDate extends StringField 
{ 
  public static final int FIELD = 432; 

  public ExpireDate() 
  { 
    super(432);
  } 
  public ExpireDate(String data) 
  { 
    super(432, data);
  } 
} 
