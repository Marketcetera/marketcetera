package quickfix.field; 
import quickfix.StringField; 
import java.util.Date; 

public class RegistEmail extends StringField 
{ 
  public static final int FIELD = 511; 

  public RegistEmail() 
  { 
    super(511);
  } 
  public RegistEmail(String data) 
  { 
    super(511, data);
  } 
} 
