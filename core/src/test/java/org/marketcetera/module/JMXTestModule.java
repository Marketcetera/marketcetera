package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.Util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Properties;

/* $License$ */
/**
 * A module for testing JMX integration
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class JMXTestModule extends Module implements JMXTestModuleMXBean {
    public JMXTestModule(ModuleURN inURN) {
        super(inURN, false);
    }

    @Override
    protected void preStart() throws ModuleException {
    }

    @Override
    public void preStop() throws ModuleException {
    }

    @Override
    public boolean isPrimBoolean() {
        return mPrimBoolean;
    }

    @Override
    public void setPrimBoolean(boolean inPrimBoolean) {
        mPrimBoolean = inPrimBoolean;
    }

    @Override
    public Boolean getBoolean() {
        return mBoolean;
    }

    @Override
    public void setBoolean(Boolean inBoolean) {
        mBoolean = inBoolean;
    }

    @Override
    public byte getPrimByte() {
        return mPrimByte;
    }

    @Override
    public void setPrimByte(byte inPrimByte) {
        mPrimByte = inPrimByte;
    }

    @Override
    public Byte getByte() {
        return mByte;
    }

    @Override
    public void setByte(Byte inByte) {
        mByte = inByte;
    }

    @Override
    public char getPrimCharacter() {
        return mPrimCharacter;
    }

    @Override
    public void setPrimCharacter(char inPrimCharacter) {
        mPrimCharacter = inPrimCharacter;
    }

    @Override
    public Character getCharacter() {
        return mCharacter;
    }

    @Override
    public void setCharacter(Character inCharacter) {
        mCharacter = inCharacter;
    }

    @Override
    public short getPrimShort() {
        return mPrimShort;
    }

    @Override
    public void setPrimShort(short inPrimShort) {
        mPrimShort = inPrimShort;
    }

    @Override
    public Short getShort() {
        return mShort;
    }

    @Override
    public void setShort(Short inShort) {
        mShort = inShort;
    }

    @Override
    public int getPrimInt() {
        return mPrimInt;
    }

    @Override
    public void setPrimInt(int inPrimInt) {
        mPrimInt = inPrimInt;
    }

    @Override
    public Integer getInt() {
        return mInt;
    }

    @Override
    public void setInt(Integer inInt) {
        mInt = inInt;
    }

    @Override
    public float getPrimFloat() {
        return mPrimFloat;
    }

    @Override
    public void setPrimFloat(float inPrimFloat) {
        mPrimFloat = inPrimFloat;
    }

    @Override
    public Float getFloat() {
        return mFloat;
    }

    @Override
    public void setFloat(Float inFloat) {
        mFloat = inFloat;
    }

    @Override
    public long getPrimLong() {
        return mPrimLong;
    }

    @Override
    public void setPrimLong(long inPrimLong) {
        mPrimLong = inPrimLong;
    }

    @Override
    public Long getLong() {
        return mLong;
    }

    @Override
    public void setLong(Long inLong) {
        mLong = inLong;
    }

    @Override
    public double getPrimDouble() {
        return mPrimDouble;
    }

    @Override
    public void setPrimDouble(double inPrimDouble) {
        mPrimDouble = inPrimDouble;
    }

    @Override
    public Double getDouble() {
        return mDouble;
    }

    @Override
    public void setDouble(Double inDouble) {
        mDouble = inDouble;
    }

    @Override
    public String getString() {
        return mString;
    }

    @Override
    public void setString(String inString) {
        mString = inString;
    }

    @Override
    public BigDecimal getDecimal() {
        return mDecimal;
    }

    @Override
    public void setDecimal(BigDecimal inDecimal) {
        mDecimal = inDecimal;
    }

    @Override
    public BigInteger getInteger() {
        return mInteger;
    }

    @Override
    public void setInteger(BigInteger inInteger) {
        mInteger = inInteger;
    }

    @Override
    public String getFile() {
        return mFile;
    }

    @Override
    public void setFile(String inFile) {
        mFile = inFile;
    }

    @Override
    public String getURL() {
        return mURL;
    }

    @Override
    public void setURL(String inURL) {
        mURL = inURL;
    }
    
    @Override
    public String getProperties() {
        return Util.propertiesToString(mProperties);
    }

    public void setProperties(Properties inProperties) {
        mProperties = inProperties;
    }

    @Override
    public String getFactoryAnnotation() {
        return mFactoryAnnotation;
    }

    @Override
    public void setFactoryAnnotation(String inFactoryAnnotation) {
        //for testing exceptions from mxbean method invocations.
        if("error".equals(inFactoryAnnotation)) {
            throw new IllegalArgumentException(inFactoryAnnotation);
        }
        mFactoryAnnotation = inFactoryAnnotation;
    }

    private boolean mPrimBoolean;
    private Boolean mBoolean;
    private byte mPrimByte;
    private Byte mByte;
    private char mPrimCharacter;
    private Character mCharacter;
    private short mPrimShort;
    private Short mShort;
    private int mPrimInt;
    private Integer mInt;
    private float mPrimFloat;
    private Float mFloat;
    private long mPrimLong;
    private Long mLong;
    private double mPrimDouble;
    private Double mDouble;
    private String mString;
    private BigDecimal mDecimal;
    private BigInteger mInteger;
    private String mFile;
    private String mURL;
    private Properties mProperties;
    private String mFactoryAnnotation;
}
