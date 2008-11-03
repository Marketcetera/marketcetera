package quickfix.field; 
import quickfix.StringField; 
import java.util.Date; 

public class CashDistribAgentCode extends StringField 
{ 
  public static final int FIELD = 499; 

  public CashDistribAgentCode() 
  { 
    super(499);
  } 
  public CashDistribAgentCode(String data) 
  { 
    super(499, data);
  } 
} 
