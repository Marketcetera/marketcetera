package quickfix.field; 
import quickfix.StringField; 
import java.util.Date; 

public class SecuritySettlAgentAcctNum extends StringField 
{ 
  public static final int FIELD = 178; 

  public SecuritySettlAgentAcctNum() 
  { 
    super(178);
  } 
  public SecuritySettlAgentAcctNum(String data) 
  { 
    super(178, data);
  } 
} 
