package org.marketcetera.trade;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.marketcetera.util.misc.ClassVersion;


/**
 * Class to represent a currency instrument.
 * 
 * leftCCY is the typical "traded" ccy ( in relation to the price )
 *     in EURUSD this is EUR while the price represents the number of USD per EUR
 * 
 * while rightCCY is the currency in which PL is realized
 *  
 * @author richard.obrien
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@ClassVersion("$Id$")
public class Currency extends Instrument implements Comparable<Currency>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * validation/set of permissible tenors
	 *  OUTRIGHTS:
	 * 	   TOD = Tomorrow  (T+0)
	 *     ON  = Overnight (T+0)
	 *     TN  = Tomorrow Next (T+1)
	 *     SP  = Spot  -- T+1 in CAD,PHP,RUB ... rest are T+2
	 *     SN  = Spot Next (Spot+1)
	 *     nD  = n days until settlement
	 *     nW  = n weeks until settlement
	 *     nM  = n months until settlement
	 *     nY  = n years until settlement
	 *     nIMM = n'th nearest IMM date.  an IMM date is the 3rd Wednesday
	 *                of March, June, September and December months
	 *                
	 *     
	 */
	
	// when metc upgrades to newest guava, replace with ImmutableSet.of(...)
	/*protected static Set<String> TENORSET = new HashSet<String>();
	static{
		String[] TENORARRAY = 
		   {"TOD","ON","TN","SP","SN",
			"1D","2D","3D","4D","5D","6D","7D","8D","9D","10D",
			"1W","2W","3W",
			"1M","2M","3M","4M","5M","6M","7M","8M","9M","10M","11M","12M",
			"1Y","2Y","3Y",
			"1IMM","2IMM","3IMM","4IMM"};
		
		TENORSET.addAll(Arrays.asList(TENORARRAY));
	}*/
				
	/*public static Set<String> getTenorSet()
	{
		return Collections.unmodifiableSet(TENORSET);
	}*/
	protected static DateTimeFormatter fixDateFormat = DateTimeFormat.forPattern("yyyyMMdd");
	
	/**
	 * aka the "base" currency, eg EUR in EURUSD
	 * 
	 */
	protected final String leftCCY;   
	
	/**
	 * aka the "pl" or "variable" currency, eg JPY in USDJPY
	 */
	protected final String rightCCY;	
	
	/**
	 * store the tenor as a string
	 *   trading systems often value generic representations, eg Spot
	 *   alternatively if we stored it as a date object
	 *   we'd have to actually calculate the settlement date when any of the
	 *   generics are specified which requires a holiday calendar of many countries
	 */
	protected final String nearTenor;
	protected final String farTenor;
	
	/**
	 *  leftCCY/rightCCY, final cached for minute performance gain in getSymbol()
	 */
	protected final String fixSymbol;
	
	/**
	 * likewise cache a hashCode
	 */
	private final int hashCode;
	
	/**
	 *   currency pertaining to the order submitted
	 *      eg, both the SIDE and QUANTITY refer to this currency
	 */
	protected String tradedCCY;	    
	
	
	/**
	 * JAXB empty constructor
	 *    JAXB cannot instantiate an object without empty constructor
	 */
	protected Currency()
    {
        //this("","","","");
		leftCCY = null;
		rightCCY = null;
		nearTenor = null;
		farTenor = null;
		hashCode = -1;
		fixSymbol = null;
		
    }
	
	public Currency(String symbol)
	{
		String[] currencies = symbol.split("/");
		fixSymbol = symbol;
		nearTenor=null;
		farTenor=null;		
		if(currencies.length ==2)
		{
			leftCCY = currencies[0];
			rightCCY = currencies[1];
			hashCode = symbol.hashCode();
		} else {
			leftCCY=null;
			rightCCY=null;
			hashCode=-1;
		}		
	}
	
	/**
	 * outright trade constructor for generic delivery date
	 * 
	 * @param baseCCY
	 * @param plCCY
	 * @param nearTenor
	 * 
	 */
	public Currency(String baseCCY, String plCCY, String nearTenor){
		this(baseCCY, plCCY, nearTenor, new String(""));
	}
	
	
	

	/**
	 * outright trade constructor for an exact delivery date
	 * 
	 * @param baseCCY
	 * @param plCCY
	 * @param nearTenor
	 */
	public Currency(String baseCCY, String plCCY, LocalDate nearTenor){
		this(baseCCY,plCCY,nearTenor,null);
	}
	

	/**
	 * swap trade constructor for generic near tenor, exact far tenor swap
	 *    (eg typically for use in Spot vs IMM fwd)
	 * 
	 * @param baseCCY
	 * @param plCCY
	 * @param nearTenor
	 */	
	public Currency(String baseCCY, String plCCY, String nearTenor, LocalDate farTenor){
		this(baseCCY, plCCY, nearTenor,
				farTenor==null ? null : farTenor.toString(fixDateFormat));
	}
	
	/**
	 * swap trade constructor for exact swap dates
	 * 
	 * @param baseCCY
	 * @param plCCY
	 * @param nearTenor
	 */	
	public Currency(String baseCCY, String plCCY, LocalDate nearTenor, LocalDate farTenor){
		this(baseCCY, plCCY, nearTenor.toString(fixDateFormat),
				farTenor==null ? null : farTenor.toString(fixDateFormat));
	}
	
	/**
	 * outright trade constructor -OR- generic swap constructor
	 * 
	 * @param baseCCY
	 * @param plCCY
	 * @param nearTenor
	 * @param farTenor
	 * 
	 */
	public Currency(String baseCCY, String plCCY, String nearTenor, String farTenor){
		baseCCY = StringUtils.trimToNull(baseCCY);
		plCCY = StringUtils.trimToNull(plCCY);
		nearTenor = StringUtils.trimToNull(nearTenor);
		farTenor = StringUtils.trimToNull(farTenor);
		Validate.notNull(baseCCY,Messages.MISSING_LEFT_CURRENCY.getText());
		Validate.notNull(plCCY,Messages.MISSING_RIGHT_CURRENCY.getText());
		this.leftCCY = baseCCY;
		this.rightCCY   = plCCY;
		this.fixSymbol = leftCCY+"/"+rightCCY;
		if(Currency.isValidTenor(nearTenor)){
			this.nearTenor = nearTenor;
		}else{
			this.nearTenor = null;
		}
		if(Currency.isValidTenor(farTenor)){
			this.farTenor = farTenor;
		}else{
			this.farTenor = null;
		}
		this.setTradedCCY(baseCCY);
		
		// how to handle null nearTenor in this case?
		String hashSymbol = this.fixSymbol+this.nearTenor+(this.farTenor==null?"":this.farTenor);
		
		this.hashCode   = hashSymbol.hashCode();
	}
	
	/**
	 * utility method determining if this is a currency swap
	 *   eg when the farTenor is null
	 *   
	 * @return
	 */
	public boolean isSwap(){
		return (farTenor!=null);
	}
	
	/**
	 * returns the "base" or left ccy of the currency pair
	 * @return
	 */
	public String getLeftCCY() {
		return leftCCY;
	}

	/**
	 * returns the "pl" or right ccy of the currency pair
	 * @return
	 */
	public String getRightCCY() {
		return rightCCY;
	}
	
	/**
	 * returns the delivery date for the currency outright trade
	 *   or if it is a currency swap the near leg's delivery date
	 *   
	 * @return
	 */
	public String getNearTenor(){
		return nearTenor;
	}
	
	/**
	 * returns the far delivery date for the currency swap trade
	 * @return
	 */
	public String getFarTenor(){
		return farTenor;
	}

	/**
	 * determines which currency OrderQty (and related FIX fields)
	 *  relates to...  FIX Tag 15
	 *  
	 * that is if sending an order for symbol USD/JPY, the order can specify
	 * whether the desired qty traded is in JPY or USD
	 *  
	 * @param ccy
	 */
	public void setTradedCCY(String ccy){
		if(ccy.equals(leftCCY)){
			this.tradedCCY=ccy;
		}else{
			if(ccy.equals(rightCCY)){
				this.tradedCCY=ccy;	
			}
		}
	}

	/**
	 * returns the currency specified in the qty fields of FIX orders 
	 * for this Instrument
	 * 
	 * @return
	 */
	public String getTradedCCY(){
		return this.tradedCCY;
	}
	
	
	/**
	 * returns the METC API enum corresponding to tag167
	 */
	@Override
	public SecurityType getSecurityType() {
		return SecurityType.Currency; // SecurityType.Currency
	}

	/**
	 * returns the FIX tag55 value for this Instrument
	 */
	@Override
	public String getSymbol() {
		return this.fixSymbol;
	}
	
	/**
	 * simple String hashcode of symbol+tenors
	 *   
	 */
	public int hashCode(){
		return this.hashCode;
	}
	
    @Override
	public boolean equals(Object obj) { 	
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Currency other = (Currency) obj;
        if (nearTenor == null) {
            if (other.nearTenor != null)
                return false;
        } else if (!nearTenor.equals(other.nearTenor))
            return false;
        if (farTenor == null) {
            if (other.farTenor != null)
                return false;
        } else if (!farTenor.equals(other.farTenor))
            return false;
        if (leftCCY == null) {
            if (other.leftCCY != null)
                return false;
        } else if (!leftCCY.equals(other.leftCCY))
            return false;
        if (rightCCY == null) {
            if (other.rightCCY != null)
                return false;
        } else if (!rightCCY.equals(other.rightCCY))
            return false;
        return true;
		
	}
    
	
	@Override
	public String toString() {
		return "Currency [leftCCY=" + leftCCY + ", rightCCY=" + rightCCY
				+ ", nearTenor=" + nearTenor + ", farTenor=" + farTenor
				+ ", fixSymbol=" + fixSymbol + ", hashCode=" + hashCode
				+ ", tradedCCY=" + tradedCCY + "]";
	}


	/**
	 * utility method to invert the ccypair and return a new instrument
	 *   eg for Currency Pair EUR/USD, returns USD/EUR
	 *   
	 *   WARN: inverse MAINTAINS the tradedCCY field
	 *   
	 * @return
	 */
	public Currency inverse(){
		Currency inverse = new Currency(this.rightCCY,this.leftCCY,this.nearTenor,this.farTenor);
		inverse.setTradedCCY(this.getTradedCCY());
		return inverse;
	}
	
	/**
	 * utility static method to verify tenors
	 * @param tenor
	 * @return
	 */
	protected static boolean isValidTenor(String tenor){
		/*if(tenor!=null){
			if(TENORSET.contains(tenor)||tenor.matches("\\d{8}")){
				return true;
			}
		}
		return false;*/
		
		boolean flag = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sdf.setLenient(false);
		Date date;
		try {
			date  = sdf.parse(tenor);	
			flag = true;
		} catch (Exception ignore) {
		
		}
		return flag;
	}


	@Override
	public int compareTo(Currency o2) {
		Currency o1 = this;
		if(o2==null){
			//throw new NullPointerException("compareTo invalid for null objects in Currency.compareTo");
			return 1; // prefer to say this object is always greater than a null?
		}
		int symbolComp = o1.getSymbol().compareTo(o2.getSymbol());
    	if(symbolComp==0){
    		int nearTenorComp = o1.getNearTenor().compareTo(o2.getNearTenor());
    		if(nearTenorComp==0){
    			if(o1.isSwap() && o2.isSwap()){
    				return o1.getFarTenor().compareTo(o2.getFarTenor());
    			}else{
    				return 0;
    			}
    		}else{
    			return nearTenorComp;
    		}
    	}else{
    		return symbolComp;
    	}
	}
	
}




