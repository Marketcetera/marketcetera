package quickfix.field; 
import quickfix.IntField; 
import java.util.Date; 

public class MsgSeqNum extends IntField 
{ 
  public static final int FIELD = 34; 

  public MsgSeqNum() 
  { 
    super(34);
  } 
  public MsgSeqNum(int data) 
  { 
    super(34, data);
  } 
} 
