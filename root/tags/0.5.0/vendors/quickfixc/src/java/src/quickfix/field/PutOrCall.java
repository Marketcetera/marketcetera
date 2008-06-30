package quickfix.field; 
import quickfix.IntField; 
import java.util.Date; 

public class PutOrCall extends IntField 
{ 
  public static final int FIELD = 201; 
public static final int PUT = 0; 
public static final int CALL = 1; 

  public PutOrCall() 
  { 
    super(201);
  } 
  public PutOrCall(int data) 
  { 
    super(201, data);
  } 
} 
