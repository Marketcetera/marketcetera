package quickfix.field; 
import quickfix.IntField; 
import java.util.Date; 

public class UnderlyingProduct extends IntField 
{ 
  public static final int FIELD = 462; 

  public UnderlyingProduct() 
  { 
    super(462);
  } 
  public UnderlyingProduct(int data) 
  { 
    super(462, data);
  } 
} 
