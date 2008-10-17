package quickfix.field; 
import quickfix.IntField; 
import java.util.Date; 

public class ListNoOrds extends IntField 
{ 
  public static final int FIELD = 68; 

  public ListNoOrds() 
  { 
    super(68);
  } 
  public ListNoOrds(int data) 
  { 
    super(68, data);
  } 
} 
