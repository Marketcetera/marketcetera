package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.math.BigDecimal;
import java.math.BigInteger;

/* $License$ */
/**
 * MX Bean interface for testing module MX bean interfaces
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
@DisplayName("JMX Testing module")
public interface JMXTestModuleMXBean {
    @DisplayName("primitive boolean type")
    boolean isPrimBoolean();

    @DisplayName("primitive boolean type")
    void setPrimBoolean(
            @DisplayName("primitive boolean type")
            boolean inPrimBoolean);

    @DisplayName("boolean type")
    Boolean getBoolean();

    @DisplayName("boolean type")
    void setBoolean(
            @DisplayName("boolean type")
            Boolean inBoolean);

    @DisplayName("primitive byte type")
    byte getPrimByte();

    @DisplayName("primitive byte type")
    void setPrimByte(
            @DisplayName("primitive byte type")
            byte inPrimByte);

    @DisplayName("byte type")
    Byte getByte();

    @DisplayName("byte type")
    void setByte(
            @DisplayName("byte type")
            Byte inByte);

    @DisplayName("primitive char type")
    char getPrimCharacter();

    @DisplayName("primitive char type")
    void setPrimCharacter(
            @DisplayName("primitive char type")
            char inPrimCharacter);

    @DisplayName("char type")
    Character getCharacter();

    @DisplayName("char type")
    void setCharacter(
            @DisplayName("char type")
            Character inCharacter);

    @DisplayName("primitive short type")
    short getPrimShort();

    @DisplayName("primitive short type")
    void setPrimShort(
            @DisplayName("primitive short type")
            short inPrimShort);

    @DisplayName("short type")
    Short getShort();

    @DisplayName("short type")
    void setShort(
            @DisplayName("short type")
            Short inShort);

    @DisplayName("primitive int type")
    int getPrimInt();

    @DisplayName("primitive int type")
    void setPrimInt(
            @DisplayName("primitive int type")
            int inPrimInt);

    @DisplayName("int type")
    Integer getInt();

    @DisplayName("int type")
    void setInt(
            @DisplayName("int type")
            Integer inInt);

    @DisplayName("primitive float type")
    float getPrimFloat();

    @DisplayName("primitive float type")
    void setPrimFloat(
            @DisplayName("primitive float type")
            float inPrimFloat);

    @DisplayName("float type")
    Float getFloat();

    @DisplayName("float type")
    void setFloat(
            @DisplayName("float type")
            Float inFloat);

    @DisplayName("primitive long type")
    long getPrimLong();

    @DisplayName("primitive long type")
    void setPrimLong(
            @DisplayName("primitive long type")
            long inPrimLong);

    @DisplayName("long type")
    Long getLong();

    @DisplayName("long type")
    void setLong(
            @DisplayName("long type")
            Long inLong);

    @DisplayName("primitive double type")
    double getPrimDouble();

    @DisplayName("primitive double type")
    void setPrimDouble(
            @DisplayName("primitive double type")
            double inPrimDouble);

    @DisplayName("double type")
    Double getDouble();

    @DisplayName("double type")
    void setDouble(
            @DisplayName("double type")
            Double inDouble);

    @DisplayName("string type")
    String getString();

    @DisplayName("string type")
    void setString(
            @DisplayName("string type")
            String inString);

    @DisplayName("big decimal type")
    BigDecimal getDecimal();

    @DisplayName("big decimal type")
    void setDecimal(
            @DisplayName("big decimal type")
            BigDecimal inDecimal);

    @DisplayName("big integer type")
    BigInteger getInteger();

    @DisplayName("big integer type")
    void setInteger(
            @DisplayName("big integer type")
            BigInteger inInteger);

    @DisplayName("file type")
    String getFile();

    @DisplayName("file type")
    void setFile(
            @DisplayName("file type")
            String inFile);

    @DisplayName("URL type")
    String getURL();

    @DisplayName("URL type")
    void setURL(
            @DisplayName("URL type")
            String inURL);

    @DisplayName("properties")
    String getProperties();

    @DisplayName("factory annotation")
    String getFactoryAnnotation();

    @DisplayName("factory annotation")
    void setFactoryAnnotation(
            @DisplayName("factory annotation")
            String inFactoryAnnotation);

}
