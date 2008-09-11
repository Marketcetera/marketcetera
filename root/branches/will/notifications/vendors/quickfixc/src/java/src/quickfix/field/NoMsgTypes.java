package quickfix.field; 
import quickfix.IntField; 
import java.util.Date; 

public class NoMsgTypes extends IntField 
{ 
  public static final int FIELD = 384; 

  public NoMsgTypes() 
  { 
    super(384);
  } 
  public NoMsgTypes(int data) 
  { 
    super(384, data);
  } 
} 
