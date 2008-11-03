package quickfix.field; 
import quickfix.StringField; 
import java.util.Date; 

public class EncodedText extends StringField 
{ 
  public static final int FIELD = 355; 

  public EncodedText() 
  { 
    super(355);
  } 
  public EncodedText(String data) 
  { 
    super(355, data);
  } 
} 
