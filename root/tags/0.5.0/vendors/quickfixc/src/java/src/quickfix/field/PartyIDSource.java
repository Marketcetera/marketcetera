package quickfix.field; 
import quickfix.CharField; 
import java.util.Date; 

public class PartyIDSource extends CharField 
{ 
  public static final int FIELD = 447; 
public static final char BIC = 'B'; 
public static final char GENERALLY_ACCEPTED_MARKET_PARTICIPANT_IDENTIFIER = 'C'; 
public static final char PROPRIETARY_CUSTOM_CODE = 'D'; 
public static final char ISO_COUNTRY_CODE = 'E'; 
public static final char SETTLEMENT_ENTITY_LOCATION = 'F'; 
public static final char MIC = 'G'; 
public static final char CSD_PARTICIPANT_MEMBER_CODE = 'H'; 
public static final char KOREAN_INVESTOR_ID = '1'; 
public static final char TAIWANESE_QUALIFIED_FOREIGN_INVESTOR_ID_QFII_FID = '2'; 
public static final char TAIWANESE_TRADING_ACCOUNT = '3'; 
public static final char MALAYSIAN_CENTRAL_DEPOSITORY_NUMBER = '4'; 
public static final char CHINESE_B_SHARE = '5'; 
public static final char UK_NATIONAL_INSURANCE_OR_PENSION_NUMBER = '6'; 
public static final char US_SOCIAL_SECURITY_NUMBER = '7'; 
public static final char US_EMPLOYER_IDENTIFICATION_NUMBER = '8'; 
public static final char AUSTRALIAN_BUSINESS_NUMBER = '9'; 
public static final char AUSTRALIAN_TAX_FILE_NUMBER = 'A'; 
public static final char DIRECTED_BROKER = 'I'; 

  public PartyIDSource() 
  { 
    super(447);
  } 
  public PartyIDSource(char data) 
  { 
    super(447, data);
  } 
} 
