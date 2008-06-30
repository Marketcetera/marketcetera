package quickfix.field; 
import quickfix.CharField; 
import java.util.Date; 

public class RegistTransType extends CharField 
{ 
  public static final int FIELD = 514; 
public static final char NEW = '0'; 
public static final char REPLACE = '1'; 
public static final char CANCEL = '2'; 

  public RegistTransType() 
  { 
    super(514);
  } 
  public RegistTransType(char data) 
  { 
    super(514, data);
  } 
} 
