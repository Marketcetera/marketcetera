package quickfix.field; 
import quickfix.IntField; 
import java.util.Date; 

public class CustomerOrFirm extends IntField 
{ 
  public static final int FIELD = 204; 
public static final int CUSTOMER = 0; 
public static final int FIRM = 1; 

  public CustomerOrFirm() 
  { 
    super(204);
  } 
  public CustomerOrFirm(int data) 
  { 
    super(204, data);
  } 
} 
