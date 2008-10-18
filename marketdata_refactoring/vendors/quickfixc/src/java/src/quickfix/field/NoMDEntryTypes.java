package quickfix.field; 
import quickfix.IntField; 
import java.util.Date; 

public class NoMDEntryTypes extends IntField 
{ 
  public static final int FIELD = 267; 

  public NoMDEntryTypes() 
  { 
    super(267);
  } 
  public NoMDEntryTypes(int data) 
  { 
    super(267, data);
  } 
} 
